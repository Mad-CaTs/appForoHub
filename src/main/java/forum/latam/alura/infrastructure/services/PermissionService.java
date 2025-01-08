package forum.latam.alura.infrastructure.services;

import forum.latam.alura.domain.entity.PermissionEntity;
import forum.latam.alura.domain.repository.PermissionRepository;
import forum.latam.alura.presentation.dto.permissions.UserRoleByPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionService extends GenericService<PermissionEntity, Integer> {

    @Autowired
    public PermissionService(PermissionRepository permissionRepository) {
        super(permissionRepository);
        this.permissionRepository = permissionRepository;
    }

    private final PermissionRepository permissionRepository;


    public List<Object[]> getAllUsersRolesAndPermissions() {
        return permissionRepository.findAllUsersRolesAndPermissions();
    }



}
