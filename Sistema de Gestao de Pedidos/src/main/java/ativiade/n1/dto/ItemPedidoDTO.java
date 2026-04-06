package ativiade.n1.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class ItemPedidoDTO {

    public record Request(
            @NotNull(message = "Produto é obrigatório")
            Long produtoId,

            @NotNull(message = "Quantidade é obrigatória")
            @Min(value = 1, message = "Quantidade mínima é 1")
            Integer quantidade
    ) {}

    public record Response(
            Long id,
            Long produtoId,
            String nomeProduto,
            Integer quantidade,
            BigDecimal precoUnitario,
            BigDecimal subtotal
    ) {}
}
