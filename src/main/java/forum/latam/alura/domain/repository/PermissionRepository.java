package forum.latam.alura.domain.repository;

import forum.latam.alura.domain.entity.PermissionEntity;
import forum.latam.alura.presentation.dto.permissions.UserRoleByPermissions;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PermissionRepository extends BaseRepository<PermissionEntity, Integer> {

    @Query("select a from PermissionEntity AS a where a.name = :name")
    Optional<PermissionEntity> findByName(String name);


    @Query(nativeQuery = true, value = "SELECT " +
            "    u.username, " +
            "    r.role_name AS roleName, " +
            "    p.name AS permissionName " +
            "FROM users u " +
            "INNER JOIN user_role ur ON u.id = ur.id_user " +
            "INNER JOIN roles r ON ur.id_role = r.id " +
            "INNER JOIN role_permissions rp ON r.id = rp.role_id " +
            "INNER JOIN permissions p ON rp.permission_id = p.id")
    List<Object[]> findAllUsersRolesAndPermissions();


}
