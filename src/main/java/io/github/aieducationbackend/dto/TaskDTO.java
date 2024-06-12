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
    private String subject;
    private String predefinedPrompt;
    private String subjectSection;
    private String hobby;
    private int taskAmount;
    private int grade;
    private String prompt;
    private List<SubtaskDTO> generatedTasks;
}
