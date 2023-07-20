<h1 align="center">
  To-Do List
</h1>

API para gerenciar tarefas (CRUD) [desse desafio](https://github.com/simplify-liferay/desafio-junior-backend-simplify)
para desenvolvedores backend júnior.

## Tópicos

- [Tecnologias](https://github.com/jawwadbr/todos-api#tecnologias)
- [Como Executar](https://github.com/jawwadbr/todos-api#como-executar)
- [API Endpoints](https://github.com/jawwadbr/todos-api#api-endpoints)

## Tecnologias

- [Java 17](https://docs.oracle.com/en/java/javase/17/)
- [Spring Boot v3.1.1](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://docs.spring.io/spring-data/data-jpa/docs/current/reference/html/#repositories)
- [Lombok](https://projectlombok.org/features/)
- [MySQL](https://dev.mysql.com/doc/)

## Como Executar

- Clonar repositório git
- Construir o projeto:

```
./mvnw clean package
```

- Executar a aplicação:

```
java -jar target/todos-0.0.1-SNAPSHOT.jar
```

A API poderá ser acessada em [localhost:8080](http://localhost:8080).

## API Endpoints

Para fazer as requisições HTTP abaixo, foi utilizado a ferramenta [Postman](https://www.postman.com):

- POST - Criar Tarefa

```
/api/todos

{
    "nome": "Finalizar Projeto",
    "descricao": "Finalizar o projeto <tal>",
    "realizado": 0,
    "prioridade": 5
}
```

- GET - Listar Tarefas

```
/api/todos
```

**Parâmetros**

|       Nome | Requerido |  Tipo  | Descrição                                                                                                                                                                 |
|-----------:|:---------:|:------:|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|     `page` | opcional  |  int   | Número da página. <br/> Padrão: 0                                                                                                                                         |
| `pageSize` | opcional  |  int   | Tamanho da página. Quantas tarefas irão aparecer por página. <br/> Padrão: 10 <br/> Máxima: 50                                                                            |
|   `sortBy` | opcional  | string | Ordenar pelo campo desejado. <br/> Valores suportados: `id`, `nome`, `descricao`, `realizado`, `prioridade`. <br/> <br/> Quando não especificado. <br/> <br/>Padrão: `id` |

Resposta da requisição HTTP GET

```
{
    "content": [
        {
            "id": 1,
            "nome": "Finalizar Projeto",
            "descricao": "Finalizar o projeto <tal>",
            "realizado": false,
            "prioridade": 5
        }
    ],
    "pageable": {
        "sort": {
            "empty": false,
            "sorted": true,
            "unsorted": false
        },
        "offset": 0,
        "pageNumber": 0,
        "pageSize": 10,
        "unpaged": false,
        "paged": true
    },
    "last": true,
    "totalPages": 1,
    "totalElements": 1,
    "size": 10,
    "number": 0,
    "sort": {
        "empty": false,
        "sorted": true,
        "unsorted": false
    },
    "first": true,
    "numberOfElements": 1,
    "empty": false
}
```

- PUT - Atualizar Tarefa

```
/api/todos/1

{
    "nome": "Finalizar Projeto PUT",
    "descricao": "Finalizar o projeto <tal> PUT",
    "realizado": 1,
    "prioridade": 0
}
```

Resposta da requisição HTTP PUT

```
Status 200 OK

{
    "id": 1,
    "nome": "Finalizar Projeto PUT",
    "descricao": "Finalizar o projeto <tal> PUT",
    "realizado": true,
    "prioridade": 0
}
```

- DELETE - Deletar Tarefa

```
/api/todos/1
```

Resposta da requisição HTTP PUT

```
Status 204 No Content
```