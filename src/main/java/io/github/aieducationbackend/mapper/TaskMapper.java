package io.github.aieducationbackend.mapper;

import io.github.aieducationbackend.dto.SubtaskDTO;
import io.github.aieducationbackend.dto.TaskDTO;
import io.github.aieducationbackend.entity.Subtask;
import io.github.aieducationbackend.entity.Task;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    TaskDTO taskToTaskDTO(Task task);

    List<Subtask> subtaskDTOListToSubtaskList(List<SubtaskDTO> subtaskDTOList);
}
