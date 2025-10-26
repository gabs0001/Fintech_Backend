package br.com.fintech.service;

import br.com.fintech.model.CategoriaGasto;
import br.com.fintech.repository.CategoriaGastoRepository;
import org.springframework.stereotype.Service;

@Service
public class CategoriaGastoService extends CategoriaBaseService<CategoriaGasto, Long> {
    public CategoriaGastoService(CategoriaGastoRepository repository) {
        super(repository);
    }

    @Override
    protected void validar(CategoriaGasto categoria) throws IllegalArgumentException {
        if(categoria.getDescricao() == null || categoria.getDescricao().trim().isEmpty()) {
            throw new IllegalArgumentException("Erro: A descrição da Categoria de Gasto é obrigatória e não pode estar em branco!");
        }
    }
}