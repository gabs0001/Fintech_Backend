package br.com.fintech.service;

import br.com.fintech.model.TipoRecebimento;
import br.com.fintech.repository.TipoRecebimentoRepository;
import org.springframework.stereotype.Service;

@Service
public class TipoRecebimentoService extends CategoriaBaseService<TipoRecebimento, Long> {
    public TipoRecebimentoService(TipoRecebimentoRepository repository) {
        super(repository);
    }

    @Override
    protected void validar(TipoRecebimento categoria) throws IllegalArgumentException {
        if(categoria.getDescricao() == null || categoria.getDescricao().trim().isEmpty()) {
            throw new IllegalArgumentException("Erro: A descrição da categoria de Recebimento é obrigatória e não pode estar em branco!");
        }
    }
}