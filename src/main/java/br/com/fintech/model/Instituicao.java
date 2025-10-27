package br.com.fintech.model;

import jakarta.persistence.*;

@Entity
@Table(name = "T_SIF_INSTITUICAO")
public class Instituicao {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "SEQ_SIF_INSTUICAO"
    )
    @SequenceGenerator(
            name = "SEQ_SIF_INSTUICAO",
            sequenceName = "SEQ_SIF_INSTUICAO",
            allocationSize = 1
    )
    private Long id;

    @Column(name = "NOM_INSTITUICAO", length = 100, nullable = false)
    private String nome;

    public Instituicao() {}

    public Instituicao(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Long getId() { return this.id; }

    public String getNome() { return this.nome; }

    public void setId(Long id) { this.id = id; }

    public void setNome(String nome) { this.nome = nome; }
}