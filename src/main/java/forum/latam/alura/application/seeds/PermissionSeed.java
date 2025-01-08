package forum.latam.alura.application.seeds;

import forum.latam.alura.domain.entity.PermissionEntity;
import forum.latam.alura.domain.repository.PermissionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class PermissionSeed {

    CommandLineRunner initPermissions(PermissionRepository permissionRepository) {
        return args -> {
            if (permissionRepository.count() > 0) {
                System.out.println("Permissions already initialized. Skipping seed.");
                return;
            }

            PermissionEntity createPermission = PermissionEntity.builder()
                    .name("CREATE")
                    .build();

            PermissionEntity readPermission = PermissionEntity.builder()
                    .name("READ")
                    .build();

            PermissionEntity updatePermission = PermissionEntity.builder()
                    .name("UPDATE")
                    .build();

            PermissionEntity deletePermission = PermissionEntity.builder()
                    .name("DELETE")
                    .build();

            permissionRepository.saveAll(List.of(createPermission, readPermission, updatePermission, deletePermission));
            System.out.println("Permissions initialized successfully.");
        };
    }
}
