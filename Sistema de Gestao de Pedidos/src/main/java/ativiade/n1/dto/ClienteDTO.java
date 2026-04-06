package ativiade.n1.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ClienteDTO {

    public record Request(
            @NotBlank(message = "Nome é obrigatório")
            String nome,

            @NotBlank(message = "Email é obrigatório")
            @Email(message = "Email inválido")
            String email
    ) {}

    public record Response(
            Long id,
            String nome,
            String email
    ) {}
}
