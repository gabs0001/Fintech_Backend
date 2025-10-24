package br.com.fintech.model;

import jakarta.persistence.*;

@Entity
@Table(name = "T_SIF_TIPO_INVESTIMENTO")
public class TipoInvestimento {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "SEQ_SIF_TIPO_INVESTIMENTO"
    )
    @SequenceGenerator(
            name = "SEQ_SIF_TIPO_INVESTIMENTO",
            sequenceName = "SEQ_SIF_TIPO_INVESTIMENTO",
            allocationSize = 1
    )
    private Long id;

    @Column(name = "DES_TIPO_INVESTIMENTO", length = 100, nullable = false)
    private String descricao;

    public TipoInvestimento() {}

    public TipoInvestimento(Long id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getDescricao() { return descricao; }

    public void setDescricao(String descricao) { this.descricao = descricao; }
}