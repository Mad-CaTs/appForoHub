package forum.latam.alura.application.seeds;

import forum.latam.alura.domain.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class SeedOrchestrator implements CommandLineRunner {

    private final UserSeed userSeed;
    private final PermissionSeed permissionSeed;
    private final RoleSeed roleSeed;
    private final ForumSeed forumSeed;
    private final MessageSeed messageSeed;

    private UserRepository userRepository;
    private PermissionRepository permissionRepository;
    private RoleRepository roleRepository;
    private ForumRepository forumRepository;
    private MessageRepository messageRepository;

    @Autowired
    public SeedOrchestrator(UserSeed userSeed, PermissionSeed permissionSeed, RoleSeed roleSeed,
                            ForumSeed forumSeed, MessageSeed messageSeed,
                            UserRepository userRepository, PermissionRepository permissionRepository,
                            RoleRepository roleRepository, ForumRepository forumRepository,
                            MessageRepository messageRepository) {
        this.userSeed = userSeed;
        this.permissionSeed = permissionSeed;
        this.roleSeed = roleSeed;
        this.forumSeed = forumSeed;
        this.messageSeed = messageSeed;
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.forumRepository = forumRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        permissionSeed.initPermissions(permissionRepository).run(args);

        roleSeed.initRoles(roleRepository, permissionRepository).run(args);

        userSeed.initUsers(userRepository, roleRepository).run(args);


        forumSeed.initForums(userRepository, forumRepository).run(args);

        messageSeed.initMessages(userRepository, forumRepository, messageRepository).run(args);

        System.out.println("All seeds initialized successfully.");
    }
}
