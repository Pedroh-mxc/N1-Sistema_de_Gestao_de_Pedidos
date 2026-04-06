package ativiade.n1.controller;

import ativiade.n1.dto.PedidoDTO;
import ativiade.n1.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<PedidoDTO.Response> criar(@RequestBody @Valid PedidoDTO.Request request) {
        PedidoDTO.Response response = pedidoService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PedidoDTO.Response>> listarTodos() {
        return ResponseEntity.ok(pedidoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO.Response> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.buscarPorId(id));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<PedidoDTO.Response>> listarPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(pedidoService.listarPorCliente(clienteId));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PedidoDTO.Response> atualizarStatus(
            @PathVariable Long id,
            @RequestBody @Valid PedidoDTO.AtualizarStatusRequest request) {
        return ResponseEntity.ok(pedidoService.atualizarStatus(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        pedidoService.cancelar(id);
        return ResponseEntity.noContent().build();
    }
}
