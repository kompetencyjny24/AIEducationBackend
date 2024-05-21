package io.github.aieducationbackend.service;

import io.github.aieducationbackend.client.ChatGptClient;
import io.github.aieducationbackend.dto.TaskDTO;
import io.github.aieducationbackend.dto.TaskRequestDTO;
import io.github.aieducationbackend.dto.chatgpt.ChatGptApiRequestDTO;
import io.github.aieducationbackend.dto.chatgpt.ChatGptApiRequestMessageDTO;
import io.github.aieducationbackend.dto.chatgpt.ChatGptApiResponseDTO;
import io.github.aieducationbackend.entity.Task;
import io.github.aieducationbackend.mapper.TaskMapper;
import io.github.aieducationbackend.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {

    private static final String CHAT_MODEL = "gpt-3.5-turbo";
    private static final String ROLE = "user";

    private final ChatGptClient chatGptClient;
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public TaskDTO createTask(TaskRequestDTO taskRequestDTO) {
        ChatGptApiRequestDTO chatGptApiRequestDto = ChatGptApiRequestDTO.builder()
                .model(CHAT_MODEL)
                .messages(List.of(ChatGptApiRequestMessageDTO.builder()
                        .role(ROLE)
                        .content(prepareContent(taskRequestDTO))
                        .build()))
                .build();

        ChatGptApiResponseDTO chatGptApiResponseDTO = chatGptClient.sendPrompt(chatGptApiRequestDto);
        Task task = prepareTask(chatGptApiResponseDTO);
        taskRepository.save(task);
        return taskMapper.taskToTaskDTO(task);
    }

    public TaskDTO getTask(UUID uuid) {
        Task task = taskRepository.findById(uuid).orElse(null);
        if (task == null) {
            return null;
        }

        return taskMapper.taskToTaskDTO(task);
    }

    private String prepareContent(TaskRequestDTO taskRequestDTO) {
        return "Generate task about " + taskRequestDTO.getSubject();
    }

    private Task prepareTask(ChatGptApiResponseDTO chatGptApiResponseDTO) {
        return new Task();
    }
}
