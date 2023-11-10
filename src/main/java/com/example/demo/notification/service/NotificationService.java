package com.example.demo.notification.service;

import com.example.demo.member.entity.Member;
import com.example.demo.notification.Entity.Notification;
import com.example.demo.notification.Entity.NotificationType;
import com.example.demo.notification.repository.EmitterRepository;
import com.example.demo.notification.repository.EmitterRepositoryImpl;
import com.example.demo.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final EmitterRepository emitterRepository = new EmitterRepositoryImpl();
    private final NotificationRepository notificationRepository;

    // 1시간동안 연결
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    public SseEmitter subscribe(Long memberId, String lastEventId) {
        String emitterId = memberId + "_" + System.currentTimeMillis();
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

        // SSE 연결 후, 데이터가 전송되지 않았는데 SseEmitter 의 유효시간이 끝나면 503 에러 발생
        // 최초 연결시 더미 데이터 전송
        sendToClient(emitter, emitterId, "EventStream Created. [memberId=" + memberId + "]");

        // 저장된 데이터 캐시에서 유실된 데이터들을 다시 전송
        if (!lastEventId.isEmpty()) {
            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithByMemberId(String.valueOf(memberId));
            events.entrySet().stream()
                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                    .forEach(entry -> sendToClient(emitter, entry.getKey(), entry.getValue()));
        }

        return emitter;
    }

    public void send(Member receiver, NotificationType notificationType, String content, String url, URL imageUrl) {
        Notification notification = notificationRepository.save(new Notification(receiver, notificationType, content, url, imageUrl));
        String memberId = String.valueOf(receiver.getId());

        Map<String, SseEmitter> sseEmitters = emitterRepository.findAllEmitterStartWithByMemberId(memberId);
        sseEmitters.forEach(
                (key, emitter) -> {
                    emitterRepository.saveEventCache(key, notification);
                    sendToClient(emitter, key, notification.getContent());
                }
        );
    }

    private void sendToClient(SseEmitter emitter, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(emitterId)
                    .name("sse")
                    .data(data));
//            log.info(emitterId+"-emitter has been sent and completed");
        } catch (IOException exception) {
            log.error("Unable to emit");
            emitter.completeWithError(exception);
            emitterRepository.deleteById(emitterId);
        }
    }
}
