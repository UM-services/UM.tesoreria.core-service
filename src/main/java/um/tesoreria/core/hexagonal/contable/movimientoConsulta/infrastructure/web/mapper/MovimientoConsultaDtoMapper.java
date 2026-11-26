package um.tesoreria.core.hexagonal.contable.movimientoConsulta.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model.AsientoDiario;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model.AsientoNoCuadrado;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model.AsientosNoCuadrados;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model.LibroDiario;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model.LineaDiario;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.infrastructure.web.dto.AsientoDiarioResponse;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.infrastructure.web.dto.AsientoNoCuadradoResponse;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.infrastructure.web.dto.AsientosNoCuadradosResponse;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.infrastructure.web.dto.LibroDiarioResponse;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.infrastructure.web.dto.LineaDiarioResponse;

@Component
public class MovimientoConsultaDtoMapper {

    public LibroDiarioResponse toResponse(LibroDiario libro) {
        return new LibroDiarioResponse(libro.desde(), libro.hasta(),
                libro.asientos().stream().map(this::toResponse).toList(),
                libro.totalDebe(), libro.totalHaber());
    }

    public AsientoDiarioResponse toResponse(AsientoDiario asiento) {
        return new AsientoDiarioResponse(asiento.fechaContable(), asiento.ordenContable(),
                asiento.lineas().stream().map(this::toResponse).toList(),
                asiento.totalDebe(), asiento.totalHaber());
    }

    public AsientosNoCuadradosResponse toResponse(AsientosNoCuadrados noCuadrados) {
        return new AsientosNoCuadradosResponse(noCuadrados.desde(), noCuadrados.hasta(),
                noCuadrados.asientos().stream().map(this::toResponse).toList());
    }

    public AsientoNoCuadradoResponse toResponse(AsientoNoCuadrado asiento) {
        return new AsientoNoCuadradoResponse(asiento.fechaContable(), asiento.ordenContable(),
                asiento.lineas().stream().map(this::toResponse).toList(),
                asiento.totalDebe(), asiento.totalHaber(), asiento.diferencia());
    }

    public LineaDiarioResponse toResponse(LineaDiario linea) {
        return new LineaDiarioResponse(linea.fechaContable(), linea.ordenContable(), linea.item(),
                linea.numeroCuenta(), linea.nombreCuenta(), linea.comprobante(), linea.proveedor(),
                linea.concepto(), linea.debe(), linea.haber());
    }
}
