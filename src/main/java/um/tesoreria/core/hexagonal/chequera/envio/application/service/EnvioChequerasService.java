package um.tesoreria.core.hexagonal.chequera.envio.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.ChequeraEnvioAlta;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioJob;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioJobTarea;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioTareaEstado;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.ResultadoEnvio;
import um.tesoreria.core.hexagonal.chequera.envio.domain.ports.out.ChequeraEnvioAltaReader;
import um.tesoreria.core.hexagonal.chequera.envio.domain.ports.out.EnvioChequeraGateway;

/**
 * Ejecuta una tarea del job. Paridad con el {@code For/Next} de {@code frmCheqMail}: cada fila es
 * independiente y un error de negocio no corta el resto.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EnvioChequerasService {

    private final ChequeraEnvioAltaReader reader;
    private final EnvioChequeraGateway gateway;

    public ResultadoEnvio ejecutar(EnvioJobTarea tarea, EnvioJob job) {
        return switch (job.getOperacion()) {
            case CAPTURAR_CORREOS -> capturar(tarea);
            case EXTENDER_DEBITOS -> extender(tarea);
            case MAILING -> enviar(tarea, job);
        };
    }

    private ResultadoEnvio capturar(EnvioJobTarea tarea) {
        try {
            Integer actualizados = gateway.capturarCorreos(tarea.getPersonaId(), tarea.getDocumentoId());
            return ResultadoEnvio.de(EnvioTareaEstado.CORREO_CAPTURADO, "CORREO_CAPTURADO",
                    "Correos capturados (" + (actualizados == null ? 0 : actualizados) + ")");
        } catch (RuntimeException e) {
            log.warn("Envío masivo: no se pudieron capturar correos de {}.{}: {}",
                    tarea.getPersonaId(), tarea.getDocumentoId(), e.getMessage());
            return ResultadoEnvio.error("ERROR_CAPTURA", e.getMessage());
        }
    }

    private ResultadoEnvio extender(EnvioJobTarea tarea) {
        try {
            gateway.extenderDebito(tarea.getFacultadId(), tarea.getTipoChequeraId(), tarea.getChequeraSerieId());
            return ResultadoEnvio.de(EnvioTareaEstado.DEBITO_EXTENDIDO, "DEBITO_EXTENDIDO", "Débitos extendidos");
        } catch (RuntimeException e) {
            log.warn("Envío masivo: no se pudieron extender débitos de {}: {}",
                    tarea.getChequeraId(), e.getMessage());
            return ResultadoEnvio.error("ERROR_DEBITO", e.getMessage());
        }
    }

    /**
     * Mailing de una serie. Ya no hay retención por deuda: la chequera se envía siempre que tenga
     * correo válido y no haya sido enviada. La deuda de la persona no interviene en la decisión.
     */
    private ResultadoEnvio enviar(EnvioJobTarea tarea, EnvioJob job) {
        ChequeraEnvioAlta alta = reader.findByChequeraId(tarea.getChequeraId()).orElse(null);
        if (alta == null) {
            // Paridad con el legacy, que releía la serie: si ya no está en el padrón, no se envía.
            return ResultadoEnvio.de(EnvioTareaEstado.OMITIDA, "NO_ENCONTRADA",
                    "La serie ya no está en el padrón de altas");
        }
        if (!alta.hayCorreoValido()) {
            return ResultadoEnvio.de(EnvioTareaEstado.SIN_CORREOS, "SIN_CORREOS",
                    "Sin correo válido en el domicilio: no se envía (verificar con «Capturar correos»)");
        }
        if (alta.enviado()) {
            // El ledger es la autoridad final; el flag de la vista se usa como corte temprano.
            return ResultadoEnvio.de(EnvioTareaEstado.OMITIDA, "YA_ENVIADA",
                    "La chequera ya fue enviada");
        }

        String respuesta = gateway.enviarChequera(alta.facultadId(), alta.tipoChequeraId(),
                alta.chequeraSerieId(), alta.alternativaId(), job.isCodigoBarras(), job.getJobId());
        return ResultadoEnvio.builder()
                .estado(EnvioTareaEstado.ENCOLADA)
                .codigo("ENCOLADA")
                .mensaje(respuesta)
                .detalle(detalle(alta, respuesta))
                .retenida(false)
                .enviado(false)
                .build();
    }

    /** Línea de log del legacy: {@code chequeraId - fac/tch/serie - persona.doc - apellido - respuesta}. */
    private String detalle(ChequeraEnvioAlta alta, String respuesta) {
        return alta.chequeraId() + " - " + alta.numero()
                + " - " + alta.personaId() + "." + alta.documentoId()
                + " - " + alta.apellidoNombre()
                + " - " + respuesta;
    }
}
