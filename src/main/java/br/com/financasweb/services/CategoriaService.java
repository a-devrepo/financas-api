package br.com.financasweb.services;

import br.com.financasweb.dtos.CategoriaRequest;
import br.com.financasweb.dtos.CategoriaResponse;
import br.com.financasweb.entities.Categoria;
import br.com.financasweb.exceptions.ValidacaoException;
import br.com.financasweb.repositories.CategoriaRepository;
import org.springframework.stereotype.Service;

@Service
public class CategoriaService {

    private CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public CategoriaResponse cadastrar(CategoriaRequest request) {

        var categoria = new Categoria();

        categoria.setNome(request.nome());

        validarCategoria(categoria);

        categoria = categoriaRepository.save(categoria);

        return new CategoriaResponse(categoria.getId(), categoria.getNome());
    }

    private void validarCategoria(Categoria categoria) {

        if (nomeCategoriaEhNulOuVazio(categoria))
            throw new ValidacaoException("O nome da categoria é obrigatório.");

        if (nomeCategoriaNaoPossuiMinimoDeCaracteres(categoria))
            throw new ValidacaoException("O nome da categoria deve ter no mínimo 6 caracteres.");
    }

    private boolean nomeCategoriaEhNulOuVazio(Categoria categoria) {
        return categoria.getNome() == null || categoria.getNome().trim().isEmpty();
    }

    private boolean nomeCategoriaNaoPossuiMinimoDeCaracteres(Categoria categoria) {
        return categoria.getNome().length() < 6;
    }
}
