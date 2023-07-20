package com.jawbr.todos.service;

import com.jawbr.todos.dto.request.TodoRequest;
import com.jawbr.todos.entity.Todo;
import com.jawbr.todos.repository.TodoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public Page<Todo> findAllTodos(Integer page, Integer pageSize, String sortBy) {
        pageSize = Math.min(Optional.ofNullable(pageSize).orElse(10), 50);
        sortBy = Optional.ofNullable(sortBy)
                .filter(s -> !s.isEmpty())
                .orElse("id");
        return Optional.of(todoRepository.findAll(
                        PageRequest.of(Optional.ofNullable(page).orElse(0),
                                pageSize,
                                Sort.Direction.ASC,
                                sortBy)))
                .filter(list -> !list.isEmpty())
                .orElseThrow(() -> new RuntimeException("Nenhuma tarefa encontrada."));

    }

    public Todo createTodo(TodoRequest todo) {
        Todo newTodo = Todo.builder()
                .nome(todo.nome())
                .descricao(todo.descricao())
                .prioridade(todo.prioridade())
                .realizado(todo.realizado())
                .build();
        return todoRepository.save(newTodo);
    }

    public Page<Todo> updateTodo(int id, TodoRequest todo) {
        todoRepository.findById(id).ifPresentOrElse((t) -> {
            todoRepository.save(Todo.builder()
                    .id(id)
                    .nome(todo.nome())
                    .descricao(todo.descricao())
                    .prioridade(todo.prioridade())
                    .realizado(todo.realizado())
                    .build());
        }, () -> {
            throw new RuntimeException(String.format("Tarefa de id '%d' não encontrado", id));
        });

        return findAllTodos(0, 10, "id");
    }

    public void deleteTodoById(int id) {
        todoRepository.findById(id).ifPresentOrElse(
                todoRepository::delete, () -> {
                    throw new RuntimeException(String.format("Tarefa de id '%d' não encontrado", id));
                }
        );

    }
}
