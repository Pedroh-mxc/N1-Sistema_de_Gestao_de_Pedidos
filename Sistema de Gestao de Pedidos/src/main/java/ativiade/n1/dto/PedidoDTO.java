package ativiade.n1.dto;

import ativiade.n1.model.StatusPedido;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PedidoDTO {

    public record Request(
            @NotNull(message = "Cliente é obrigatório")
            Long clienteId,

            @NotNull(message = "Endereço é obrigatório")
            Long enderecoId,

            @NotEmpty(message = "Pedido deve ter ao menos um item")
            @Valid
            List<ItemPedidoDTO.Request> itens
    ) {}

    public record Response(
            Long id,
            LocalDateTime data,
            StatusPedido status,
            BigDecimal total,
            Long clienteId,
            String nomeCliente,
            Long enderecoId,
            List<ItemPedidoDTO.Response> itens
    ) {}

    public record AtualizarStatusRequest(
            @NotNull(message = "Status é obrigatório")
            StatusPedido status
    ) {}
}
