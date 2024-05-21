package io.github.aieducationbackend.mapper;

import io.github.aieducationbackend.dto.TaskDTO;
import io.github.aieducationbackend.entity.Task;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    TaskDTO taskToTaskDTO(Task task);
}
