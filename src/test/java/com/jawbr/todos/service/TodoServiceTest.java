package com.jawbr.todos.service;

import com.jawbr.todos.dto.request.TodoRequest;
import com.jawbr.todos.entity.Todo;
import com.jawbr.todos.exception.TodoNotFoundException;
import com.jawbr.todos.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class TodoServiceTest {

    @InjectMocks
    private TodoService todoService;

    @Mock
    private TodoRepository todoRepository;

    private Todo todo;
    private TodoRequest todoRequest;

    @BeforeEach
    public void init() {
        todo = Todo.builder()
                .id(1)
                .nome("Valid TODO")
                .descricao("Valid Description")
                .realizado(false)
                .prioridade(10)
                .build();
        todoRequest = TodoRequest.builder()
                .nome(todo.getNome())
                .descricao(todo.getDescricao())
                .prioridade(todo.getPrioridade())
                .realizado(todo.isRealizado())
                .build();
    }

    @Test
    public void canFindAllTodosTest() {
        List<Todo> todoList = new ArrayList<>();
        todoList.add(todo);
        todoList.add(Todo.builder()
                .id(2)
                .nome("Valid TODO")
                .descricao("Valid Description")
                .realizado(false)
                .prioridade(10)
                .build());

        int page = 0;
        int pageSize = 10;
        String sortBy = "id";

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(sortBy));
        Page<Todo> mockedPage = new PageImpl<>(todoList, pageable, todoList.size());
        when(todoRepository.findAll(pageable)).thenReturn(mockedPage);

        Page<Todo> result = todoService.findAllTodos(page, pageSize, sortBy);

        assertNotNull(result);
        assertEquals(result.getContent(), todoList);
    }

    @Test
    public void cannotFindAllTodosTests() {
        int page = 0;
        int pageSize = 10;
        String sortBy = "id";

        Page<Todo> pageList = new PageImpl<>(new ArrayList<>());
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(sortBy));

        when(todoRepository.findAll(pageable)).thenReturn(pageList);

        assertThrows(TodoNotFoundException.class, () -> todoService.findAllTodos(page, pageSize, sortBy), "Nenhuma tarefa encontrada.");
    }

    @Test
    public void canCreateTodoTest() {
        when(todoService.createTodo(todoRequest)).thenReturn(todo);

        Todo result = todoService.createTodo(todoRequest);

        assertNotNull(result);
        assertEquals(todo.getNome(), result.getNome());

        verify(todoRepository, times(1)).save(argThat(
                todoToSave -> todoToSave.getNome().equals(todo.getNome()) &&
                        todoToSave.getDescricao().equals(todo.getDescricao())));
    }

    @Test
    public void canUpdateTodoTest() {
        TodoRequest request = TodoRequest.builder()
                .nome("Valid TODO Update")
                .descricao("Valid Description Update")
                .realizado(false)
                .prioridade(10).build();

        todo.setNome("Valid TODO Update");
        todo.setDescricao("Valid Description Update");
        todo.setRealizado(false);
        todo.setPrioridade(10);

        when(todoRepository.findById(todo.getId())).thenReturn(Optional.ofNullable(todo));
        when(todoRepository.save(todo)).thenReturn(todo);

        Todo result = todoService.updateTodo(1, request);

        assertNotNull(result);
        assertEquals(todo.getNome(), result.getNome());
        assertEquals(todo.getDescricao(), result.getDescricao());

        verify(todoRepository, times(1)).save(argThat(
                todo1 -> todo1.getNome().equals(todo.getNome()) &&
                        todo1.getDescricao().equals(todo.getDescricao())));
    }

    @Test
    public void cannotUpdateTodoTest() {
        assertThrows(TodoNotFoundException.class, () -> todoService.updateTodo(1, null),
                "Tarefa de id '1' não encontrada.");
    }

    @Test
    public void canDeleteTodoTest() {
        int todoId = 1;

        when(todoRepository.findById(todoId)).thenReturn(Optional.ofNullable(todo));
        doNothing().when(todoRepository).delete(todo);

        assertAll(() -> todoService.deleteTodoById(todoId));
    }

    @Test
    public void cannotDeleteTodoTest() {
        assertThrows(TodoNotFoundException.class, () -> todoService.deleteTodoById(1),
                "Tarefa de id '1' não encontrada.");
    }
}
