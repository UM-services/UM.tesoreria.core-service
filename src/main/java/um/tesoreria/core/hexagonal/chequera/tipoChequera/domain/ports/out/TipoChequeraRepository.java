package um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.ports.out;

import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera;

import java.util.List;
import java.util.Optional;

public interface TipoChequeraRepository {
    List<TipoChequera> findAll();
    List<TipoChequera> findAllAsignable(Integer facultadId, Integer lectivoId, Integer geograficaId, Integer claseChequeraId);
    List<TipoChequera> findAllByFacultadIdAndGeograficaId(Integer facultadId, Integer geograficaId);
    List<TipoChequera> findAllByClaseChequeraId(Integer claseChequeraId);
    List<TipoChequera> findAllByClaseChequeraIds(List<Integer> claseChequeraIds);
    Optional<TipoChequera> findById(Integer tipoChequeraId);
    Optional<TipoChequera> findLast();
    TipoChequera create(TipoChequera tipoChequera);
    TipoChequera update(TipoChequera tipoChequera);
    void deleteById(Integer tipoChequeraId);
    void unmark();
    TipoChequera mark(Integer tipoChequeraId, Byte imprimir);
}
