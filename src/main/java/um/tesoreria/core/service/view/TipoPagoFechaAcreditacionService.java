package um.tesoreria.core.service.view;

import java.time.OffsetDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import um.tesoreria.core.model.view.TipoPagoFechaAcreditacion;
import um.tesoreria.core.repository.view.TipoPagoFechaAcreditacionRepository;

@Service
@Slf4j
public class TipoPagoFechaAcreditacionService {

	private final TipoPagoFechaAcreditacionRepository repository;

	public TipoPagoFechaAcreditacionService(TipoPagoFechaAcreditacionRepository repository) {
		this.repository = repository;
	}

	public List<TipoPagoFechaAcreditacion> findAllByFechaAcreditacion(OffsetDateTime fechaAcreditacion) {
		log.debug("Processing findAllByFechaAcreditacion");
		var tipos = repository.findAllByFechaAcreditacion(fechaAcreditacion);
		logTipoPagoFechas(tipos);
		return tipos;
	}

	private void logTipoPagoFechas(List<TipoPagoFechaAcreditacion> tipos) {
		try {
			log.debug("Tipos Fecha Acreditacion -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(tipos));
		} catch (Exception e) {
			log.debug("Tipos Fecha Acreditacion jsonify error -> {}", e.getMessage());
		}
	}

}
