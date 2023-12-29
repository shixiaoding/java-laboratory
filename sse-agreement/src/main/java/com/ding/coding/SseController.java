package com.ding.coding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author shiding
 */

@RestController
@RequestMapping("/sse")
public class SseController {

    private static Logger log = LoggerFactory.getLogger(SseController.class);


    public static Map<String, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();

    @GetMapping(path = "subscribe")
    public SseEmitter push(String id) {
        // 超时时间设置为1小时
        SseEmitter sseEmitter = new SseEmitter(3600_000L);
        sseEmitterMap.put(id, sseEmitter);
        sseEmitter.onTimeout(() -> sseEmitterMap.remove(id));
        sseEmitter.onCompletion(() -> System.out.println("完成！！！"));
        return sseEmitter;
    }

    @GetMapping(path = "push")
    public String push(String id, String content) throws IOException {
        SseEmitter sseEmitter = sseEmitterMap.get(id);
        if (sseEmitter != null) {
            sseEmitter.send(content);
        }
        return "over";
    }

    @GetMapping(path = "over")
    public String over(String id) {
        SseEmitter sseEmitter = sseEmitterMap.get(id);
        if (sseEmitter != null) {
            sseEmitter.complete();
            sseEmitterMap.remove(id);
        }
        return "over";
    }

}

