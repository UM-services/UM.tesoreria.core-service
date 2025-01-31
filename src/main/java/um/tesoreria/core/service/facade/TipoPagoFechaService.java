package um.tesoreria.core.service.facade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import um.tesoreria.core.model.dto.TipoPagoFechaDto;
import um.tesoreria.core.model.view.TipoPagoFechaAcreditacion;
import um.tesoreria.core.model.view.TipoPagoFechaPago;
import um.tesoreria.core.service.view.TipoPagoFechaAcreditacionService;
import um.tesoreria.core.service.view.TipoPagoFechaPagoService;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class TipoPagoFechaService {

    private static final Integer TIPO_PAGO_MERCADO_PAGO = 18;

    private final TipoPagoFechaAcreditacionService tipoPagoFechaAcreditacionService;
    private final TipoPagoFechaPagoService tipoPagoFechaPagoService;

    public TipoPagoFechaService(TipoPagoFechaAcreditacionService tipoPagoFechaAcreditacionService,
                                TipoPagoFechaPagoService tipoPagoFechaPagoService) {
        this.tipoPagoFechaAcreditacionService = tipoPagoFechaAcreditacionService;
        this.tipoPagoFechaPagoService = tipoPagoFechaPagoService;
    }

    public List<TipoPagoFechaDto> findAllByFecha(OffsetDateTime fecha) {
        // Obtener datos con filtros aplicados directamente en los servicios
        List<TipoPagoFechaAcreditacion> acreditaciones = tipoPagoFechaAcreditacionService.findAllByFechaAcreditacion(fecha)
                .stream()
                .filter(acreditacion -> !Objects.equals(acreditacion.getTipoPagoId(), TIPO_PAGO_MERCADO_PAGO))
                .toList();
                
        List<TipoPagoFechaPago> pagos = tipoPagoFechaPagoService.findAllByFechaPago(fecha)
                .stream()
                .filter(pago -> Objects.equals(pago.getTipoPagoId(), TIPO_PAGO_MERCADO_PAGO))
                .toList();

        // Crear lista con capacidad inicial optimizada
        List<TipoPagoFechaDto> tipoPagos = new ArrayList<>(acreditaciones.size() + pagos.size());

        // Mapear acreditaciones
        tipoPagos.addAll(acreditaciones.stream()
                .map(acreditacion -> TipoPagoFechaDto.builder()
                        .uniqueId(acreditacion.getUniqueId())
                        .tipoPagoId(acreditacion.getTipoPagoId())
                        .fecha(acreditacion.getFechaAcreditacion())
                        .build())
                .toList());

        // Mapear pagos
        tipoPagos.addAll(pagos.stream()
                .map(pago -> TipoPagoFechaDto.builder()
                        .uniqueId(pago.getUniqueId())
                        .tipoPagoId(pago.getTipoPagoId())
                        .fecha(pago.getFechaPago())
                        .build())
                .toList());

        logTipos(tipoPagos);

        return tipoPagos;
    }

    private void logTipos(List<TipoPagoFechaDto> tipoPagos) {
        try {
            log.debug("Tipos -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(tipoPagos));
        } catch (JsonProcessingException e) {
            log.debug("Tipos jsonify error -> {}", e.getMessage());
        }
    }

}
