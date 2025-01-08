package forum.latam.alura.infrastructure.services;

import forum.latam.alura.domain.entity.PermissionEntity;
import forum.latam.alura.domain.entity.Role;
import forum.latam.alura.domain.repository.PermissionRepository;
import forum.latam.alura.domain.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService extends GenericService<Role, Integer> {

    @Autowired
    public RoleService(RoleRepository repository, PermissionRepository permissionentity) {
        super(repository);
        this.roleRepository = repository;
        this.permissionRepository = permissionentity;
    }

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;


    public void addPermissionToRole(Integer roleId, String permissionName) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found with ID: " + roleId));

        PermissionEntity permission = permissionRepository.findByName(permissionName)
                .orElseThrow(() -> new IllegalArgumentException("Permission " + permissionName + " does not exist."));

        if (role.getPermissionList().contains(permission)) {
            throw new IllegalArgumentException("Role already has the permission: " + permissionName);
        }

        role.getPermissionList().add(permission);
        roleRepository.save(role);
    }

    public void removePermissionFromRole(Integer roleId, String permissionName) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found with ID: " + roleId));

        PermissionEntity permission = permissionRepository.findByName(permissionName)
                .orElseThrow(() -> new IllegalArgumentException("Permission " + permissionName + " does not exist."));

        if (!role.getPermissionList().contains(permission)) {
            throw new IllegalArgumentException("Role does not have the permission: " + permissionName);
        }

        role.getPermissionList().remove(permission);
        roleRepository.save(role);
    }

}

