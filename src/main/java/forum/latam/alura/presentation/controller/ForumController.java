package forum.latam.alura.presentation.controller;

import forum.latam.alura.domain.entity.Forum;

import forum.latam.alura.infrastructure.services.ForumService;
import forum.latam.alura.presentation.dto.forums.ForumWithReplies;
import forum.latam.alura.presentation.dto.forums.SingleForum;
import forum.latam.alura.presentation.dto.messages.SingleMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/api/forums")
@RestController
public class ForumController implements BaseApiController<Forum, Integer> {

    @Autowired
    public ForumController(ForumService forumService) {
        this.forumService = forumService;
    }

    private final ForumService forumService;


    @GetMapping("/single")
    public ResponseEntity<?> getSingleForums() {
        List<Forum> forums = forumService.getAll();
        List<SingleForum> dtos = forums.stream()
                .map(forum ->
                        new SingleForum(forum.getTitle(),
                                forum.getDescription(),
                                forum.getCreatedAt().toString(),
                                Integer.toString(forum.getOwner().getId())
                            )
                )
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @Override
    @GetMapping
    public ResponseEntity<?> getAll() {
        List<Forum> forums = forumService.getAll();
        List<ForumWithReplies> forumDtos = forums.stream()
                .map(forum -> new ForumWithReplies(
                        forum.getTitle(),
                        forum.getDescription(),
                        forum.getCreatedAt().toString(),
                        Integer.toString(forum.getOwner().getId()),
                        forum.getMessages().stream()
                                .map(message -> new SingleMessage(
                                        message.getContent(),
                                        message.getAuthor().getUsername(),
                                        message.getCreatedAt(),
                                        message.getReplies().stream()
                                                .map(reply -> new SingleMessage(
                                                        reply.getContent(),
                                                        reply.getAuthor().getUsername(),
                                                        reply.getCreatedAt(),
                                                        null,
                                                        message.getId()
                                                )).collect(Collectors.toList()),
                                        null
                                ))
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(forumDtos);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Integer id) {
        Forum forum = forumService.getById(id).orElse(null);
        if (forum == null) {
            return ResponseEntity.notFound().build();
        }

        ForumWithReplies forumDto = new ForumWithReplies(
                forum.getTitle(),
                forum.getDescription(),
                forum.getCreatedAt().toString(),
                Integer.toString(forum.getOwner().getId()),
                forum.getMessages().stream()
                        .map(message -> new SingleMessage(
                                message.getContent(),
                                message.getAuthor().getUsername(),
                                message.getCreatedAt(),
                                message.getReplies().stream()
                                        .map(reply -> new SingleMessage(
                                                reply.getContent(),
                                                reply.getAuthor().getUsername(),
                                                reply.getCreatedAt(),
                                                null,
                                                message.getId()
                                        )).collect(Collectors.toList()),
                                null
                        ))
                        .collect(Collectors.toList())
        );

        return ResponseEntity.ok(forumDto);
    }


    @Override
    @Deprecated
    @PostMapping
    public ResponseEntity<?> save(Forum entity) {
        return ResponseEntity.status(405).body("This method not be created yet.");
    }


    @PostMapping("/createforum")
    public ResponseEntity<?> saveForum(@RequestBody Forum entity, Authentication authentication) {
        forumService.createForum(entity, authentication);
        return ResponseEntity.ok("Forum created successfully.");
    }


    @Override
    @PutMapping("/{id}")
    @Deprecated
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody Forum entity) {
        return ResponseEntity.status(405).body("This method is not found.");
    }

    @Override
    @DeleteMapping("/{id}")
    @Deprecated
    public ResponseEntity<?> delete(Integer id) {
        return ResponseEntity.status(405).body("This method is not found.");
    }

    @PutMapping("/update-forum/{id}")
    public ResponseEntity<?> updateForum(@PathVariable Integer id, @RequestBody Forum updatedForum, Authentication authentication) {
        try {
            forumService.updateForum(id, updatedForum, authentication);
            return ResponseEntity.ok("Forum updated successfully.");
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body("You don't have permission to update this forum: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error updating forum with id " + id + ": " + e.getMessage());
        }
    }



    @DeleteMapping("/delete-forum/{id}")
    public ResponseEntity<?> deleteForum(@PathVariable Integer id, Authentication authentication) {
        try {
            forumService.deleteForum(id, authentication);
            return ResponseEntity.ok("Forum deleted successfully.");
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body("You don't have permission to delete this forum: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid forum id: " + id);
        }
    }

}
