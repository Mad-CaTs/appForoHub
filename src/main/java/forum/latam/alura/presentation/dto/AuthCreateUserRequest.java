package forum.latam.alura.presentation.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AuthCreateUserRequest(@NotBlank String username,
                                    @NotBlank String password,
                                    @NotBlank String name,
                                    @NotBlank String email,
                                    @Valid AuthCreateRoleRequest roleRequest) {
}
