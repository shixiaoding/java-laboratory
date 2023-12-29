package com.ding.coding;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelAnalysisException;
import com.alibaba.excel.metadata.CellData;
import com.ding.coding.dto.ImportDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author shiding
 */
@RestController
@RequestMapping("/sse-import")
public class SseImportController {

    private static Logger log = LoggerFactory.getLogger(SseController.class);

    public static Map<String, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();

    public static Map<String, Integer> importCountMap = new ConcurrentHashMap<>();

    @PostMapping(value = "/import")
    public Boolean createImportTask(@RequestParam("file") MultipartFile file,
                                    @RequestParam("code") String code) throws IOException {
        Integer totalCount = getTotalCount(file);
        handle(file, code, totalCount);
        return Boolean.TRUE;
    }

    @GetMapping(value = "/subscribe",
            produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public SseEmitter connenct(String code) {
        // 超时时间设置为1小时
        SseEmitter sseEmitter = new SseEmitter(3600_000L);
        sseEmitterMap.put(code, sseEmitter);
        sseEmitter.onTimeout(() -> sseEmitterMap.remove(code));
        sseEmitter.onCompletion(() -> System.out.println("完成！！！"));
        log.info("连接建立成功");
        return sseEmitter;
    }

    public void sendMsg(String code, String process) throws IOException {
        SseEmitter sseEmitter = sseEmitterMap.get(code);
        if (sseEmitter != null) {
            sseEmitter.send(process);
            log.info("发送消息：" + process);
        }
    }

    public void disconnect(String code) throws IOException {
        SseEmitter sseEmitter = sseEmitterMap.get(code);
        if (sseEmitter != null) {
            sseEmitter.complete();
            sseEmitterMap.remove(code);
            log.info("关闭连接：" + code);
        }
    }


    @Async
    public void handle(MultipartFile file, String code, Integer totalCount) throws IOException {
        log.info("开始处理导入任务，taskId={}", code);
        try {
            EasyExcel.read(file.getInputStream(), ImportDTO.class, new AnalysisEventListener<ImportDTO>() {
                /**
                 * 单次缓存的数据量
                 */
                public static final int BATCH_COUNT = 2;

                /**
                 *临时存储
                 */
                private List<ImportDTO> dataList = new ArrayList<>(BATCH_COUNT);

                @Override
                public void invoke(ImportDTO data, AnalysisContext context) {

                    dataList.add(data);
                    if (dataList.size() >= BATCH_COUNT) {
                        try {
                            saveData(dataList, totalCount);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        // 存储完成清理 list
                        dataList.clear();
                    }
                }

                private void saveData(List<ImportDTO> dataList, Integer totalCount) throws IOException {
                    Integer sum = importCountMap.getOrDefault(code, 0) + dataList.size();
                    BigDecimal processValue = new BigDecimal(sum).divide(new BigDecimal(totalCount), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
                    importCountMap.put(code, sum);
                    log.info("插入成功！！！");
                    sendMsg(code, processValue.toString());
                    try {
                        System.out.println("开始睡眠");
                        Thread.sleep(1000); // 睡眠5秒钟
                        System.out.println("结束睡眠");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                    if (dataList.size() > 0) {
                        try {
                            saveData(dataList, totalCount);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    System.out.println("都读完完成了");
                }
            }).sheet().doRead();

        } catch (Exception e) {

        } finally {
            importCountMap.remove(code);
            // 可以做一些异常处理
            disconnect(code);
        }
    }


    private int getTotalCount(MultipartFile file) {
        LongAdder adder = new LongAdder();
        try {
            EasyExcel.read(file.getInputStream(), ImportDTO.class, new AnalysisEventListener<ImportDTO>() {
                @Override
                public void invokeHead(Map<Integer, CellData> headMap, AnalysisContext context) {
                    StringBuilder headError = new StringBuilder("表头错误，错误列：");
                    Set<String> headNameSet = new HashSet<>();
//                    Field[] fields = getExcelHeadClazz().getDeclaredFields();

                    ParameterizedType parameterizedType = (ParameterizedType) this.getClass().getGenericSuperclass();
                    Field[] fields = parameterizedType.getActualTypeArguments()[0].getClass().getDeclaredFields();

                    Field f;
                    for (Field field : fields) {
                        f = field;
                        f.setAccessible(true);
                        if (f.isAnnotationPresent(ExcelProperty.class)) {
                            ExcelProperty annotation = f.getAnnotation(ExcelProperty.class);
                            String headName = annotation.value()[0];
                            headNameSet.add(headName);
                        }
                    }
                    boolean flag = false;
                    for (CellData<?> cellData : headMap.values()) {
                        String headName = cellData.getStringValue();
                        if (!headNameSet.contains(headName)) {
                            headError.append(headName).append("，");
                            flag = true;
                        }
                    }
                    if (flag) {
                        throw new ExcelAnalysisException(headError.toString());
                    }
                }

                @Override
                public void invoke(ImportDTO data, AnalysisContext context) {
                    adder.increment();
                }

                @Override
                public void onException(Exception exception, AnalysisContext context) {
                    log.error("解析Excel模板失败，失败原因：");
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {
                }
            }).doReadAll();
        } catch (IOException | ExcelAnalysisException e) {
            log.error("解析Excel模板失败，失败原因：", e);
        }
        return adder.intValue();
    }
}
