package org.example.controller;

import jakarta.servlet.http.HttpSession;
import org.example.model.Task;
import org.example.model.User;
import org.example.repository.TaskRepository;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class TaskController {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskController(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    // Show tasks for logged-in user
    @GetMapping("/tasks")
    public String home(Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");

        if (username == null) {
            return "redirect:/"; // not logged in
        }

        User user = userRepository.findByUsername(username);
        model.addAttribute("tasks", taskRepository.findByUser(user));
        return "tasks";
    }

    // Show add-task form
    @GetMapping("/tasks/add")
    public String addForm(Model model) {
        model.addAttribute("task", new Task());
        return "task-form";
    }

    // Handle adding a task
    @PostMapping("/tasks/add")
    public String addTask(@RequestParam String title,
                          @RequestParam String description,
                          HttpSession session) {
        String username = (String) session.getAttribute("username");

        if (username == null) {
            return "redirect:/";
        }

        User user = userRepository.findByUsername(username);

        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setUser(user);

        taskRepository.save(task);
        return "redirect:/tasks";
    }

    // Delete a task
    @GetMapping("/tasks/delete/{id}")
    public String deleteTask(@PathVariable Long id, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/";
        }

        Task task = taskRepository.findById(id).orElse(null);
        if (task != null && task.getUser().getUsername().equals(username)) {
            taskRepository.deleteById(id); // only owner can delete
        }

        return "redirect:/tasks";
    }
}
