package ativiade.n1.dto;

import ativiade.n1.model.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class EcommerceMapper {

    // ========================
    // Cliente
    // ========================

    public ClienteDTO.Response toClienteResponse(Cliente cliente) {
        return new ClienteDTO.Response(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail()
        );
    }

    public Cliente toClienteEntity(ClienteDTO.Request request) {
        return new Cliente(request.nome(), request.email());
    }

    // ========================
    // Endereco
    // ========================

    public EnderecoDTO.Response toEnderecoResponse(Endereco endereco) {
        return new EnderecoDTO.Response(
                endereco.getId(),
                endereco.getRua(),
                endereco.getCidade(),
                endereco.getCep(),
                endereco.getCliente().getId()
        );
    }

    // ========================
    // Produto
    // ========================

    public ProdutoDTO.Response toProdutoResponse(Produto produto) {
        return new ProdutoDTO.Response(
                produto.getId(),
                produto.getNome(),
                produto.getPreco(),
                produto.getEstoque()
        );
    }

    public Produto toProdutoEntity(ProdutoDTO.Request request) {
        return new Produto(request.nome(), request.preco(), request.estoque());
    }

    // ========================
    // ItemPedido
    // ========================

    public ItemPedidoDTO.Response toItemPedidoResponse(ItemPedido item) {
        BigDecimal subtotal = item.getPrecoUnitario()
                .multiply(BigDecimal.valueOf(item.getQuantidade()));

        return new ItemPedidoDTO.Response(
                item.getId(),
                item.getProduto().getId(),
                item.getProduto().getNome(),
                item.getQuantidade(),
                item.getPrecoUnitario(),
                subtotal
        );
    }

    // ========================
    // Pedido
    // ========================

    public PedidoDTO.Response toPedidoResponse(Pedido pedido) {
        List<ItemPedidoDTO.Response> itensResponse = pedido.getItens()
                .stream()
                .map(this::toItemPedidoResponse)
                .toList();

        return new PedidoDTO.Response(
                pedido.getId(),
                pedido.getData(),
                pedido.getStatus(),
                pedido.getTotal(),
                pedido.getCliente().getId(),
                pedido.getCliente().getNome(),
                pedido.getEndereco().getId(),
                itensResponse
        );
    }
}
