package um.tesoreria.core.service.view;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import um.tesoreria.core.model.view.TipoPagoFechaPago;
import um.tesoreria.core.repository.view.TipoPagoFechaPagoRepository;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@Slf4j
public class TipoPagoFechaPagoService {

    private final TipoPagoFechaPagoRepository repository;

    public TipoPagoFechaPagoService(TipoPagoFechaPagoRepository repository) {
        this.repository = repository;
    }

    public List<TipoPagoFechaPago> findAllByFechaPago(OffsetDateTime fechaPago) {
        log.debug("Processing findAllByFechaPago");
        var tipos = repository.findAllByFechaPago(fechaPago);
        logTipoPagoFechas(tipos);
        return tipos;
    }

    private void logTipoPagoFechas(List<TipoPagoFechaPago> tipos) {
        try {
            log.debug("Tipos Fecha Pago -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(tipos));
        } catch (JsonProcessingException e) {
            log.debug("Tipos Fecha Pago jsonify error -> {}", e.getMessage());
        }
    }

}
