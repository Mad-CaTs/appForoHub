package forum.latam.alura.presentation.dto.roles;

import jakarta.validation.constraints.NotBlank;

public record SingleRole(
        @NotBlank String name
) {}
