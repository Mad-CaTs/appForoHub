package forum.latam.alura.presentation.dto.forums;

import forum.latam.alura.presentation.dto.messages.SingleMessage;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record ForumWithReplies(
        @NotBlank String title,
        @NotBlank String description,
        @NotBlank String createdAt,
        @NotBlank String ownerId,
        List<SingleMessage> messages
) {
}
