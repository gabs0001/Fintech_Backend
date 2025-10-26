package br.com.fintech.service;

import br.com.fintech.exceptions.EntityNotFoundException;
import br.com.fintech.model.Gasto;
import br.com.fintech.repository.GastoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class GastoService extends CrudService<Gasto, Long> {
    private final CategoriaGastoService categoriaGastoService;
    private final GastoRepository gastoRepository;

    public GastoService(GastoRepository gastoRepository, CategoriaGastoService categoriaGastoService) {
        super(gastoRepository);
        this.gastoRepository = gastoRepository;
        this.categoriaGastoService = categoriaGastoService;
    }

    private void validarGasto(Gasto gasto) throws IllegalArgumentException, EntityNotFoundException {
        if(!gasto.validarValor()) {
            throw new IllegalArgumentException("Erro: O valor do gasto deve ser maior que zero!");
        }

        if(gasto.getDataGasto() == null || gasto.getDataGasto().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Erro: A data do gasto não pode ser uma data futura!");
        }

        if(gasto.getDescricao() == null || gasto.getDescricao().trim().isEmpty()) {
            throw new IllegalArgumentException("Erro: A descrição do gasto é obrigatória e não pode estar em branco!");
        }

        if(gasto.getCategoriaGastoId() == null) {
            throw new IllegalArgumentException("Erro: A Categoria do gasto é obrigatória!");
        }

        categoriaGastoService.fetchOrThrowException(gasto.getCategoriaGastoId());
    }

    public Gasto insert(Gasto novoGasto) throws IllegalArgumentException, EntityNotFoundException {
        validarGasto(novoGasto);
        return super.save(novoGasto);
    }

    public Gasto update(Long ownerId, Gasto gastoParaAlterar) throws EntityNotFoundException, IllegalArgumentException {
        validarGasto(gastoParaAlterar);

        if(gastoParaAlterar.getId() == null) {
            throw new IllegalArgumentException("Erro: ID do gasto a ser atualizado é obrigatório.");
        }

        super.fetchOrThrowExceptionByOwner(gastoParaAlterar.getId(), ownerId);

        return super.save(gastoParaAlterar);
    }

    public void remove(Long idEntity, Long ownerId) throws EntityNotFoundException {
        super.deleteByIdAndOwnerId(idEntity, ownerId);
    }

    public Gasto getById(Long idEntity, Long ownerId) throws EntityNotFoundException {
        return super.fetchOrThrowExceptionByOwner(idEntity, ownerId);
    }

    public List<Gasto> getUltimos(Long userId, int limite) {
        return gastoRepository.findTopNByUsuarioIdOrderByDataGastoDesc(userId, limite);
    }

    public BigDecimal calcularTotal(Long userId) {
        return gastoRepository.calcularTotal(userId);
    }

    public BigDecimal calcularTotalPeriodo(Long userId, LocalDate inicio, LocalDate fim) {
        return gastoRepository.calcularTotalPeriodo(userId, inicio, fim);
    }
}