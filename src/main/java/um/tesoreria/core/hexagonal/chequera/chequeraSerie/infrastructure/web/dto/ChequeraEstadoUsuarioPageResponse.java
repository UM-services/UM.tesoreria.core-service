package um.tesoreria.core.hexagonal.chequera.chequeraSerie.infrastructure.web.dto;

import org.springframework.data.domain.Page;

import java.util.List;

/** Contrato JSON estable para la consulta paginada de chequeras por usuario. */
public record ChequeraEstadoUsuarioPageResponse(
        List<ChequeraEstadoUsuarioResponse> content,
        long totalElements,
        int number,
        int size,
        int totalPages) {

    public static ChequeraEstadoUsuarioPageResponse from(Page<ChequeraEstadoUsuarioResponse> page) {
        return new ChequeraEstadoUsuarioPageResponse(
                page.getContent(),
                page.getTotalElements(),
                page.getNumber(),
                page.getSize(),
                page.getTotalPages());
    }
}
