package br.com.financasweb.dtos;

import java.util.UUID;

public record CategoriaResponse(
        UUID id,
        String nome
) {
}
