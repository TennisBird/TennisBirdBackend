package org.tennis_bird.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.tennis_bird.api.data.InfoConverter;
import org.tennis_bird.api.data.TaskInfoRequest;
import org.tennis_bird.api.data.WorkerInfoResponse;
import org.tennis_bird.core.entities.*;
import org.tennis_bird.core.services.TaskService;
import org.tennis_bird.core.services.WorkerOnTaskService;
import org.tennis_bird.core.services.WorkerService;

import java.util.List;
import java.util.Optional;

@RestController
public class TaskController {
    @Autowired
    TaskService taskService;
    @Autowired
    WorkerService workerService;
    @Autowired
    WorkerOnTaskService workerOnTaskService;
    @Autowired
    InfoConverter converter;
    private static final Logger logger = LogManager.getLogger(TaskController.class.getName());
    @PostMapping(path = "/task/",
            consumes = "application/json",
            produces = "application/json")
    public TaskEntity createTask(@RequestBody TaskInfoRequest request) {
        logger.info("create task by {}", request);
        return taskService.create(converter.requestToEntity(request));
    }

    @GetMapping(path = "/task/{code}",
            produces = "application/json")
    public Optional<TaskEntity> getTask(@PathVariable(value = "code") String code) {
        logger.info("get task by {}", code);
        return taskService.findByCode(code);
    }

    @DeleteMapping(path = "/task/{code}", produces = "application/json")
    public boolean deleteTask(@PathVariable(value = "code") String code) {
        logger.info("Attempting to delete task with code: {}", code);
        Optional<TaskEntity> task = taskService.findByCode(code);
        return task.map(taskEntity -> taskService.delete(taskEntity.getId())).orElse(true);
    }

    @PostMapping(path = "/task/{code}/role/{role}",
            produces = "application/json")
    public boolean setWorkerOnTask(
            @PathVariable(value = "code") String code,
            @PathVariable(value = "role") String role,
            @RequestParam(value = "worker_id") Long authorId
    ) {
        logger.info("set worker {} on task with code {}", authorId, code);
        Optional<WorkerEntity> author = workerService.find(authorId);
        if (author.isEmpty()) {
            return false;
        }
        workerOnTaskService.setWorkerOnTask(code, author.get(), role);
        return workerOnTaskService.getWorkersWithRoleForTask(code, role).contains(author.get());
    }

    @GetMapping(path = "/task/{code}/role/{role}",
            produces = "application/json")
    public List<WorkerInfoResponse> getWorkersWithRoleOnTask(
            @PathVariable(value = "code") String code,
            @PathVariable(value = "role") String role
    ) {
        logger.info("get {} for task {}", role, code);
        return workerOnTaskService.getWorkersWithRoleForTask(code, role).stream()
                .map(converter::entityToResponse).toList();
    }

    @PutMapping(path = "/task/{code}/",
            produces = "application/json")
    public Optional<TaskEntity> updateTask(
            @PathVariable(value = "code") String code,
            @RequestParam(value = "title") Optional<String> title,
            @RequestParam(value = "description") Optional<String> description,
            @RequestParam(value = "status") Optional<String> status,
            @RequestParam(value = "priority") Optional<String> priority,
            @RequestParam(value = "estimate") Optional<Integer> estimate
    ) {
        logger.info(code);
        Optional<TaskEntity> taskO = taskService.findByCode(code);
        if (taskO.isEmpty()) {
            return taskO;
        }
        TaskEntity task = taskO.get();
        title.ifPresent(task::setTitle);
        description.ifPresent(task::setDescription);
        status.ifPresent(task::setStatus);
        priority.ifPresent(task::setPriority);
        estimate.ifPresent(task::setEstimate);
        return taskService.update(task);
    }
}
