package forum.latam.alura.application.seeds;

import forum.latam.alura.domain.entity.Forum;
import forum.latam.alura.domain.entity.User;
import forum.latam.alura.domain.repository.ForumRepository;
import forum.latam.alura.domain.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ForumSeed {

    CommandLineRunner initForums(UserRepository userRepository, ForumRepository forumRepository) {
        return args -> {
            if (forumRepository.count() > 0) {
                System.out.println("Forums already initialized. Skipping seed.");
                return;
            }

            User user = userRepository.findByname("Mad_CaTs")
                    .orElseThrow(() -> new RuntimeException("User not found in the database"));

            Forum forum1 = Forum.builder()
                    .title("What is the best way to create a Java ArrayList?")
                    .description("I've been looking for a way to create an ArrayList in Java but haven't found a clear solution.")
                    .createdAt(new java.util.Date())
                    .owner(user)
                    .build();

            Forum forum2 = Forum.builder()
                    .title("How can I create an AI with Java?")
                    .description("I don't know where to start.")
                    .createdAt(new java.util.Date())
                    .owner(user)
                    .build();

            forumRepository.saveAll(List.of(forum1, forum2));
            System.out.println("Forums initialized successfully.");
        };
    }
}
