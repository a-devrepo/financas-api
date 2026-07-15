package br.com.financasweb.controllers;

import br.com.financasweb.dtos.CategoriaRequest;
import br.com.financasweb.exceptions.RegistroNaoEncontradoException;
import br.com.financasweb.exceptions.ValidacaoException;
import br.com.financasweb.services.CategoriaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categorias")
public class CategoriaController {

    private CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @PostMapping
    public ResponseEntity<?> post(@RequestBody CategoriaRequest request) {

        try {
            var response = categoriaService.cadastrar(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (ValidacaoException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> get() {

        var response = categoriaService.consultar();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable UUID id) {

        try {
            var response = categoriaService.obterPorId(id);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (RegistroNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable UUID id, @RequestBody CategoriaRequest request) {

        try {
            var response = categoriaService.alterar(id, request);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (ValidacaoException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RegistroNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {

        try {
            var response = categoriaService.excluir(id);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (RegistroNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
