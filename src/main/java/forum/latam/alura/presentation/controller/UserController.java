package forum.latam.alura.presentation.controller;


import forum.latam.alura.domain.entity.User;
import forum.latam.alura.infrastructure.services.UserService;
import forum.latam.alura.presentation.dto.UserWithRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController implements BaseApiController<User, Integer> {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Override
    @GetMapping
    public ResponseEntity<List<UserWithRole>> getAll() {
        List<User> users = userService.getAll();
        List<UserWithRole> dtos = users.stream()
                .map(user -> new UserWithRole(
                        user.getUsername(),
                        user.getName(),
                        user.getEmail(),
                        user.getCreatedAt(),
                        user.isEnabled(),
                        user.isAccountNoExpired(),
                        user.isAccountNoLocked(),
                        user.isCredentialNoExpired(),
                        user.getRoles().stream()
                                .map(role -> role.getName().toString())
                                .collect(Collectors.toSet())))
                .toList();

        return ResponseEntity.ok(dtos);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Integer id) {
        Optional<User> userOptional = userService.getById(id);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            UserWithRole dto = new UserWithRole(
                    user.getUsername(),
                    user.getName(),
                    user.getEmail(),
                    user.getCreatedAt(),
                    user.isEnabled(),
                    user.isAccountNoExpired(),
                    user.isAccountNoLocked(),
                    user.isCredentialNoExpired(),
                    user.getRoles().stream()
                            .map(role -> role.getName().toString())
                            .collect(Collectors.toSet())
            );
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    @PostMapping
    public ResponseEntity<?> save(@RequestBody User user) {
        userService.update(user);
        return ResponseEntity.ok("User Created Sucessfully.");
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody User user) {
        return ResponseEntity.status(405).body("This method is not found.");
    }


    @Override
    @DeleteMapping("/{id}")
    @Deprecated
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        return ResponseEntity.status(405).body("This method is not found.");
    }

    @DeleteMapping("deleteAcc/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id, Authentication authentication) {
        try {
            userService.deleteUser(id, authentication);
            return ResponseEntity.ok("User deleted successfully.");
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body("You don't have permission to delete this user: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid user id: " + id);
        }
    }


    @PostMapping("/{id}/add-role")
    public ResponseEntity<?> addRoleToUser(@PathVariable Integer id, @RequestParam String roleName) {
        try {
            userService.addRoleToUser(id, roleName);
            return ResponseEntity.ok("Role " + roleName + " added successfully to the user.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid role id: " + id);
        }
    }

    @DeleteMapping("/{id}/remove-role")
    public ResponseEntity<?> removeRoleFromUser(@PathVariable Integer id, @RequestParam String roleName) {
        try {
            userService.removeRoleFromUser(id, roleName);
            return ResponseEntity.ok("Role " + roleName + " removed successfully from the user.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid role id: " + id);
        }
    }

    @PutMapping("/{id}/details")
    public ResponseEntity<?> updateUserDetails(@PathVariable Integer id, @RequestBody User updatedUser, Authentication authentication) {
        try {
            User updated = userService.updateUserDetails(id, updatedUser, authentication);
            return ResponseEntity.ok("User details updated successfully.");
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body("You don't have permission to update this user: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("User not found or invalid data: " + e.getMessage());
        }
    }
}
