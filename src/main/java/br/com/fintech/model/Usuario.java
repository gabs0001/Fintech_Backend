package br.com.fintech.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@JsonIgnoreProperties({" hibernateLazyInitializer", "handler" })
@Table(name = "T_SIF_USUARIO")
public class Usuario {
    @Id
    @Column(name = "COD_USUARIO")
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "SEQ_SIF_USUARIO"
    )
    @SequenceGenerator(
            name = "SEQ_SIF_USUARIO",
            sequenceName = "SEQ_SIF_USUARIO",
            allocationSize = 1
    )
    private Long id;

    @Column(name = "NOM_USUARIO", length = 100, nullable = false)
    private String nome;

    @Column(name = "DAT_NASCIMENTO")
    private LocalDate dataNascimento;

    @Column(name = "DES_GENERO", length = 10)
    private String genero;

    @Column(name = "TXT_EMAIL", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "TXT_SENHA", nullable = false, length = 150)
    private String senha;

    public Usuario() {}

    public Usuario(Long id, String nome, LocalDate dataNascimento, String genero, String email, String senha) {
        this.id = id;
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.genero = genero;
        this.email = email;
        this.senha = senha;
    }

    public Long getId() { return this.id; }

    public String getNome() { return this.nome; }

    public LocalDate getDataNascimento() { return this.dataNascimento; }

    public String getGenero() { return this.genero; }

    public String getEmail() { return this.email; }

    public String getSenha() { return this.senha; }

    public void setId(Long id) { this.id = id; }

    public void setNome(String nome) { this.nome = nome; }

    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public void setGenero(String genero) { this.genero = genero; }

    public void setEmail(String email) { this.email = email; }

    public void setSenha(String senha) { this.senha = senha; }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + this.id +
                ", nome=" + this.nome +
                ", dataNascimento=" + this.dataNascimento +
                ", genero='" + this.genero + '\'' +
                ", email='" + this.email + '\'' +
                '}';
    }
}