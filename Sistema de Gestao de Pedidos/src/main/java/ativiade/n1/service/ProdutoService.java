package ativiade.n1.service;

import ativiade.n1.dto.EcommerceMapper;
import ativiade.n1.dto.ProdutoDTO;
import ativiade.n1.exception.RecursoNaoEncontradoException;
import ativiade.n1.model.Produto;
import ativiade.n1.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final EcommerceMapper mapper;

    public ProdutoService(ProdutoRepository produtoRepository, EcommerceMapper mapper) {
        this.produtoRepository = produtoRepository;
        this.mapper = mapper;
    }

    @Transactional
    public ProdutoDTO.Response criar(ProdutoDTO.Request request) {
        Produto produto = mapper.toProdutoEntity(request);
        Produto salvo = produtoRepository.save(produto);
        return mapper.toProdutoResponse(salvo);
    }

    @Transactional(readOnly = true)
    public List<ProdutoDTO.Response> listarTodos() {
        return produtoRepository.findAll()
                .stream()
                .map(mapper::toProdutoResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProdutoDTO.Response buscarPorId(Long id) {
        Produto produto = buscarProdutoOuLancarErro(id);
        return mapper.toProdutoResponse(produto);
    }

    @Transactional
    public ProdutoDTO.Response atualizar(Long id, ProdutoDTO.Request request) {
        Produto produto = buscarProdutoOuLancarErro(id);

        produto.setNome(request.nome());
        produto.setPreco(request.preco());
        produto.setEstoque(request.estoque());

        Produto atualizado = produtoRepository.save(produto);
        return mapper.toProdutoResponse(atualizado);
    }

    @Transactional
    public void deletar(Long id) {
        buscarProdutoOuLancarErro(id);
        produtoRepository.deleteById(id);
    }

    public Produto buscarProdutoOuLancarErro(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado com id: " + id));
    }
}
