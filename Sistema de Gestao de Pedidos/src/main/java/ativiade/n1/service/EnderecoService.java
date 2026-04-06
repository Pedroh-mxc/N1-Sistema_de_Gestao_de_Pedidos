package ativiade.n1.service;

import ativiade.n1.dto.EnderecoDTO;
import ativiade.n1.dto.EcommerceMapper;
import ativiade.n1.exception.RecursoNaoEncontradoException;
import ativiade.n1.model.Cliente;
import ativiade.n1.model.Endereco;
import ativiade.n1.repository.EnderecoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EnderecoService {

    private final EnderecoRepository enderecoRepository;
    private final ClienteService clienteService;
    private final EcommerceMapper mapper;

    public EnderecoService(EnderecoRepository enderecoRepository,
                           ClienteService clienteService,
                           EcommerceMapper mapper) {
        this.enderecoRepository = enderecoRepository;
        this.clienteService = clienteService;
        this.mapper = mapper;
    }

    @Transactional
    public EnderecoDTO.Response criar(EnderecoDTO.Request request) {
        Cliente cliente = clienteService.buscarClienteOuLancarErro(request.clienteId());

        Endereco endereco = new Endereco(
                request.rua(),
                request.cidade(),
                request.cep(),
                cliente
        );

        Endereco salvo = enderecoRepository.save(endereco);
        return mapper.toEnderecoResponse(salvo);
    }

    @Transactional(readOnly = true)
    public List<EnderecoDTO.Response> listarPorCliente(Long clienteId) {
        clienteService.buscarClienteOuLancarErro(clienteId);
        return enderecoRepository.findByClienteId(clienteId)
                .stream()
                .map(mapper::toEnderecoResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public EnderecoDTO.Response buscarPorId(Long id) {
        Endereco endereco = buscarEnderecoOuLancarErro(id);
        return mapper.toEnderecoResponse(endereco);
    }

    @Transactional
    public void deletar(Long id) {
        buscarEnderecoOuLancarErro(id);
        enderecoRepository.deleteById(id);
    }

    public Endereco buscarEnderecoOuLancarErro(Long id) {
        return enderecoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Endereço não encontrado com id: " + id));
    }
}
