package com.jawbr.todos.service;

import com.jawbr.todos.dto.request.TodoRequest;
import com.jawbr.todos.entity.Todo;
import com.jawbr.todos.exception.TodoNotFoundException;
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
        Sort.Direction direction = sortBy.equalsIgnoreCase("prioridade") ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Optional.of(todoRepository.findAll(
                        PageRequest.of(Optional.ofNullable(page).orElse(0),
                                pageSize,
                                direction,
                                sortBy)))
                .filter(list -> !list.isEmpty())
                .orElseThrow(() -> new TodoNotFoundException("Nenhuma tarefa encontrada."));

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

    public Todo updateTodo(int id, TodoRequest todo) {
        return todoRepository.findById(id)
                .map((t) -> {
                    Todo updatedTodo = Todo.builder()
                            .id(id)
                            .nome(todo.nome())
                            .descricao(todo.descricao())
                            .prioridade(todo.prioridade())
                            .realizado(todo.realizado())
                            .build();

                    return todoRepository.save(updatedTodo);
                })
                .orElseThrow(() -> new TodoNotFoundException(String.format("Tarefa de id '%d' não encontrada.", id)));
    }


    public void deleteTodoById(int id) {
        todoRepository.findById(id).ifPresentOrElse(
                todoRepository::delete, () -> {
                    throw new TodoNotFoundException(String.format("Tarefa de id '%d' não encontrada.", id));
                }
        );

    }
}
