package ativiade.n1.controller;

import ativiade.n1.dto.EnderecoDTO;
import ativiade.n1.service.EnderecoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/enderecos")
public class EnderecoController {

    private final EnderecoService enderecoService;

    public EnderecoController(EnderecoService enderecoService) {
        this.enderecoService = enderecoService;
    }

    @PostMapping
    public ResponseEntity<EnderecoDTO.Response> criar(@RequestBody @Valid EnderecoDTO.Request request) {
        EnderecoDTO.Response response = enderecoService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<EnderecoDTO.Response>> listarPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(enderecoService.listarPorCliente(clienteId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnderecoDTO.Response> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(enderecoService.buscarPorId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        enderecoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
