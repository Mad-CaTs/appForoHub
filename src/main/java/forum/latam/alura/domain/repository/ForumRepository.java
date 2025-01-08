package forum.latam.alura.domain.repository;


import forum.latam.alura.domain.entity.Forum;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ForumRepository extends BaseRepository<Forum, Integer> {

    @Query("select a from Forum AS a where a.title = :title")
    Optional<Forum> findByTitle(String title);


    @Query("SELECT f FROM Forum f " +
            "LEFT JOIN FETCH f.messages m " +
            "LEFT JOIN FETCH m.replies " +
            "WHERE f.id = :forumId")
    Optional<Forum> findForumWithMessagesAndReplies(Integer forumId);

    @Query("SELECT f FROM Forum f " +
            "LEFT JOIN FETCH f.messages m " +
            "LEFT JOIN FETCH m.replies")
    List<Forum> findAllForumsWithMessagesAndReplies();

}


