package forum.latam.alura.presentation.dto.forums;

import jakarta.validation.constraints.NotBlank;

public record SingleForum(
        @NotBlank String title,
        @NotBlank String description,
        @NotBlank String createdAt,
        @NotBlank String ownerId
) {
}
