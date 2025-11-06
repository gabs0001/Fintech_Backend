package br.com.fintech.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@JsonIgnoreProperties({" hibernateLazyInitializer", "handler" })
@Table(name = "T_SIF_RECEBIMENTO")
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "COD_RECEBIMENTO")),
        @AttributeOverride(name = "usuarioId", column = @Column(name = "COD_USUARIO")),
        @AttributeOverride(name = "descricao", column = @Column(name = "DES_RECEBIMENTO")),
        @AttributeOverride(name = "valor", column = @Column(name = "VAL_RECEBIMENTO"))
})
public class Recebimento extends Transacao {
    @Column(name = "DAT_RECEBIMENTO")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataRecebimento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COD_TIPO_RECEBIMENTO", nullable = false)
    private TipoRecebimento tipoRecebimento;

    public Recebimento() {}

    public Recebimento(Long usuarioId, String descricao, TipoRecebimento tipoRecebimento, BigDecimal valor, LocalDate dataRecebimento) {
        super(usuarioId, descricao, valor);
        this.dataRecebimento = dataRecebimento;
        this.tipoRecebimento = tipoRecebimento;
    }

    public LocalDate getDataRecebimento() { return this.dataRecebimento; }

    public void setDataRecebimento(LocalDate dataRecebimento) { this.dataRecebimento = dataRecebimento; }

    public TipoRecebimento getTipoRecebimento() {
        return this.tipoRecebimento;
    }

    public Long getTipoRecebimentoId() {
        return this.tipoRecebimento != null ? this.tipoRecebimento.getId() : null;
    }

    public void setTipoRecebimento(TipoRecebimento tipoRecebimento) {
        this.tipoRecebimento = tipoRecebimento;
    }

    @Override
    @Transient
    public String getTipoMovimentacao() { return "Recebimento"; }

    @Override
    @Transient
    public int getMultiplicador() { return 1; }

    @Override
    public String toString() {
        return "Recebimento{" +
                super.toString().substring(super.toString().indexOf("{") + 1, super.toString().length() - 1) +
                ", dataRecebimento=" + dataRecebimento +
                ", tipoRecebimento=" + (tipoRecebimento != null ? tipoRecebimento.getDescricao() : "N/A") +
                '}';
    }
}