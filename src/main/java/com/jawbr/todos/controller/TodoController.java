package com.jawbr.todos.controller;

import com.jawbr.todos.dto.request.TodoRequest;
import com.jawbr.todos.entity.Todo;
import com.jawbr.todos.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public Page<Todo> findAllTodos(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String sortBy)
    {
        return todoService.findAllTodos(page, pageSize, sortBy);
    }

    @PostMapping
    public ResponseEntity<Todo> createTodo(@Valid @RequestBody TodoRequest todoRequest) {
        Todo response = todoService.createTodo(todoRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public Page<Todo> updateTodo(@PathVariable int id, TodoRequest todoRequest) {
        return todoService.updateTodo(id, todoRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTodo(@PathVariable int id) {
        todoService.deleteTodoById(id);
        return ResponseEntity.noContent().build();
    }
}
