package um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.domain.model.LectivoTotalImputacion;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.infrastructure.web.dto.LectivoTotalImputacionRequest;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.infrastructure.web.dto.LectivoTotalImputacionResponse;

@Component
public class LectivoTotalImputacionDtoMapper {

    public LectivoTotalImputacion toDomain(LectivoTotalImputacionRequest request) {
        if (request == null) return null;
        return LectivoTotalImputacion.builder()
                .lectivoTotalImputacionId(request.getLectivoTotalImputacionId())
                .facultadId(request.getFacultadId())
                .lectivoId(request.getLectivoId())
                .tipoChequeraId(request.getTipoChequeraId())
                .productoId(request.getProductoId())
                .numeroCuenta(request.getNumeroCuenta())
                .build();
    }

    public LectivoTotalImputacionResponse toResponse(LectivoTotalImputacion domain) {
        if (domain == null) return null;
        return LectivoTotalImputacionResponse.builder()
                .lectivoTotalImputacionId(domain.getLectivoTotalImputacionId())
                .facultadId(domain.getFacultadId())
                .lectivoId(domain.getLectivoId())
                .tipoChequeraId(domain.getTipoChequeraId())
                .productoId(domain.getProductoId())
                .numeroCuenta(domain.getNumeroCuenta())
                .facultad(domain.getFacultad())
                .lectivo(domain.getLectivo())
                .tipoChequera(domain.getTipoChequera())
                .producto(domain.getProducto())
                .cuenta(domain.getCuenta())
                .build();
    }

}
