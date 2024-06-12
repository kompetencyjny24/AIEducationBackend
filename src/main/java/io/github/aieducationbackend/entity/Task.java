package io.github.aieducationbackend.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 2048)
    private String prompt;

    @Column(length = 2048)
    private String predefinedPrompt;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Subtask> generatedTasks;
    private String subject;
    private String subjectSection;
    private String hobby;
    private int taskAmount;
    private int grade;

}
