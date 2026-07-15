package br.com.financasweb.services;

import br.com.financasweb.dtos.CategoriaResponse;
import br.com.financasweb.dtos.MovimentacaoRequest;
import br.com.financasweb.dtos.MovimentacaoResponse;
import br.com.financasweb.entities.Categoria;
import br.com.financasweb.entities.Movimentacao;
import br.com.financasweb.enums.TipoMovimentacao;
import br.com.financasweb.exceptions.RegistroNaoEncontradoException;
import br.com.financasweb.exceptions.ValidacaoException;
import br.com.financasweb.repositories.CategoriaRepository;
import br.com.financasweb.repositories.MovimentacaoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class MovimentacaoService {

    private MovimentacaoRepository movimentacaoRepository;

    private CategoriaRepository categoriaRepository;

    public MovimentacaoService(
            MovimentacaoRepository movimentacaoRepository,
            CategoriaRepository categoriaRepository) {
        this.movimentacaoRepository = movimentacaoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public MovimentacaoResponse criar(MovimentacaoRequest request) {

        var categoria = categoriaRepository
                .findById(request.categoriaId())
                .orElseThrow(() -> new RegistroNaoEncontradoException("Categoria não encontrada"));

        var movimentacao = toEntity(request, categoria);

        validarMovimentacao(movimentacao);

        movimentacao = movimentacaoRepository.save(movimentacao);

        return toResponse(movimentacao);
    }

    public MovimentacaoResponse obterPorId(UUID id) {

        var movimentacao = movimentacaoRepository
                .findById(id)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Movimentação não encontrada"));

        return toResponse(movimentacao);
    }

    public MovimentacaoResponse alterar(UUID id, MovimentacaoRequest request) {

        var movimentacao = movimentacaoRepository
                .findById(id)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Movimentação não encontrada"));

        var categoria = categoriaRepository
                .findById(request.categoriaId())
                .orElseThrow(() -> new RegistroNaoEncontradoException("Categoria não encontrada"));

        movimentacao = toEntity(request, categoria);

        validarMovimentacao(movimentacao);

        movimentacao = movimentacaoRepository.save(movimentacao);

        return toResponse(movimentacao);
    }

    public Page<MovimentacaoResponse> consultar(LocalDate dataInicio, LocalDate dataFim, int pageIndex, int pageSize) {

        if (dataInicioEhMaiorQueDataFim(dataInicio, dataFim)) {
            throw new ValidacaoException("A data de início não pode ser maior do que a data de fim");
        }

        if (pageSize > 25) pageSize = 25;
        var pageable = PageRequest.of(pageIndex, pageSize);

        var movimentacoes = movimentacaoRepository.findByData(dataInicio, dataFim, pageable);

        return movimentacoes.map(this::toResponse);
    }

    private boolean dataInicioEhMaiorQueDataFim(LocalDate dataInicio, LocalDate dataFim) {
        return dataInicio.isAfter(dataFim);
    }

    public MovimentacaoResponse excluir(UUID id) {

        var movimentacao = movimentacaoRepository
                .findById(id)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Movimentação não encontrada"));

        movimentacaoRepository.delete(movimentacao);

        return toResponse(movimentacao);
    }

    private void validarMovimentacao(Movimentacao movimentacao) {

        if (nomeEhNullOuVazio(movimentacao.getNome())) {
            throw new ValidacaoException("O nome da movimentação é obrigatório");
        }
        if (nomeNaoPossuiMinimoDeCaracteres(movimentacao.getNome())) {
            throw new ValidacaoException("O nome da movimentação deve ter pelo menos 6 caracteres");
        }
        if (valorEhMenorOuIgualZero(movimentacao.getValor())) {
            throw new ValidacaoException("O valor da movimentação deve ser maior que zero");
        }
        if (tipoMovimentacaoNaoExiste(movimentacao.getTipoMovimentacao())) {
            throw new ValidacaoException("O tipo da movimentação deve ser RECEITA OU DESPESA");
        }
    }

    private boolean nomeEhNullOuVazio(String nome) {
        return nome == null || nome.trim().isEmpty();
    }

    private boolean nomeNaoPossuiMinimoDeCaracteres(String nome) {
        return nome.length() < 6;
    }

    private boolean valorEhMenorOuIgualZero(BigDecimal valor) {
        return valor.doubleValue() <= 0;
    }

    private boolean tipoMovimentacaoNaoExiste(TipoMovimentacao tipoMovimentacao) {
        return !tipoMovimentacao.toString().equals("DESPESA") &&
                !tipoMovimentacao.toString().equals("RECEITA");
    }

    private Movimentacao toEntity(MovimentacaoRequest request, Categoria categoria) {
        var movimentacao = new Movimentacao();
        movimentacao.setNome(request.nome());
        movimentacao.setData(request.data());
        movimentacao.setValor(BigDecimal.valueOf(request.valor()));
        movimentacao.setTipoMovimentacao(TipoMovimentacao.valueOf(request.tipo()));
        movimentacao.setCategoria(categoria);
        return movimentacao;
    }

    private MovimentacaoResponse toResponse(Movimentacao movimentacao) {
        var response = new MovimentacaoResponse(
                movimentacao.getId(),
                movimentacao.getNome(),
                movimentacao.getData(),
                movimentacao.getValor().doubleValue(),
                movimentacao.getTipoMovimentacao().toString(),
                new CategoriaResponse(
                        movimentacao.getCategoria().getId(),
                        movimentacao.getCategoria().getNome()));
        return response;
    }
}