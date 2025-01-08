package forum.latam.alura.infrastructure.services;

import forum.latam.alura.domain.entity.Forum;
import forum.latam.alura.domain.entity.Message;
import forum.latam.alura.domain.entity.User;
import forum.latam.alura.domain.repository.ForumRepository;
import forum.latam.alura.domain.repository.MessageRepository;
import forum.latam.alura.domain.repository.UserRepository;
import forum.latam.alura.presentation.dto.messages.MessageWithForum;
import forum.latam.alura.presentation.dto.messages.SingleMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MessageService extends GenericService<Message, Integer> {

    @Autowired
    public MessageService(MessageRepository repository, ForumRepository forumRepository, UserRepository userRepository) {
        super(repository);
        this.messageRepository = repository;
        this.forumRepository = forumRepository;
        this.userRepository = userRepository;
    }

    private final MessageRepository messageRepository;
    private final ForumRepository forumRepository;
    private final UserRepository userRepository;




    public Message createForumMessage(MessageWithForum messageWithForum) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User author = userRepository.findByname(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (messageWithForum.parentMessageId() != null) {
            throw new IllegalArgumentException("A forum message cannot have a parent message");
        }

        Forum forum = forumRepository.findById(messageWithForum.forumId())
                .orElseThrow(() -> new IllegalArgumentException("Forum not found"));

        Message message = Message.builder()
                .content(messageWithForum.content())
                .createdAt(new Date())
                .author(author)
                .forum(forum)
                .parentMessage(null)
                .build();

        return messageRepository.save(message);
    }

    public Message replyToMessage(MessageWithForum messageWithForum) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User author = userRepository.findByname(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (messageWithForum.parentMessageId() == null) {
            throw new IllegalArgumentException("A reply must have a parent message ID");
        }

        Message parentMessage = messageRepository.findById(messageWithForum.parentMessageId())
                .orElseThrow(() -> new IllegalArgumentException("Parent message not found"));

        Forum forum = parentMessage.getForum();

        if (messageWithForum.forumId() != null && forum.getId() != messageWithForum.forumId()) {
            throw new IllegalArgumentException("Inconsistent forum ID for reply");
        }
        Message message = Message.builder()
                .content(messageWithForum.content())
                .createdAt(new Date())
                .author(author)
                .forum(forum)
                .parentMessage(parentMessage)
                .build();

        return messageRepository.save(message);
    }



    public List<SingleMessage> getAllMessages() {
        List<Message> messages = messageRepository.findAll();
        List<SingleMessage> singleMessages = new ArrayList<>();
        for (Message message : messages) {
            singleMessages.add(convertToSingleMessage(message, new HashSet<>()));
        }
        return singleMessages;
    }

    public SingleMessage convertToSingleMessage(Message message, Set<Integer> processedMessages) {
        if (processedMessages.contains(message.getId())) {
            return null;
        }

        processedMessages.add(message.getId());

        List<SingleMessage> replies = new ArrayList<>();
        for (Message reply : message.getReplies()) {
            SingleMessage singleReply = convertToSingleMessage(reply, processedMessages);
            if (singleReply != null) {
                replies.add(singleReply);
            }
        }

        return SingleMessage.builder()
                .content(message.getContent())
                .authorUsername(message.getAuthor().getUsername())
                .createdAt(message.getCreatedAt())
                .replies(replies)
                .parentMessageId(message.getParentMessage() != null ? message.getParentMessage().getId() : null)
                .build();
    }

    public List<Message> findbyParentMessage(Integer parentMessageId) {
        return messageRepository.findByParentMessageId(parentMessageId);
    }


    public void updateMessage(Integer id, Message message, Authentication authentication) {
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByname(currentUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + currentUsername));

        Message existingMessage = messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        if (!existingMessage.getAuthor().getUsername().equals(currentUsername) &&
                !currentUser.getRoles().stream()
                        .anyMatch(role -> role.getName().name().equals("ADMIN") || role.getName().name().equals("DEVELOPER"))) {
            throw new SecurityException("You don't have permission to update the message of other users");
        }

        if (message.getContent() != null) {
            existingMessage.setContent(message.getContent());
        }

        messageRepository.save(existingMessage);
    }

    public void deleteMessage(Integer id, Authentication authentication) {
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByname(currentUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + currentUsername));

        Message existingMessage = messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        if (!existingMessage.getAuthor().getUsername().equals(currentUsername) &&
                !currentUser.getRoles().stream()
                        .anyMatch(role -> role.getName().name().equals("ADMIN") || role.getName().name().equals("DEVELOPER"))) {
            throw new SecurityException("You don't have permission to delete the message of other users");
        }

        messageRepository.deleteById(id);
    }


}
