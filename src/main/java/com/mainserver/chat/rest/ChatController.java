package com.mainserver.chat.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.MessageFormat;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final SessionRegistry sessionRegistry;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public ChatController(SessionRegistry sessionRegistry, SimpMessagingTemplate simpMessagingTemplate) {
        this.sessionRegistry = sessionRegistry;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @PostMapping(value = "/send", consumes = "text/plain")
    public ResponseEntity<?> send(@RequestBody String message) {
        User loggedInUser = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        sessionRegistry.getAllPrincipals()
                .stream()
                .map(p -> (User) p)
                .filter(user -> !user.getUsername().equals(loggedInUser.getUsername()))
                .forEach(user -> {
                    simpMessagingTemplate.convertAndSendToUser(
                            user.getUsername(),
                            "/message",
                            MessageFormat.format("{0}: {1}",
                                    loggedInUser.getUsername(),
                                    message
                            )
                    );
                });

        return ResponseEntity.ok().build();
    }

}
