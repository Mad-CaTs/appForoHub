package forum.latam.alura.presentation.dto.messages;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.Date;
import java.util.List;

@Builder
public record SingleMessage(
       @NotBlank  String content,
       @NotBlank String authorUsername,
       @NotBlank Date createdAt,
       List<SingleMessage> replies,
       Integer parentMessageId

)
{}
