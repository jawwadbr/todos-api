package com.jawbr.todos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jawbr.todos.dto.request.TodoRequest;
import com.jawbr.todos.entity.Todo;
import com.jawbr.todos.exception.TodoNotFoundException;
import com.jawbr.todos.service.TodoService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@AutoConfigureMockMvc
public class TodoControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoService todoService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String PATH = "/api/todos";

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
    public void canFindAllTodosTest() throws Exception {
        List<Todo> list = Collections.singletonList(todo);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Todo> page = new PageImpl<>(list, pageable, list.size());

        when(todoService.findAllTodos(0, 10, "id")).thenReturn(page);

        mockMvc.perform(MockMvcRequestBuilders.get(PATH + "?page=0&pageSize=10&sortBy=id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(todo.getId())))
                .andExpect(jsonPath("$.content[0].nome", is(todo.getNome())))
                .andExpect(jsonPath("$.content[0].descricao", is(todo.getDescricao())))
                .andExpect(jsonPath("$.content[0].prioridade", is(todo.getPrioridade())))
                .andExpect(jsonPath("$.content[0].realizado", is(todo.isRealizado())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void cannotFindAllTodosTest() throws Exception {
        when(todoService.findAllTodos(0, 10, "id")).thenThrow(new TodoNotFoundException("Nenhuma tarefa encontrada"));

        mockMvc.perform(MockMvcRequestBuilders.get(PATH + "?page=0&pageSize=10&sortBy=id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof TodoNotFoundException))
                .andExpect(result -> Assertions.assertEquals("Nenhuma tarefa encontrada", Objects.requireNonNull(result.getResolvedException()).getMessage()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void canCreateTodoTest() throws Exception {
        when(todoService.createTodo(todoRequest)).thenReturn(todo);

        String todoJson = objectMapper.writeValueAsString(todo);

        mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(todoJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome", is(todo.getNome())))
                .andExpect(jsonPath("$.descricao", is(todo.getDescricao())))
                .andExpect(jsonPath("$.prioridade", is(todo.getPrioridade())))
                .andExpect(jsonPath("$.realizado", is(todo.isRealizado())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void cannotCreateTodoTest() throws Exception {
        TodoRequest request = TodoRequest.builder()
                .nome("")
                .descricao(todo.getDescricao())
                .prioridade(todo.getPrioridade())
                .realizado(todo.isRealizado())
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Nome da tarefa não pode estar vazia!")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void canUpdateTodoTest() throws Exception {

        when(todoService.updateTodo(1, todoRequest)).thenReturn(todo);

        mockMvc.perform(MockMvcRequestBuilders.put(PATH + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todoRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is(todo.getNome())))
                .andExpect(jsonPath("$.descricao", is(todo.getDescricao())))
                .andExpect(jsonPath("$.prioridade", is(todo.getPrioridade())))
                .andExpect(jsonPath("$.realizado", is(todo.isRealizado())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void cannotUpdateTodoTest() throws Exception {
        when(todoService.updateTodo(2, todoRequest)).thenThrow(new TodoNotFoundException("Tarefa de id '2' não encontrada."));

        mockMvc.perform(MockMvcRequestBuilders.put(PATH + "/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todoRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Tarefa de id '2' não encontrada.")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void canDeleteTodoTest() throws Exception {
        doNothing().when(todoService).deleteTodoById(1);

        mockMvc.perform(MockMvcRequestBuilders.delete(PATH + "/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(MockMvcResultHandlers.print());

        verify(todoService, times(1)).deleteTodoById(1);
    }

    @Test
    public void cannotDeleteTodoTest() throws Exception {
        doThrow(new TodoNotFoundException("Tarefa de id '2' não encontrada.")).when(todoService).deleteTodoById(2);

        mockMvc.perform(MockMvcRequestBuilders.delete(PATH + "/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Tarefa de id '2' não encontrada.")))
                .andDo(MockMvcResultHandlers.print());

        verify(todoService, times(1)).deleteTodoById(2);
    }
}
