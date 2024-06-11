package io.github.aieducationbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequestDTO {

    private String subject;
    private String predefinedPrompt;
    private String subjectSection;
    private String hobby;
    private int taskAmount;
    private int grade;

}
