package io.github.aieducationbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {

    private UUID id;
    private String prompt;
    private String content;
    private String answer;
    private List<String> hints;
}
