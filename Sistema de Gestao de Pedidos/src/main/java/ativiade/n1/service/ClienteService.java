package ativiade.n1.service;

import ativiade.n1.dto.ClienteDTO;
import ativiade.n1.dto.EcommerceMapper;
import ativiade.n1.exception.RecursoNaoEncontradoException;
import ativiade.n1.exception.RegraNegocioException;
import ativiade.n1.model.Cliente;
import ativiade.n1.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final EcommerceMapper mapper;

    public ClienteService(ClienteRepository clienteRepository, EcommerceMapper mapper) {
        this.clienteRepository = clienteRepository;
        this.mapper = mapper;
    }

    @Transactional
    public ClienteDTO.Response criar(ClienteDTO.Request request) {
        // Regra 1 — Email único
        if (clienteRepository.existsByEmail(request.email())) {
            throw new RegraNegocioException("Já existe um cliente cadastrado com o email: " + request.email());
        }

        Cliente cliente = mapper.toClienteEntity(request);
        Cliente salvo = clienteRepository.save(cliente);
        return mapper.toClienteResponse(salvo);
    }

    @Transactional(readOnly = true)
    public List<ClienteDTO.Response> listarTodos() {
        return clienteRepository.findAll()
                .stream()
                .map(mapper::toClienteResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClienteDTO.Response buscarPorId(Long id) {
        Cliente cliente = buscarClienteOuLancarErro(id);
        return mapper.toClienteResponse(cliente);
    }

    @Transactional
    public ClienteDTO.Response atualizar(Long id, ClienteDTO.Request request) {
        Cliente cliente = buscarClienteOuLancarErro(id);

        // Regra 1 — Verifica email duplicado apenas se o email mudou
        boolean emailMudou = !cliente.getEmail().equalsIgnoreCase(request.email());
        if (emailMudou && clienteRepository.existsByEmail(request.email())) {
            throw new RegraNegocioException("Já existe um cliente cadastrado com o email: " + request.email());
        }

        cliente.setNome(request.nome());
        cliente.setEmail(request.email());

        Cliente atualizado = clienteRepository.save(cliente);
        return mapper.toClienteResponse(atualizado);
    }

    @Transactional
    public void deletar(Long id) {
        buscarClienteOuLancarErro(id);
        clienteRepository.deleteById(id);
    }

    public Cliente buscarClienteOuLancarErro(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado com id: " + id));
    }
}
