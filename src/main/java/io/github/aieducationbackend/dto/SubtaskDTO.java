package io.github.aieducationbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubtaskDTO {

    private Long id;
    private String content;
    private String answer;
    private List<String> hints;
}
