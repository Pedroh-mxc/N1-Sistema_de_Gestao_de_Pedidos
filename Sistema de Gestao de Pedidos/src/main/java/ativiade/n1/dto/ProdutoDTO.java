package ativiade.n1.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class ProdutoDTO {

    public record Request(
            @NotBlank(message = "Nome é obrigatório")
            String nome,

            @NotNull(message = "Preço é obrigatório")
            @Positive(message = "Preço deve ser positivo")
            BigDecimal preco,

            @NotNull(message = "Estoque é obrigatório")
            @Min(value = 0, message = "Estoque não pode ser negativo")
            Integer estoque
    ) {}

    public record Response(
            Long id,
            String nome,
            BigDecimal preco,
            Integer estoque
    ) {}
}
