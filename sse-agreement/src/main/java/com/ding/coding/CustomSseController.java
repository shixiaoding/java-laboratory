package com.ding.coding;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.WebAsyncTask;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author shiding
 */
@RestController
@RequestMapping("/custom-sse")
public class CustomSseController {

    /**
     * 基于web 异步请求 实现 SSE
     * 异步学习：【WEB 系列】异步请求知识点与使用姿势小结
     * https://mp.weixin.qq.com/s?__biz=MzU3MTAzNTMzMQ==&mid=2247484567&idx=1&sn=de01a82e80c748aa6bb9fde8a51cae5b
     */

    /**
     * 新建一个容器，保存连接，用于输出返回
     */
    private Map<String, PrintWriter> responseMap = new ConcurrentHashMap();

    private void writeData(String id, String msg, boolean over) {
        PrintWriter writer = responseMap.get(id);
        if (writer == null) {
            return;
        }

        writer.println(msg);
        writer.flush();
        if (over) {
            responseMap.remove(id);
        }
    }

    // 推送
    @ResponseBody
    @GetMapping(path = "subscribe")
    public WebAsyncTask<Void> subscribe(String id, HttpServletResponse response) {

        Callable<Void> callable = () -> {
            response.setHeader("Content-Type", "text/event-stream;charset=UTF-8");
            responseMap.put(id, response.getWriter());
            writeData(id, "订阅成功", false);
            while (true) {
                Thread.sleep(1000);
                if (!responseMap.containsKey(id)) {
                    break;
                }
            }
            return null;
        };

        // 采用WebAsyncTask 返回 这样可以处理超时和错误 同时也可以指定使用的Excutor名称
        WebAsyncTask<Void> webAsyncTask = new WebAsyncTask<>(30000, callable);
        // 注意：onCompletion表示完成，不管你是否超时、是否抛出异常，这个函数都会执行的
        webAsyncTask.onCompletion(() -> System.out.println("程序[正常执行]完成的回调"));

        // 这两个返回的内容，最终都会放进response里面去===========
        webAsyncTask.onTimeout(() -> {
            responseMap.remove(id);
            System.out.println("超时了!!!");
            return null;
        });
        // 备注：这个是Spring5新增的
        webAsyncTask.onError(() -> {
            System.out.println("出现异常!!!");
            return null;
        });


        return webAsyncTask;
    }

    @ResponseBody
    @GetMapping(path = "push")
    public String pushData(String id, String content) throws IOException {
        writeData(id, content, false);
        return "over!";
    }

    @ResponseBody
    @GetMapping(path = "over")
    public String over(String id) throws IOException {
        writeData(id, "over", true);
        return "over!";
    }
}
