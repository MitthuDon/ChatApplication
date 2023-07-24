package com.chatapp.chatapp.event;


import com.chatapp.chatapp.model.ChatMessage;
import com.chatapp.chatapp.model.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

import org.springframework.web.socket.messaging.SessionDisconnectEvent;


@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {
    @Autowired
    private final SimpMessageSendingOperations messageTemplate;

    @EventListener
    public void disconnectedUser(SessionDisconnectEvent disconnectEvent) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(disconnectEvent.getMessage());
        String user = headerAccessor.getSessionAttributes().get("username").toString();
        if (user != null) {
            log.info("User Disconnected: {}", user);
            var chatMessage = ChatMessage.builder()
                    .sender(user)
                    .type(MessageType.LEFT)
                    .build();
            messageTemplate.convertAndSend("/topic/public", chatMessage);
        }
    }
}
