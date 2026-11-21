package um.tesoreria.core.hexagonal.contable.asiento.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.asiento.domain.model.AsientoContableLinea;
import um.tesoreria.core.hexagonal.contable.asiento.domain.model.AsientoContableResultado;
import um.tesoreria.core.hexagonal.contable.asiento.infrastructure.web.dto.AsientoContableLineaRequest;
import um.tesoreria.core.hexagonal.contable.asiento.infrastructure.web.dto.AsientoContableLineaResponse;
import um.tesoreria.core.hexagonal.contable.asiento.infrastructure.web.dto.AsientoContableResponse;

import java.util.List;

@Component
public class AsientoContableDtoMapper {

    public AsientoContableLinea toLinea(AsientoContableLineaRequest request) {
        return new AsientoContableLinea(request.numeroCuenta(), null, request.debita(), request.importe());
    }

    public List<AsientoContableLinea> toLineas(List<AsientoContableLineaRequest> requests) {
        return requests == null ? List.of() : requests.stream().map(this::toLinea).toList();
    }

    public AsientoContableLineaResponse toLineaResponse(AsientoContableLinea linea) {
        return new AsientoContableLineaResponse(linea.numeroCuenta(), linea.nombreCuenta(),
                linea.debita(), linea.importe());
    }

    public AsientoContableResponse toResponse(AsientoContableResultado resultado) {
        return new AsientoContableResponse(resultado.fecha(), resultado.orden(), resultado.vinculo(),
                resultado.totalDebe(), resultado.totalHaber(), resultado.cuadra(),
                resultado.lineas().stream().map(this::toLineaResponse).toList());
    }

}
