package forum.latam.alura.presentation.dto.permissions;

import jakarta.validation.constraints.NotBlank;

public record SinglePermission(
        @NotBlank String name
) {}
