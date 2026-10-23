package um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.web.dto;

public record BancariaUsoResponse(long valores, long libroBanco, boolean eliminable) {
}
