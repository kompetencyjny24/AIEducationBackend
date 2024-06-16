package io.github.aieducationbackend.service;

import io.github.aieducationbackend.client.ChatGptClient;
import io.github.aieducationbackend.dto.SubtaskDTO;
import io.github.aieducationbackend.dto.TaskDTO;
import io.github.aieducationbackend.dto.TaskRequestDTO;
import io.github.aieducationbackend.dto.chatgpt.ChatGptApiRequestDTO;
import io.github.aieducationbackend.dto.chatgpt.ChatGptApiRequestMessageDTO;
import io.github.aieducationbackend.dto.chatgpt.ChatGptApiResponseChoiceDTO;
import io.github.aieducationbackend.dto.chatgpt.ChatGptApiResponseDTO;
import io.github.aieducationbackend.entity.Subtask;
import io.github.aieducationbackend.entity.Task;
import io.github.aieducationbackend.mapper.TaskMapper;
import io.github.aieducationbackend.repository.SubtaskRepository;
import io.github.aieducationbackend.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {

    private static final String CHAT_MODEL = "gpt-3.5-turbo";
    private static final String ROLE = "user";
    private static final String PROMPT_PLURAL_SUFFIX = " Pod tym wypisz tekst \nSEPARATOR\n I generuj kolejne zadania w takim samym formacie jak przed chwilą.";
    private static final String PROMPT_SUFFIX = " Pod tym wygeneruj 2 podpowiedzi do zadania i na koniec napisz odpowiedź. Wypisz wszystko w formacie Zadanie: *tutaj napisz tresc zadania* zrób \n\n i Podpowiedź 1: *Tutaj wypisz tresc podpowiedzi 1* zrób \n\n i Podpowiedź 2: *Tutaj wypisz tresc podpowiedzi 2* zrób \n\n i Odpowiedź: *Tutaj wypisz tresc odpowiedzi*.";
    private static final String PROMPT = "Wygeneruj mi treść {TASK_AMOUNT} z przedmiotu {SUBJECT} z działu \"{SUBJECT_SECTION}\". Nawiąż treścią zadania do hobby o tematyce {HOBBY}. Weź pod uwagę że uczeń jest w {GRADE} klasie podstawowej.";

    private final ChatGptClient chatGptClient;
    private final TaskRepository taskRepository;
    private final SubtaskRepository subtaskRepository;
    private final TaskMapper taskMapper;

    public TaskDTO createTask(TaskRequestDTO taskRequestDTO) {
        String prompt = preparePrompt(taskRequestDTO);

        ChatGptApiRequestDTO chatGptApiRequestDto = ChatGptApiRequestDTO.builder()
                .model(CHAT_MODEL)
                .messages(Arrays.asList(
                        ChatGptApiRequestMessageDTO.builder()
                                .role(ROLE)
                                .content(prompt)
                                .build()))
                .build();

        ChatGptApiResponseDTO chatGptApiResponseDTO = chatGptClient.sendPrompt(chatGptApiRequestDto);
        Task task = prepareTask(chatGptApiResponseDTO, prompt);
        if (task.getGeneratedTasks().size() != taskRequestDTO.getTaskAmount()) {
            throw new RuntimeException("Wygenerowano błędną ilosć zadań");
        }

        task.setTaskAmount(taskRequestDTO.getTaskAmount());
        task.setGrade(taskRequestDTO.getGrade());
        task.setPredefinedPrompt(taskRequestDTO.getPredefinedPrompt());
        task.setSubject(taskRequestDTO.getSubject());
        task.setSubjectSection(taskRequestDTO.getSubjectSection());
        task.setHobby(taskRequestDTO.getHobby());
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

    private String preparePrompt(TaskRequestDTO taskRequestDTO) {
        if (StringUtils.isEmpty(taskRequestDTO.getPredefinedPrompt())) {
            String replacedPrompt = StringUtils.replace(PROMPT + PROMPT_SUFFIX, "{SUBJECT}", taskRequestDTO.getSubject());
            replacedPrompt = StringUtils.replace(replacedPrompt, "{SUBJECT_SECTION}", taskRequestDTO.getSubjectSection());
            replacedPrompt = StringUtils.replace(replacedPrompt, "{HOBBY}", taskRequestDTO.getHobby());
            replacedPrompt = StringUtils.replace(replacedPrompt, "{GRADE}", String.valueOf(taskRequestDTO.getGrade()));

            int taskAmount = taskRequestDTO.getTaskAmount();
            String taskAmountString = taskAmount + (taskAmount == 1 ? " zadania otwartego " : " zadań otwartych ");
            replacedPrompt = StringUtils.replace(replacedPrompt, "{TASK_AMOUNT}", taskAmountString);

            if (taskAmount > 1) {
                replacedPrompt += PROMPT_PLURAL_SUFFIX;
            }

            return replacedPrompt;
        }

        return taskRequestDTO.getPredefinedPrompt() + PROMPT_SUFFIX + " Jesli zadań jest więcej niż jedno to " + PROMPT_PLURAL_SUFFIX;
    }

    private Task prepareTask(ChatGptApiResponseDTO chatGptApiResponseDTO, String prompt) {
        if (CollectionUtils.isEmpty(chatGptApiResponseDTO.getChoices())) {
            throw new RuntimeException("Błąd połączenia z ChatGPT");
        }

        ChatGptApiResponseChoiceDTO choiceDTO = chatGptApiResponseDTO.getChoices().get(0);
        if (choiceDTO == null || choiceDTO.getMessage() == null || StringUtils.isEmpty(choiceDTO.getMessage().getContent())) {
            throw new RuntimeException("Błąd połączenia z ChatGPT");
        }

        String content = choiceDTO.getMessage().getContent();

        Task task = new Task();
        task.setPrompt(StringUtils.substringBefore(prompt, PROMPT_SUFFIX));

        String[] separatedTasks = content.split("SEPARATOR");

        List<Subtask> subtasks = new ArrayList<>();
        for (String separatedTask : separatedTasks) {
            subtasks.add(prepareSubtask(separatedTask));
        }

        task.setGeneratedTasks(subtasks);
        return task;
    }

    private Subtask prepareSubtask(String content) {
        Subtask subtask = new Subtask();
        try {
            subtask.setContent(extractTaskContentFromResponse(content));
            subtask.setAnswer(extractAnswerFromResponse(content));
            subtask.setHints(Arrays.asList(extractFirstHintFromResponse(content), extractSecondHintFromResponse(content)));
        } catch (Exception e) {
            throw new RuntimeException("Wystąpił błąd połączenia z ChatGPT");
        }

        return subtask;
    }

    private String extractTaskContentFromResponse(String content) {
        return StringUtils.substringBetween(content, "Zadanie:", "\n").trim();
    }

    private String extractFirstHintFromResponse(String content) {
        return StringUtils.substringBetween(content, "Podpowiedź 1:", "\n").trim();
    }

    private String extractSecondHintFromResponse(String content) {
        return StringUtils.substringBetween(content, "Podpowiedź 2:", "\n").trim();
    }

    private String extractAnswerFromResponse(String content) {
        return StringUtils.substringBetween(content, "Odpowiedź:", ".").trim();
    }

    public int getAmountOfGeneratedTasks() {
        return (int) subtaskRepository.count();
    }

    public TaskDTO editSubtasks(UUID uuid, List<SubtaskDTO> subtaskDTOList) {
        Task task = taskRepository.findById(uuid)
                .orElse(null);

        if (task == null) {
            return null;
        }

        task.setGeneratedTasks(taskMapper.subtaskDTOListToSubtaskList(subtaskDTOList));
        taskRepository.save(task);
        return taskMapper.taskToTaskDTO(task);
    }
}
