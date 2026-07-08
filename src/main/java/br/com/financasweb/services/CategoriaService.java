package br.com.financasweb.services;

import br.com.financasweb.dtos.CategoriaRequest;
import br.com.financasweb.dtos.CategoriaResponse;
import br.com.financasweb.entities.Categoria;
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

        categoria = categoriaRepository.save(categoria);

        return new CategoriaResponse(categoria.getId(), categoria.getNome());
    }
}
