package forum.latam.alura.presentation.dto.roles;

import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public record RolewithPermissions(
        @NotBlank String name,
        @NotBlank Set<String> permissions
) {
}
