package forum.latam.alura.domain.repository;

import forum.latam.alura.domain.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends BaseRepository<User, Integer> {

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN TRUE ELSE FALSE END FROM User u WHERE u.email = :email")
    boolean existsByEmail(String email);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN TRUE ELSE FALSE END FROM User u WHERE u.username = :username")
    boolean existsByUsername(String username);

    @Query("SELECT r FROM User r WHERE r.username = :username")
    Optional<User> findByname(String username);

}

