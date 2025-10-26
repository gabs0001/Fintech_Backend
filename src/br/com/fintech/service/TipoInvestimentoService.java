package br.com.fintech.service;

import br.com.fintech.model.TipoInvestimento;
import br.com.fintech.repository.TipoInvestimentoRepository;
import org.springframework.stereotype.Service;

@Service
public class TipoInvestimentoService extends CategoriaBaseService<TipoInvestimento, Long> {
    public TipoInvestimentoService(TipoInvestimentoRepository repository) {
        super(repository);
    }

    @Override
    protected void validar(TipoInvestimento categoria) throws IllegalArgumentException {
        if (categoria.getDescricao() == null || categoria.getDescricao().trim().isEmpty()) {
            throw new IllegalArgumentException("Erro: A descrição da categoria de Investimento é obrigatória e não pode estar em branco!");
        }
    }
}
