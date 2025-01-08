package forum.latam.alura.application.seeds;

import forum.latam.alura.domain.entity.PermissionEntity;
import forum.latam.alura.domain.entity.Role;
import forum.latam.alura.domain.repository.PermissionRepository;
import forum.latam.alura.domain.repository.RoleRepository;
import forum.latam.alura.infrastructure.helpers.RoleEnum;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;


@Component
public class RoleSeed {

    CommandLineRunner initRoles(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        return args -> {
            if (roleRepository.count() > 0) {
                System.out.println("Roles already initialized. Skipping seed.");
                return;
            }

            PermissionEntity createPermission = permissionRepository.findByName("CREATE")
                    .orElseThrow(() -> new RuntimeException("Permission CREATE not found"));
            PermissionEntity readPermission = permissionRepository.findByName("READ")
                    .orElseThrow(() -> new RuntimeException("Permission READ not found"));
            PermissionEntity updatePermission = permissionRepository.findByName("UPDATE")
                    .orElseThrow(() -> new RuntimeException("Permission UPDATE not found"));
            PermissionEntity deletePermission = permissionRepository.findByName("DELETE")
                    .orElseThrow(() -> new RuntimeException("Permission DELETE not found"));

            Role roleAdmin = Role.builder()
                    .name(RoleEnum.ADMIN)
                    .build();

            Role roleUser = Role.builder()
                    .name(RoleEnum.USER)
                    .build();

            Role roleInvited = Role.builder()
                    .name(RoleEnum.INVITED)
                    .build();

            Role roleDeveloper = Role.builder()
                    .name(RoleEnum.DEVELOPER)
                    .build();

            roleRepository.save(roleAdmin);
            roleRepository.save(roleUser);
            roleRepository.save(roleInvited);
            roleRepository.save(roleDeveloper);
            System.out.println("Roles initialized successfully.");

            roleAdmin.setPermissionList(new HashSet<>(Set.of(createPermission, readPermission, updatePermission, deletePermission)));
            roleUser.setPermissionList(new HashSet<>(Set.of(readPermission)));
            roleInvited.setPermissionList(new HashSet<>(Set.of(readPermission)));
            roleDeveloper.setPermissionList(new HashSet<>(Set.of(createPermission, readPermission, updatePermission)));

            roleRepository.save(roleAdmin);
            roleRepository.save(roleUser);
            roleRepository.save(roleInvited);
            roleRepository.save(roleDeveloper);
            System.out.println("Roles and permissions associated successfully.");
        };
    }
}