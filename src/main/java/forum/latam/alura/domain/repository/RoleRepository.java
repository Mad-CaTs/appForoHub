package forum.latam.alura.domain.repository;


import forum.latam.alura.domain.entity.Role;
import forum.latam.alura.infrastructure.helpers.RoleEnum;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends BaseRepository<Role, Integer> {

    @Query("SELECT r FROM Role r WHERE r.name = :name")
    Optional<Role> findByName(RoleEnum name);

    @Query("SELECT r FROM Role r WHERE r.name IN :roleNames")
    List<Role> findRoleEntitiesByRoleEnumIn(List<String> roleNames);

    @Query("SELECT r FROM Role r WHERE r.name IN :names")
    Set<Role> findByNameIn(Set<RoleEnum> names);

    @Query("SELECT r, p FROM Role AS r Inner Join r.permissionList AS p")
    Optional<Role> RoleWithPermissions();

}


