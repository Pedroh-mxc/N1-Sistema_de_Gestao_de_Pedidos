package ativiade.n1.dto;

import jakarta.validation.constraints.NotBlank;

public class EnderecoDTO {

    public record Request(
            @NotBlank(message = "Rua é obrigatória")
            String rua,

            @NotBlank(message = "Cidade é obrigatória")
            String cidade,

            @NotBlank(message = "CEP é obrigatório")
            String cep,

            Long clienteId
    ) {}

    public record Response(
            Long id,
            String rua,
            String cidade,
            String cep,
            Long clienteId
    ) {}
}
