package br.com.fintech.model;

import jakarta.persistence.*;

@Entity
@Table(name = "T_SIF_CATEGORIA_GASTO")
public class CategoriaGasto {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "SEQ_SIF_CATEGORIA_GASTO"
    )
    @SequenceGenerator(
            name = "SEQ_SIF_CATEGORIA_GASTO",
            sequenceName = "SEQ_SIF_CATEGORIA_GASTO",
            allocationSize = 1
    )
    private Long id;

    @Column(name = "DES_CATEGORIA_GASTO", length = 100, nullable = false)
    private String descricao;

    public CategoriaGasto() { }

    public CategoriaGasto(Long id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public Long getId() { return this.id; }

    public String getDescricao() { return this.descricao; }

    public void setId(Long id) { this.id = id; }

    public void setDescricao(String descricao) { this.descricao = descricao; }
}