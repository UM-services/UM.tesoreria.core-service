package um.tesoreria.core.hexagonal.contable.asientoCierre.domain.model;

public enum VarianteCierre {
    RESULTADO("Asiento Cierre Resultado"),
    ACTIVO_PASIVO("Asiento Cierre Activo-Pasivo");

    private final String vinculo;

    VarianteCierre(String vinculo) {
        this.vinculo = vinculo;
    }

    public String vinculo() {
        return vinculo;
    }
}
