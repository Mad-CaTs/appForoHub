package forum.latam.alura.domain.repository;

import forum.latam.alura.domain.entity.Message;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends BaseRepository<Message, Integer> {

    @Query("SELECT m FROM Message m WHERE m.parentMessage.id = :parentMessageId")
    List<Message> findByParentMessageId(@Param("parentMessageId") Integer parentMessageId);


}
