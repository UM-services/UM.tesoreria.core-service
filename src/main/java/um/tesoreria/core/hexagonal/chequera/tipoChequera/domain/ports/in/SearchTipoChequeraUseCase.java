package um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequeraSearch;

import java.util.List;

public interface SearchTipoChequeraUseCase {
    List<TipoChequeraSearch> searchTipoChequeras(List<String> conditions);
    List<TipoChequeraSearch> searchTipoChequerasByGeograficaId(List<String> conditions, Integer geograficaId);
}
