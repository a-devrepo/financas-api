package br.com.financasweb.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nome", length = 50, nullable = false)
    private String nome;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "categoria")
    private List<Movimentacao> movimentacoes;
}

