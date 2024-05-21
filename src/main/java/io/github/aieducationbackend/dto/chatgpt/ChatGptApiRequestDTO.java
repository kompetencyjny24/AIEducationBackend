package io.github.aieducationbackend.dto.chatgpt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatGptApiRequestDTO {
    private String model;
    private List<ChatGptApiRequestMessageDTO> messages;
}
