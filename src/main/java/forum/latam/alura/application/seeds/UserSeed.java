package forum.latam.alura.application.seeds;

import forum.latam.alura.domain.entity.Role;
import forum.latam.alura.domain.entity.User;
import forum.latam.alura.domain.repository.RoleRepository;
import forum.latam.alura.domain.repository.UserRepository;
import forum.latam.alura.infrastructure.helpers.RoleEnum;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Component
public class UserSeed {

    private final PasswordEncoder passwordEncoder;

    public UserSeed(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    CommandLineRunner initUsers(UserRepository userRepository, RoleRepository roleRepository) {
        return args -> {
            if (userRepository.count() > 0) {
                System.out.println("Users already initialized. Skipping seed.");
                return;
            }

            Role roleAdmin = roleRepository.findByName(RoleEnum.ADMIN)
                    .orElseThrow(() -> new RuntimeException("Role ADMIN not found"));
            Role roleUser = roleRepository.findByName(RoleEnum.USER)
                    .orElseThrow(() -> new RuntimeException("Role USER not found"));

            User userMarkus = User.builder()
                    .username("Mad_CaTs")
                    .password(passwordEncoder.encode("madcats"))
                    .email("markusperezch1@gmail.com")
                    .name("Markus")
                    .CreatedAt(new Date())
                    .isEnabled(true)
                    .accountNoExpired(true)
                    .accountNoLocked(true)
                    .credentialNoExpired(true)
                    .build();

            User userPedro = User.builder()
                    .username("Pedrogamer123")
                    .password(passwordEncoder.encode("pedro123"))
                    .email("pedro@gmail.com")
                    .name("Pedro")
                    .CreatedAt(new Date())
                    .isEnabled(true)
                    .accountNoExpired(true)
                    .accountNoLocked(true)
                    .credentialNoExpired(true)
                    .build();

            userRepository.saveAll(List.of(userMarkus, userPedro));
            System.out.println("Users created successfully.");

            userMarkus.setRoles(new HashSet<>(Set.of(roleAdmin)));
            userPedro.setRoles(new HashSet<>(Set.of(roleUser)));

            userRepository.saveAll(List.of(userMarkus, userPedro));
            System.out.println("Roles assigned to users successfully.");
        };
    }
}