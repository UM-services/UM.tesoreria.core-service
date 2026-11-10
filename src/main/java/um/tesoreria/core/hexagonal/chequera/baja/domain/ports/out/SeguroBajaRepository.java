package um.tesoreria.core.hexagonal.chequera.baja.domain.ports.out;

import java.math.BigDecimal;

public interface SeguroBajaRepository {

    void resetBaja(Integer documentoId, BigDecimal personaId, Integer lectivoId);

}
