package forum.latam.alura.infrastructure.services;


import forum.latam.alura.domain.entity.Role;
import forum.latam.alura.domain.entity.User;
import forum.latam.alura.domain.repository.RoleRepository;
import forum.latam.alura.domain.repository.UserRepository;
import forum.latam.alura.infrastructure.helpers.RoleEnum;
import forum.latam.alura.presentation.dto.AuthCreateUserRequest;
import forum.latam.alura.presentation.dto.AuthResponse;
import forum.latam.alura.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class UserService extends GenericService<User, Integer>{

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils, RoleRepository roleRepository) {
      super(userRepository);
      this.userRepository = userRepository;
      this.passwordEncoder = passwordEncoder;
      this.jwtUtils = jwtUtils;
      this.roleRepository = roleRepository;
    }

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RoleRepository roleRepository;

    public AuthResponse create(AuthCreateUserRequest createUserRequest) {
        try {
            String username = createUserRequest.username();
            String password = createUserRequest.password();
            String name = createUserRequest.name();
            String email = createUserRequest.email();

            if (userRepository.existsByUsername(username)) {
                return new AuthResponse(null, "The username is already in use.", null, false);
            }

            if (userRepository.existsByEmail(email)) {
                return new AuthResponse(null, "The email is already in use.", null, false);
            }

            List<String> rolesRequest = createUserRequest.roleRequest() != null ?
                    createUserRequest.roleRequest().roleListName() : new ArrayList<>();

            validateRoles(rolesRequest);

            if (!isPasswordValid(password)) {
                return new AuthResponse(null, "Password must contain at least one uppercase letter, two numbers, and one special character.", null, false);
            }
            if (rolesRequest.size() > 2) {
                throw new IllegalArgumentException("A user cannot have more than 3 roles in total, including the default role.");
            }

            Role defaultUserRole = roleRepository.findByName(RoleEnum.USER)
                    .orElseThrow(() -> new IllegalArgumentException("The default role USER does not exist in the database."));

            Set<Role> roleEntityList = getRoles(rolesRequest, defaultUserRole);

            User userEntity = buildUserEntity(username, name, email, password, roleEntityList);

            User userSaved = userRepository.save(userEntity);

            String accessToken = generateToken(userSaved);

            return buildAuthResponse(username, accessToken);

        } catch (Exception e) {
            return new AuthResponse(null, "An error occurred during user creation: " + e.getMessage(), null, false);
        }
    }


    private void validateRoles(List<String> rolesRequest) {
        if (rolesRequest.stream().anyMatch(role -> role.equalsIgnoreCase("ADMIN") || role.equalsIgnoreCase("DEVELOPER"))) {
            throw new IllegalArgumentException("Cannot assign roles ADMIN or DEVELOPER during user creation.");
        }
    }

    private Set<Role> getRoles(List<String> rolesRequest, Role defaultUserRole) {
        Set<Role> roleEntityList = new HashSet<>();
        for (String roleName : rolesRequest) {
            RoleEnum roleEnum = RoleEnum.valueOf(roleName);
            Role role = roleRepository.findByName(roleEnum)
                    .orElseThrow(() -> new IllegalArgumentException("Role " + roleEnum.toString() + " does not exist."));
            roleEntityList.add(role);
        }
        roleEntityList.add(defaultUserRole);
        return roleEntityList;
    }

    private boolean isPasswordValid(String password) {
        return password != null && password.matches("^(?=.*[A-Z])(?=.*[0-9].*[0-9])(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,}$");
    }


    private User buildUserEntity(String username, String name, String email, String password, Set<Role> roleEntityList) {
        return User.builder()
                .username(username)
                .name(name)
                .email(email)
                .CreatedAt(new Date())
                .password(passwordEncoder.encode(password))
                .roles(roleEntityList)
                .isEnabled(true)
                .accountNoLocked(true)
                .accountNoExpired(true)
                .credentialNoExpired(true)
                .build();
    }

    private String generateToken(User userSaved) {
        ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();

        userSaved.getRoles().forEach(role -> {
            System.out.println("Role: " + role.getName());
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName().name()));
        });

        userSaved.getRoles().stream()
                .flatMap(role -> role.getPermissionList().stream())
                .forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission.getName())));

        Authentication authentication = new UsernamePasswordAuthenticationToken(userSaved.getUsername(), null, authorities);

        return jwtUtils.createToken(authentication);
    }



    private AuthResponse buildAuthResponse(String username, String accessToken) {
        return new AuthResponse(
                username,
                "User created successfully with default role USER.",
                accessToken,
                true
        );
    }



    public void addRoleToUser(Integer userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + userId));

        RoleEnum roleEnum = RoleEnum.valueOf(roleName);
        Role role = roleRepository.findByName(roleEnum)
                .orElseThrow(() -> new IllegalArgumentException("Role " + roleEnum + " does not exist."));

        if (user.getRoles().contains(role)) {
            throw new IllegalArgumentException("User already has the role: " + roleName);
        }

        user.getRoles().add(role);
        userRepository.save(user);
    }

    public void removeRoleFromUser(Integer userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + userId));

        RoleEnum roleEnum = RoleEnum.valueOf(roleName);
        Role role = roleRepository.findByName(roleEnum)
                .orElseThrow(() -> new IllegalArgumentException("Role " + roleEnum + " does not exist."));

        if (!user.getRoles().contains(role)) {
            throw new IllegalArgumentException("User does not have the role: " + roleName);
        }

        user.getRoles().remove(role);
        userRepository.save(user);
    }

    public User updateUserDetails(Integer id, User updatedUser, Authentication authentication) {
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByname(currentUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + currentUsername));

        if (currentUser.getId() != id && !currentUser.getRoles().stream()
                .anyMatch(role -> role.getName().name().equals("ADMIN") || role.getName().name().equals("DEVELOPER"))) {
            throw new SecurityException("u dont have permission to update user details to other users");
        }

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id + " and username: " + currentUsername));

        if (updatedUser.getUsername() != null) {
            existingUser.setUsername(updatedUser.getUsername());
        }
        if (updatedUser.getEmail() != null) {
            existingUser.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getName() != null) {
            existingUser.setName(updatedUser.getName());
        }

        existingUser.setEnabled(updatedUser.isEnabled());
        existingUser.setAccountNoExpired(updatedUser.isAccountNoExpired());
        existingUser.setAccountNoLocked(updatedUser.isAccountNoLocked());
        existingUser.setCredentialNoExpired(updatedUser.isCredentialNoExpired());

        return userRepository.save(existingUser);
    }

    public void deleteUser(Integer id, Authentication authentication) {
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByname(currentUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + currentUsername));

        if (currentUser.getId() != id && !currentUser.getRoles().stream()
                .anyMatch(role -> role.getName().name().equals("ADMIN") || role.getName().name().equals("DEVELOPER"))) {
            throw new SecurityException("U dont have permission to delete accounts to other users");
        }

        userRepository.deleteById(id);
    }


}
