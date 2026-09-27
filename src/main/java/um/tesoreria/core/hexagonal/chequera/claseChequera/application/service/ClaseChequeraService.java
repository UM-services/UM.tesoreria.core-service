package um.tesoreria.core.hexagonal.chequera.claseChequera.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.chequera.claseChequera.application.exception.ClaseChequeraException;
import um.tesoreria.core.hexagonal.chequera.claseChequera.domain.model.ClaseChequera;
import um.tesoreria.core.hexagonal.chequera.claseChequera.domain.ports.in.*;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClaseChequeraService {

    private final GetAllClaseChequeraUseCase getAllClaseChequeraUseCase;
    private final GetClaseChequeraByIdUseCase getClaseChequeraByIdUseCase;
    private final GetAllClaseChequeraByPosgradoUseCase getAllClaseChequeraByPosgradoUseCase;
    private final GetAllClaseChequeraByCursoUseCase getAllClaseChequeraByCursoUseCase;
    private final GetAllClaseChequeraByTituloUseCase getAllClaseChequeraByTituloUseCase;
    private final GetAllClaseChequeraByTramiteUseCase getAllClaseChequeraByTramiteUseCase;

    public List<ClaseChequera> findAll() {
        return getAllClaseChequeraUseCase.getAllClaseChequera();
    }

    public Optional<ClaseChequera> findById(Integer claseChequeraId) {
        return getClaseChequeraByIdUseCase.getClaseChequeraById(claseChequeraId);
    }

    public List<ClaseChequera> findAllByPosgrado() {
        return getAllClaseChequeraByPosgradoUseCase.getAllClaseChequeraByPosgrado();
    }

    public List<ClaseChequera> findAllByCurso() {
        return getAllClaseChequeraByCursoUseCase.getAllClaseChequeraByCurso();
    }

    public List<ClaseChequera> findAllByTitulo() {
        return getAllClaseChequeraByTituloUseCase.getAllClaseChequeraByTitulo();
    }

    public List<ClaseChequera> findAllByTramite() {
        return getAllClaseChequeraByTramiteUseCase.getAllClaseChequeraByTramite();
    }

}
