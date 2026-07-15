package br.com.financasweb.dtos;

import java.time.LocalDateTime;

public record ErrorResponse(
        Integer status,
        String mensagem,
        LocalDateTime timeStamp
) {
}