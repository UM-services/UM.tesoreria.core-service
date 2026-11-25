package um.tesoreria.core.hexagonal.contable.movimientoConsulta.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model.LibroDiario;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.ports.in.ConsultarLibroDiarioUseCase;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class MovimientoConsultaService {

    private final ConsultarLibroDiarioUseCase consultarLibroDiarioUseCase;

    public LibroDiario consultarLibroDiario(OffsetDateTime desde, OffsetDateTime hasta) {
        return consultarLibroDiarioUseCase.consultarLibroDiario(desde, hasta);
    }
}
