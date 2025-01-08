package forum.latam.alura.presentation.controller;

import forum.latam.alura.domain.entity.PermissionEntity;
import forum.latam.alura.infrastructure.services.PermissionService;
import forum.latam.alura.presentation.dto.permissions.SinglePermission;
import forum.latam.alura.presentation.dto.permissions.UserRoleByPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RequestMapping("/api/permissions")
@RestController
public class permissionsController implements BaseApiController<PermissionEntity, Integer> {

    private final PermissionService permissionService;


    @Autowired
    public permissionsController (PermissionService permissionService){
        this.permissionService = permissionService;
    }

    @GetMapping("/all")
    @Override
    public ResponseEntity<List<SinglePermission>> getAll() {
        List<PermissionEntity> permissions = permissionService.getAll();
        List<SinglePermission> dtos = permissions.stream()
                .map(permission -> new SinglePermission(permission.getName()))
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<?> getOne(Integer id) {
        Optional<PermissionEntity> permissionOptional = permissionService.getById(id);
        if (permissionOptional.isPresent()) {
            PermissionEntity permission = permissionOptional.get();
            SinglePermission dto = new SinglePermission(permission.getName());
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/all-users-with-roles")
    public ResponseEntity<?> AllUsersWithRoles() {
        try {
            List<Object[]> users = permissionService.getAllUsersRolesAndPermissions();

            List<UserRoleByPermissions> userRoles = users.stream()
                    .map(row -> new UserRoleByPermissions(
                            (String) row[0],
                            (String) row[1],
                            (String) row[2]
                    ))
                    .toList();


            return ResponseEntity.ok(userRoles);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error getting users with roles and permissions: " + e.getMessage());
        }
    }



    @Override
    @PostMapping
    public ResponseEntity<?> save(PermissionEntity entity) {
        permissionService.update(entity);
        return ResponseEntity.ok("Permission Created Sucessfully.");
    }


    @Override
    @PutMapping("/{id}")
    public ResponseEntity<?> update(Integer id, PermissionEntity entity) {
        return ResponseEntity.status(405).body("This method not be created yet, roles are not editable.");
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        try{
            permissionService.delete(id);
            return ResponseEntity.ok("Permission deleted successfully.");
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting permission: " + e.getMessage());
        }
    }
}
