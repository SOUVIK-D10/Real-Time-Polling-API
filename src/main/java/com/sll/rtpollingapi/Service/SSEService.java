package com.sll.rtpollingapi.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.sll.rtpollingapi.DTO.PollResponseDTO;

@Service
public class SSEService {
    private final Map<Integer,List<SseEmitter>> pollEmitters = new ConcurrentHashMap<>();
    
    public void removeEmitter(int pollId,SseEmitter emitter){
        List<SseEmitter> emitters = pollEmitters.get(pollId);
        if(emitters == null) return;
        emitters.remove(emitter);
    }
    public SseEmitter subscribe(int pollId){
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        pollEmitters.computeIfAbsent(pollId, k -> new CopyOnWriteArrayList<>()).add(emitter);
        emitter.onCompletion(() -> removeEmitter(pollId,emitter));
        emitter.onTimeout(() -> removeEmitter(pollId,emitter));
        emitter.onError((e) -> removeEmitter(pollId,emitter));
        return emitter;
    }
    public void broadcast(int pollId, PollResponseDTO dto){
        List<SseEmitter> emitters = pollEmitters.getOrDefault(pollId, new CopyOnWriteArrayList<>());
        for(SseEmitter emitter:emitters){
            try {
                emitter.send(SseEmitter.event()
                                        .name("poll-update")
                                        .data(dto)
                                        );
            } catch (IOException e) {
                emitter.completeWithError(e);
                removeEmitter(pollId, emitter);
            }
        }
    }
    public void unicast(int pollId,SseEmitter emitter,PollResponseDTO dto){
        try {
                emitter.send(SseEmitter.event()
                                        .name("poll-current-status")
                                        .data(dto)
                                        );
            } catch (IOException e) {
                emitter.completeWithError(e);
                removeEmitter(pollId, emitter);
        }
    }
}
