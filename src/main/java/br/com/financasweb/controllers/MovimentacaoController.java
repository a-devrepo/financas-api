package br.com.financasweb.controllers;

import br.com.financasweb.dtos.MovimentacaoRequest;
import br.com.financasweb.exceptions.RegistroNaoEncontradoException;
import br.com.financasweb.exceptions.ValidacaoException;
import br.com.financasweb.services.MovimentacaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/movimentacoes")
public class MovimentacaoController {

    private MovimentacaoService movimentacaoService;

    public MovimentacaoController(MovimentacaoService movimentacaoService) {
        this.movimentacaoService = movimentacaoService;
    }

    @PostMapping
    public ResponseEntity<?> post(@RequestBody MovimentacaoRequest request) {

        try {
            var response = movimentacaoService.criar(request);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RegistroNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (ValidacaoException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> get(
            @RequestParam LocalDate dataInicio,
            @RequestParam LocalDate dataFim,
            @RequestParam(defaultValue = "0") int pageIndex,
            @RequestParam(defaultValue = "25") int pageSize) {

        try {
            var response = movimentacaoService.consultar(dataInicio, dataFim, pageIndex, pageSize);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (ValidacaoException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable UUID id) {

        try {
            var response = movimentacaoService.obterPorId(id);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (RegistroNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable UUID id, @RequestBody MovimentacaoRequest request) {

        try {
            var response = movimentacaoService.alterar(id, request);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (RegistroNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ValidacaoException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {

        try {
            var response = movimentacaoService.excluir(id);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (RegistroNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
