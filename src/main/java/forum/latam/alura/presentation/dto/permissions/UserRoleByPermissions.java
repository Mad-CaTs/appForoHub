package forum.latam.alura.presentation.dto.permissions;

import forum.latam.alura.infrastructure.helpers.RoleEnum;
import jakarta.validation.constraints.NotBlank;


import java.util.Set;


public record UserRoleByPermissions (
        @NotBlank String username,
        String roleName,
        String permissionName
){
}
