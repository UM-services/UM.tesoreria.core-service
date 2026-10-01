package um.tesoreria.core.hexagonal.gestion.escrituraHistorial.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.domain.model.EscrituraHistorial;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.domain.model.EscrituraOperacion;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.domain.ports.in.RegistrarEscrituraHistorialUseCase;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.domain.ports.out.EscrituraHistorialRepository;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.domain.ports.out.EscrituraValorSerializer;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * Registra el historial en la misma transacción del caller ({@code REQUIRED}).
 * No loguea valores anteriores/nuevos. No inventa actor verificado.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EscrituraHistorialService implements RegistrarEscrituraHistorialUseCase {

    private final EscrituraHistorialRepository escrituraHistorialRepository;
    private final EscrituraValorSerializer escrituraValorSerializer;

    @Override
    @Transactional
    public EscrituraHistorial registrarAlta(String entidad, String entidadClave, Object valorNuevo) {
        return persistir(EscrituraOperacion.ALTA, entidad, entidadClave, null, valorNuevo);
    }

    @Override
    @Transactional
    public EscrituraHistorial registrarEdicion(String entidad, String entidadClave, Object valorAnterior, Object valorNuevo) {
        return persistir(EscrituraOperacion.EDICION, entidad, entidadClave, valorAnterior, valorNuevo);
    }

    @Override
    @Transactional
    public EscrituraHistorial registrarBaja(String entidad, String entidadClave, Object valorAnterior) {
        return persistir(EscrituraOperacion.BAJA, entidad, entidadClave, valorAnterior, null);
    }

    private EscrituraHistorial persistir(
            EscrituraOperacion operacion,
            String entidad,
            String entidadClave,
            Object valorAnterior,
            Object valorNuevo) {
        Assert.hasText(entidad, "entidad es obligatoria");
        Assert.hasText(entidadClave, "entidadClave es obligatoria");

        var evento = EscrituraHistorial.builder()
                .fecha(OffsetDateTime.now(ZoneOffset.UTC))
                .operacion(operacion)
                .entidad(entidad.trim())
                .entidadClave(entidadClave.trim())
                .valorAnterior(escrituraValorSerializer.serialize(valorAnterior))
                .valorNuevo(escrituraValorSerializer.serialize(valorNuevo))
                .build();

        var guardado = escrituraHistorialRepository.save(evento);
        // Solo resumen sin valores sensibles
        log.debug("Historial de escritura registrado: {}", guardado.resumenSeguro());
        return guardado;
    }
}
