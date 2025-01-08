package forum.latam.alura.presentation.controller;

import forum.latam.alura.domain.entity.PermissionEntity;
import forum.latam.alura.domain.entity.Role;
import forum.latam.alura.infrastructure.services.RoleService;
import forum.latam.alura.presentation.dto.roles.RolewithPermissions;
import forum.latam.alura.presentation.dto.roles.SingleRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/roles")
public class RoleController implements BaseApiController<Role, Integer> {

    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }


    @GetMapping("/single")
    public ResponseEntity<List<SingleRole>> getSingleRoles() {
        List<Role> roles = roleService.getAll();
        List<SingleRole> dtos = roles.stream()
                .map(role -> new SingleRole(role.getName().toString()))
                .toList();

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/with-permissions")
    @Override
    public ResponseEntity<List<RolewithPermissions>> getAll() {
        List<Role> roles = roleService.getAll();
        List<RolewithPermissions> dtos = roles.stream()
                .map(role -> new RolewithPermissions(
                        role.getName().toString(),
                        role.getPermissionList().stream()
                                .map(PermissionEntity::getName)
                                .collect(Collectors.toSet())
                )).toList();

        return ResponseEntity.ok(dtos);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(Integer id) {
        Optional<Role> roleOptional = roleService.getById(id);

        if (roleOptional.isPresent()) {
            Role role = roleOptional.get();
            RolewithPermissions dto = new RolewithPermissions(
                    role.getName().toString(),
                    role.getPermissionList().stream()
                            .map(PermissionEntity::getName)
                            .collect(Collectors.toSet())
            );
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Override
    public ResponseEntity<?> save(Role entity) {
        roleService.update(entity);
        return ResponseEntity.ok("Role Created Sucessfully.");
    }

    @PutMapping("/{id}")
    @Override
    public ResponseEntity<?> update(@PathVariable Integer id, Role entity) {
        return ResponseEntity.status(405).body("This method not be created yet, roles are not editable.");
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        try{
            roleService.delete(id);
            return ResponseEntity.ok("Role deleted successfully.");
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting role: " + e.getMessage());
        }
    }



    @PostMapping("/{roleId}/add-permission")
    public ResponseEntity<?> addPermissionToRole(@PathVariable Integer roleId, @RequestParam String permissionName) {
        try {
            roleService.addPermissionToRole(roleId, permissionName);
            return ResponseEntity.ok("Permission "+ permissionName+" added successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error adding permission: " + e.getMessage());
        }
    }

    @DeleteMapping("/{roleId}/delete-permissions")
    public ResponseEntity<?> removePermissionFromRole(@PathVariable Integer roleId, @RequestParam String permissionName) {
        try {
            roleService.removePermissionFromRole(roleId, permissionName);
            return ResponseEntity.ok("Permission "+ permissionName +" removed successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error removing permission: " + e.getMessage());
        }
    }
}
