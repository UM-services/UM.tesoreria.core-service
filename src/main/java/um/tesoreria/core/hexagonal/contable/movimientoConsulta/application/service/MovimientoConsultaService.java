package um.tesoreria.core.hexagonal.contable.movimientoConsulta.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model.AsientoDiario;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model.AsientosNoCuadrados;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model.LibroDiario;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.ports.in.ConsultarAsientoUseCase;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.ports.in.ConsultarLibroDiarioUseCase;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.ports.in.ConsultarNoCuadradosUseCase;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class MovimientoConsultaService {

    private final ConsultarLibroDiarioUseCase consultarLibroDiarioUseCase;
    private final ConsultarAsientoUseCase consultarAsientoUseCase;
    private final ConsultarNoCuadradosUseCase consultarNoCuadradosUseCase;

    public LibroDiario consultarLibroDiario(OffsetDateTime desde, OffsetDateTime hasta) {
        return consultarLibroDiarioUseCase.consultarLibroDiario(desde, hasta);
    }

    public AsientoDiario consultarAsiento(OffsetDateTime fecha, Integer orden) {
        return consultarAsientoUseCase.consultarAsiento(fecha, orden);
    }

    public AsientosNoCuadrados consultarNoCuadrados(OffsetDateTime desde, OffsetDateTime hasta) {
        return consultarNoCuadradosUseCase.consultarNoCuadrados(desde, hasta);
    }
}
