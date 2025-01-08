package forum.latam.alura.infrastructure.services;

import forum.latam.alura.domain.entity.Forum;
import forum.latam.alura.domain.entity.Message;
import forum.latam.alura.domain.entity.User;
import forum.latam.alura.domain.repository.ForumRepository;
import forum.latam.alura.domain.repository.MessageRepository;
import forum.latam.alura.domain.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;



@Service
public class ForumService extends GenericService<Forum, Integer> {

    @Autowired
    public ForumService(ForumRepository repository, MessageRepository messageRepository, UserRepository userRepository) {
        super(repository);
        this.forumRepository = repository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    private final ForumRepository forumRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;





    public void createForum(Forum forum, Authentication authentication) {
        String username = authentication.getName();

        User owner = userRepository.findByname(username)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        forum.setOwner(owner);

        forumRepository.save(forum);
    }

    public void updateForum(Integer id, Forum forum, Authentication authentication) {
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByname(currentUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + currentUsername));

        Forum existingForum = forumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Forum not found"));

        if (currentUser.getId() != existingForum.getOwner().getId() &&
                !currentUser.getRoles().stream()
                        .anyMatch(role -> role.getName().name().equals("ADMIN") || role.getName().name().equals("DEVELOPER"))) {
            throw new SecurityException("You don't have permission to update forums owned by other users.");
        }

        if (forum.getTitle() != null) {
            existingForum.setTitle(forum.getTitle());
        }
        if (forum.getDescription() != null) {
            existingForum.setDescription(forum.getDescription());
        }

        forumRepository.save(existingForum);
    }


    public void deleteForum(Integer id, Authentication authentication) {
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByname(currentUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + currentUsername));

        Forum existingForum = forumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Forum not found"));

        if (!currentUser.getRoles().stream()
                .anyMatch(role -> role.getName().name().equals("ADMIN") || role.getName().name().equals("DEVELOPER"))
                && !existingForum.getOwner().getUsername().equals(currentUsername)) {
            throw new SecurityException("You do not have permission to delete this forum");
        }

        forumRepository.deleteById(id);
    }








}

