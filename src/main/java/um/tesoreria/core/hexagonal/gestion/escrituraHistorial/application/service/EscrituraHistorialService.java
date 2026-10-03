package um.tesoreria.core.hexagonal.gestion.escrituraHistorial.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.domain.model.EscrituraHistorial;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.domain.model.EscrituraOperacion;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.domain.ports.in.RegistrarEscrituraHistorialUseCase;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.domain.ports.out.EscrituraHistorialRepository;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.domain.ports.out.EscrituraValorSerializer;

/**
 * Registra el historial en la misma transacción del caller ({@code MANDATORY}).
 * No loguea valores anteriores/nuevos. No inventa actor verificado.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EscrituraHistorialService implements RegistrarEscrituraHistorialUseCase {

    // Largos de las columnas en docs/sql/V404__gestion_escritura_historial.sql
    static final int ENTIDAD_MAX = 128;
    static final int ENTIDAD_CLAVE_MAX = 255;

    private final EscrituraHistorialRepository escrituraHistorialRepository;
    private final EscrituraValorSerializer escrituraValorSerializer;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public EscrituraHistorial registrarAlta(String entidad, String entidadClave, Object valorNuevo) {
        Assert.notNull(valorNuevo, "el alta requiere el estado creado");
        return persistir(EscrituraOperacion.ALTA, entidad, entidadClave, null, valorNuevo);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public EscrituraHistorial registrarEdicion(String entidad, String entidadClave, Object valorAnterior, Object valorNuevo) {
        Assert.notNull(valorAnterior, "la edición requiere el estado anterior");
        Assert.notNull(valorNuevo, "la edición requiere el estado nuevo");
        return persistir(EscrituraOperacion.EDICION, entidad, entidadClave, valorAnterior, valorNuevo);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public EscrituraHistorial registrarBaja(String entidad, String entidadClave, Object valorAnterior) {
        Assert.notNull(valorAnterior, "la baja requiere el estado previo");
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
        var entidadNormalizada = entidad.trim();
        var claveNormalizada = entidadClave.trim();
        // Sin depender del sql_mode del servidor: un valor largo no se trunca ni falla recién en el flush
        Assert.isTrue(entidadNormalizada.length() <= ENTIDAD_MAX, "entidad supera " + ENTIDAD_MAX + " caracteres");
        Assert.isTrue(claveNormalizada.length() <= ENTIDAD_CLAVE_MAX, "entidadClave supera " + ENTIDAD_CLAVE_MAX + " caracteres");
        // String.valueOf(id) con id nulo: el evento no quedaría ligado a ningún registro
        Assert.isTrue(!"null".equalsIgnoreCase(claveNormalizada), "entidadClave no puede ser \"null\"");

        // fecha la asigna la base al insertar (ver EscrituraHistorialEntity)
        var evento = EscrituraHistorial.builder()
                .operacion(operacion)
                .entidad(entidadNormalizada)
                .entidadClave(claveNormalizada)
                .valorAnterior(escrituraValorSerializer.serialize(valorAnterior))
                .valorNuevo(escrituraValorSerializer.serialize(valorNuevo))
                .build();

        var guardado = escrituraHistorialRepository.save(evento);
        if (log.isDebugEnabled()) {
            // Solo resumen sin valores sensibles
            log.debug("Historial de escritura registrado: {}", guardado.resumenSeguro());
        }
        return guardado;
    }
}
