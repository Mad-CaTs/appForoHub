package forum.latam.alura.presentation.dto.messages;

import jakarta.validation.constraints.NotBlank;

import java.util.Date;
import java.util.List;

public record MessageWithForum(
        @NotBlank String content,
        @NotBlank String authorUsername,
        @NotBlank Date createdAt,
        @NotBlank Integer forumId,
        List<SingleMessage> replies,
        Integer parentMessageId
) {
}
