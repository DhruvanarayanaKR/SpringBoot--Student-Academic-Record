package org.example.controller;

import jakarta.servlet.http.HttpSession;
import org.example.dto.MessageRequest;
import org.example.model.Message;
import org.example.model.User;
import org.example.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(
            @RequestBody MessageRequest request,
            HttpSession session) {

        User mentor = (User) session.getAttribute("user");

        if (mentor == null || !"MENTOR".equalsIgnoreCase(mentor.getRole())) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        messageService.sendMessage(
                mentor.getUsn(),
                request.getStudentUsn(),
                request.getMessage()
        );

        return ResponseEntity.ok("Message sent");
    }

    @GetMapping("/student")
    public ResponseEntity<?> getStudentMessages(HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null || !"STUDENT".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        List<Message> messages = messageService.getStudentMessages(user.getUsn());

        return ResponseEntity.ok(messages);
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {

        messageService.markAsRead(id);

        return ResponseEntity.ok("Marked as read");
    }
}