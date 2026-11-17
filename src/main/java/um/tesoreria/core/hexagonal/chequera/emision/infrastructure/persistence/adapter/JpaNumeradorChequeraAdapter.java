package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.chequera.emision.application.exception.EmisionException;
import um.tesoreria.core.hexagonal.chequera.emision.domain.ports.out.NumeradorChequeraPort;
import um.tesoreria.core.hexagonal.chequera.emision.domain.service.ReglasEmision;
import um.tesoreria.core.model.ChequeraSerieControl;
import um.tesoreria.core.repository.ChequeraSerieControlRepository;

import java.util.Objects;

/**
 * Numeración de chequeras equivalente a {@code clsDAOCheqSerie.numChequera}
 * ({@code clsDAOCheqSerie.cls:1960}).
 *
 * <p>El legacy tomaba el máximo de {@code chequera_serie_control} por facultad y tipo,
 * verificaba que el número no estuviera usado y registraba el control. Acá se agrega un
 * bloqueo pesimista sobre la última fila de control para evitar dos reservas simultáneas;
 * si la tabla todavía no tiene filas para ese par, la unique constraint sigue siendo la
 * última defensa y el conflicto se informa como {@code CONFLICTO_NUMERACION}.</p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JpaNumeradorChequeraAdapter implements NumeradorChequeraPort {

    private final ChequeraSerieControlRepository controlRepository;
    private final ChequeraSerieService chequeraSerieService;

    @Override
    @Transactional
    public Long siguienteNumero(Integer facultadId, Integer tipoChequeraId) {
        Long base = controlRepository
                .findFirstByFacultadIdAndTipoChequeraIdOrderByChequeraSerieIdDesc(facultadId, tipoChequeraId)
                .map(ChequeraSerieControl::getChequeraSerieId)
                .orElse(0L);
        Long candidato = base + 1;
        int intentos = 0;
        while (existeNumero(facultadId, tipoChequeraId, candidato) && intentos++ < 10000) {
            candidato++;
        }
        try {
            controlRepository.saveAndFlush(new ChequeraSerieControl(null, facultadId, tipoChequeraId, candidato,
                    (byte) 0, null, null, null, (byte) 0));
            return candidato;
        } catch (DataIntegrityViolationException e) {
            log.error("Conflicto al reservar el número {}.{}.{}", facultadId, tipoChequeraId, candidato, e);
            throw new EmisionException(ReglasEmision.CONFLICTO_NUMERACION,
                    "No se pudo reservar el número de chequera " + candidato + "; reintente la emisión");
        }
    }

    private boolean existeNumero(Integer facultadId, Integer tipoChequeraId, Long numero) {
        boolean enSerie = chequeraSerieService.findAllByNumber(facultadId, numero).stream()
                .anyMatch(serie -> Objects.equals(serie.getTipoChequeraId(), tipoChequeraId));
        boolean enControl = controlRepository
                .findByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId, numero)
                .isPresent();
        return enSerie || enControl;
    }
}
