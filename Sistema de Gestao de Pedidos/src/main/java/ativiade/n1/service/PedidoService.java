package ativiade.n1.service;

import ativiade.n1.dto.EcommerceMapper;
import ativiade.n1.dto.ItemPedidoDTO;
import ativiade.n1.dto.PedidoDTO;
import ativiade.n1.exception.RecursoNaoEncontradoException;
import ativiade.n1.exception.RegraNegocioException;
import ativiade.n1.model.*;
import ativiade.n1.repository.PedidoRepository;
import ativiade.n1.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final ClienteService clienteService;
    private final EnderecoService enderecoService;
    private final EcommerceMapper mapper;

    public PedidoService(PedidoRepository pedidoRepository,
                         ProdutoRepository produtoRepository,
                         ClienteService clienteService,
                         EnderecoService enderecoService,
                         EcommerceMapper mapper) {
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
        this.clienteService = clienteService;
        this.enderecoService = enderecoService;
        this.mapper = mapper;
    }

    @Transactional
    public PedidoDTO.Response criar(PedidoDTO.Request request) {
        Cliente cliente = clienteService.buscarClienteOuLancarErro(request.clienteId());
        Endereco endereco = enderecoService.buscarEnderecoOuLancarErro(request.enderecoId());

        // Valida que o endereço pertence ao cliente
        if (!endereco.getCliente().getId().equals(cliente.getId())) {
            throw new RegraNegocioException("O endereço informado não pertence ao cliente.");
        }

        Pedido pedido = new Pedido(cliente, endereco);

        List<ItemPedido> itens = montarItens(request.itens(), pedido);
        pedido.getItens().addAll(itens);

        // Regra 3 — Total calculado internamente, nunca informado pelo cliente
        pedido.calcularTotal();

        Pedido salvo = pedidoRepository.save(pedido);
        return mapper.toPedidoResponse(salvo);
    }

    @Transactional(readOnly = true)
    public List<PedidoDTO.Response> listarTodos() {
        return pedidoRepository.findAll()
                .stream()
                .map(mapper::toPedidoResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PedidoDTO.Response> listarPorCliente(Long clienteId) {
        clienteService.buscarClienteOuLancarErro(clienteId);
        return pedidoRepository.findByClienteId(clienteId)
                .stream()
                .map(mapper::toPedidoResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PedidoDTO.Response buscarPorId(Long id) {
        Pedido pedido = buscarPedidoOuLancarErro(id);
        return mapper.toPedidoResponse(pedido);
    }

    @Transactional
    public PedidoDTO.Response atualizarStatus(Long id, PedidoDTO.AtualizarStatusRequest request) {
        Pedido pedido = buscarPedidoOuLancarErro(id);

        // Regra 5 — Fluxo de status: CRIADO → PAGO → ENVIADO (sem pular, sem voltar)
        validarTransicaoStatus(pedido.getStatus(), request.status());

        pedido.setStatus(request.status());
        Pedido atualizado = pedidoRepository.save(pedido);
        return mapper.toPedidoResponse(atualizado);
    }

    @Transactional
    public void cancelar(Long id) {
        Pedido pedido = buscarPedidoOuLancarErro(id);

        // Regra 4 — Apenas pedidos CRIADO podem ser cancelados
        if (!StatusPedido.CRIADO.equals(pedido.getStatus())) {
            throw new RegraNegocioException(
                    "Apenas pedidos com status CRIADO podem ser cancelados. Status atual: " + pedido.getStatus()
            );
        }

        // Devolve estoque ao cancelar
        pedido.getItens().forEach(item -> {
            Produto produto = item.getProduto();
            produto.setEstoque(produto.getEstoque() + item.getQuantidade());
            produtoRepository.save(produto);
        });

        pedido.setStatus(StatusPedido.CANCELADO);
        pedidoRepository.save(pedido);
    }

    // ========================
    // Métodos privados
    // ========================

    private List<ItemPedido> montarItens(List<ItemPedidoDTO.Request> itensRequest, Pedido pedido) {
        List<ItemPedido> itens = new ArrayList<>();

        for (ItemPedidoDTO.Request itemRequest : itensRequest) {
            Produto produto = produtoRepository.findById(itemRequest.produtoId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException(
                            "Produto não encontrado com id: " + itemRequest.produtoId()));

            // Regra 2 — Validar disponibilidade de estoque
            if (produto.getEstoque() < itemRequest.quantidade()) {
                throw new RegraNegocioException(
                        "Estoque insuficiente para o produto '" + produto.getNome() +
                        "'. Disponível: " + produto.getEstoque() +
                        ", Solicitado: " + itemRequest.quantidade()
                );
            }

            // Regra 2 — Subtrair estoque
            produto.setEstoque(produto.getEstoque() - itemRequest.quantidade());
            produtoRepository.save(produto);

            ItemPedido item = new ItemPedido(
                    itemRequest.quantidade(),
                    produto.getPreco(), // Regra 3 — preço vem do produto, não do cliente
                    pedido,
                    produto
            );

            itens.add(item);
        }

        return itens;
    }

    private void validarTransicaoStatus(StatusPedido atual, StatusPedido novo) {
        // Não pode cancelar via PATCH (use DELETE)
        if (StatusPedido.CANCELADO.equals(novo)) {
            throw new RegraNegocioException("Para cancelar um pedido, use o endpoint DELETE /pedidos/{id}.");
        }

        // Não pode voltar status
        boolean tentandoVoltar = switch (novo) {
            case CRIADO -> true;
            case PAGO -> !StatusPedido.CRIADO.equals(atual);
            case ENVIADO -> !StatusPedido.PAGO.equals(atual);
            default -> false;
        };

        if (tentandoVoltar) {
            throw new RegraNegocioException(
                    "Transição de status inválida: " + atual + " → " + novo +
                    ". Fluxo permitido: CRIADO → PAGO → ENVIADO."
            );
        }
    }

    private Pedido buscarPedidoOuLancarErro(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado com id: " + id));
    }
}
