package br.com.financasweb.dtos;

import java.time.LocalDate;
import java.util.UUID;

public record MovimentacaoRequest(
        String nome,
        LocalDate data,
        Double valor,
        String tipo,
        UUID categoriaId
) {
}
