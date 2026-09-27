package um.tesoreria.core.hexagonal.chequera.claseChequera.domain.ports.out;

import um.tesoreria.core.hexagonal.chequera.claseChequera.domain.model.ClaseChequera;

import java.util.List;
import java.util.Optional;

public interface ClaseChequeraRepository {

    List<ClaseChequera> findAll();

    Optional<ClaseChequera> findByClaseChequeraId(Integer claseChequeraId);

    List<ClaseChequera> findAllByPosgrado(Byte posgrado);

    List<ClaseChequera> findAllByCurso(Byte curso);

    List<ClaseChequera> findAllByTitulo(Byte titulo);

    List<ClaseChequera> findAllByTramite(Byte tramite);

}
