package um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model;

public record ResultadoOperacionDebito(
        String operacion,
        int registros,
        int omitidos) {
}
