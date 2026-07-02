package um.tesoreria.core.hexagonal.chequera.claseChequera.domain.ports.out;

import um.tesoreria.core.hexagonal.chequera.claseChequera.domain.model.ClaseChequera;

import java.util.List;

public interface ClaseChequeraRepository {

    List<ClaseChequera> findAll();

    List<ClaseChequera> findAllByPosgrado(Byte posgrado);

    List<ClaseChequera> findAllByCurso(Byte curso);

    List<ClaseChequera> findAllByTitulo(Byte titulo);

}
