package br.com.fintech.model;

import jakarta.persistence.*;

@Entity
@Table(name = "T_SIF_TIPO_RECEBIMENTO")
public class TipoRecebimento {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "SEQ_SIF_TIPO_RECEBIMENTO"
    )
    @SequenceGenerator(
            name = "SEQ_SIF_TIPO_RECEBIMENTO",
            sequenceName = "SEQ_SIF_TIPO_RECEBIMENTO",
            allocationSize = 1
    )
    private Long id;

    @Column(name = "DES_TIPO_RECEBIMENTO", length = 100, nullable = false)
    private String descricao;

    public TipoRecebimento() {}

    public TipoRecebimento(Long id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getDescricao() { return descricao; }

    public void setDescricao(String descricao) { this.descricao = descricao; }
}