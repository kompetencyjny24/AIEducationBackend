package io.github.aieducationbackend.controller;

import io.github.aieducationbackend.dto.TaskDTO;
import io.github.aieducationbackend.dto.TaskRequestDTO;
import io.github.aieducationbackend.service.PdfService;
import io.github.aieducationbackend.service.TaskService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController()
@RequestMapping(path = "/api/v1/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final PdfService pdfService;

    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@RequestBody TaskRequestDTO taskRequestDTO) {
        TaskDTO taskDTO = taskService.createTask(taskRequestDTO);

        if (taskDTO == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(taskDTO);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<TaskDTO> getTask(@PathVariable UUID uuid) {
        TaskDTO taskDTO = taskService.getTask(uuid);
        if (taskDTO == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(taskDTO);
    }

    @GetMapping("/{uuid}/pdf")
    public void getTaskPdf(HttpServletResponse response, @PathVariable UUID uuid) {
        TaskDTO taskDTO = taskService.getTask(uuid);
        if (taskDTO == null) {
            return;
        }

        response.setContentType("application/pdf");

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=Generated Task.pdf";
        response.setHeader(headerKey, headerValue);

        pdfService.export(response, taskDTO);
    }

    @GetMapping("/generated-amount")
    public int getAmountOfGeneratedTasks() {
        return taskService.getAmountOfGeneratedTasks();
    }

}
