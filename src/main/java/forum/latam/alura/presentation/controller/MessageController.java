package forum.latam.alura.presentation.controller;

import forum.latam.alura.domain.entity.Message;
import forum.latam.alura.infrastructure.services.MessageService;
import forum.latam.alura.presentation.dto.messages.MessageWithForum;
import forum.latam.alura.presentation.dto.messages.SingleMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/messages")
public class MessageController implements BaseApiController<Message, Integer> {


    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    private final MessageService messageService;


    @GetMapping
    @Override
    public ResponseEntity<List<SingleMessage>> getAll() {
        List<SingleMessage> messages = messageService.getAllMessages();
        return ResponseEntity.ok(messages);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Integer id) {
        Optional<Message> messageOptional = messageService.getById(id);

        if (messageOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Message not found");
        }

        Message message = messageOptional.get();

        List<Message> replies = messageService.findbyParentMessage(id);
        message.setReplies(replies.stream()
                .filter(reply -> reply.getParentMessage() != null && reply.getParentMessage().getId() == id)
                .collect(Collectors.toList()));

        SingleMessage singleMessage = messageService.convertToSingleMessage(message, new HashSet<>());
        return ResponseEntity.ok(singleMessage);
    }




    @PostMapping("/create-message")
    public ResponseEntity<?> createMessage(@RequestBody MessageWithForum singleMessage) {
        try {
            Message newMessage = messageService.createForumMessage(singleMessage);
            return ResponseEntity.ok(newMessage);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error creating message: " + e.getMessage());
        }
    }

    @PostMapping("/create-reply")
    public ResponseEntity<?> createReply(@RequestBody MessageWithForum singleMessage) {
        try {
            Message newMessage = messageService.replyToMessage(singleMessage);
            return ResponseEntity.ok(newMessage);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error creating message: " + e.getMessage());
        }
    }

    @PutMapping("/upt-message/{id}")
    public ResponseEntity<?> updateMessage(@PathVariable Integer id, @RequestBody Message message, Authentication authentication) {
        try {
            messageService.updateMessage(id, message, authentication);
            return ResponseEntity.ok("Message successfully updated");
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body("You don't have permission to update this message: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error updating message with id " + id + ": " + e.getMessage());
        }
    }

    @DeleteMapping("/delete-message/{id}")
    public ResponseEntity<?> deleteMessage(@PathVariable Integer id, Authentication authentication) {
        try {
            messageService.deleteMessage(id, authentication);
            return ResponseEntity.ok("Message successfully deleted");
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body("You don't have permission to delete this message: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error deleting message with id " + id + ": " + e.getMessage());
        }
    }


    @Override
    @PostMapping
    @Deprecated
    public ResponseEntity<?> save(Message entity) {
        return ResponseEntity.status(405).body("This method is not found.");
    }

    @Override
    @PutMapping
    @Deprecated
    public ResponseEntity<?> update(Integer integer, Message entity) {
        return ResponseEntity.status(405).body("This method is not found.");
    }

    @Override
    @DeleteMapping
    @Deprecated
    public ResponseEntity<?> delete(Integer integer) {
        return ResponseEntity.status(405).body("This method is not found.");
    }
}
