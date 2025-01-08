package forum.latam.alura.application.seeds;

import forum.latam.alura.domain.entity.Forum;
import forum.latam.alura.domain.entity.Message;
import forum.latam.alura.domain.entity.User;
import forum.latam.alura.domain.repository.ForumRepository;
import forum.latam.alura.domain.repository.MessageRepository;
import forum.latam.alura.domain.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MessageSeed {

    CommandLineRunner initMessages(UserRepository userRepository, ForumRepository forumRepository, MessageRepository messageRepository) {
        return args -> {
            if (messageRepository.count() > 0) {
                System.out.println("Messages already initialized. Skipping seed.");
                return;
            }

            User user = userRepository.findByname("Mad_CaTs")
                    .orElseThrow(() -> new RuntimeException("User with name 'Markus' not found in the database"));

            Forum forum = forumRepository.findByTitle("What is the best way to create a Java ArrayList?")
                    .orElseThrow(() -> new RuntimeException("Forum with the specified title not found in the database"));

            Message message1 = Message.builder()
                    .content("Welcome to the first forum!")
                    .createdAt(new java.util.Date())
                    .author(user)
                    .forum(forum)
                    .build();

            Message message2 = Message.builder()
                    .content("Looking forward to great discussions.")
                    .createdAt(new java.util.Date())
                    .author(user)
                    .forum(forum)
                    .build();

            messageRepository.saveAll(List.of(message1, message2));
            System.out.println("Messages initialized successfully.");
        };
    }
}
