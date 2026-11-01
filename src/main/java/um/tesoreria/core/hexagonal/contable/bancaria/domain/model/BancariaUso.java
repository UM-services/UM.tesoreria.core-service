package um.tesoreria.core.hexagonal.contable.bancaria.domain.model;

/** Referencias a una cuenta bancaria: valores emitidos desde ella y movimientos de su libro banco. */
public record BancariaUso(long valores, long libroBanco) {

    public boolean eliminable() {
        return valores == 0 && libroBanco == 0;
    }
}
