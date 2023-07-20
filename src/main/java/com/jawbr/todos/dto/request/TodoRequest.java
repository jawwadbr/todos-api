package com.jawbr.todos.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TodoRequest(
        @NotBlank(message = "Nome da tarefa não pode estar vazia!") String nome,
        @NotBlank(message = "Descrição da tarefa não pode estar vazia!") String descricao,
        boolean realizado,
        int prioridade
) {
}
