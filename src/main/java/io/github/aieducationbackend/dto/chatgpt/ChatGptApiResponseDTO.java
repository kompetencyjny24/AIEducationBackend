package io.github.aieducationbackend.dto.chatgpt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatGptApiResponseDTO {

    private int created;
    private String model;
    private List<ChatGptApiResponseChoiceDTO> choices;
    private ChatGptApiResponseUsageDTO usage;
}
