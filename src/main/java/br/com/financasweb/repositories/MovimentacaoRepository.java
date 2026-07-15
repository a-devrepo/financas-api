package br.com.financasweb.repositories;

import br.com.financasweb.entities.Movimentacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface MovimentacaoRepository extends JpaRepository<Movimentacao, UUID> {

    @Query("""
                 SELECT m
                 FROM Movimentacao m
                 WHERE m.data BETWEEN :pDataInicio AND :pDataFim
            """)
    Page<Movimentacao> findByData(
            @Param("pDataInicio") LocalDate dataInicio,
            @Param("pDataFim") LocalDate dataFim,
            Pageable paginacao);
}