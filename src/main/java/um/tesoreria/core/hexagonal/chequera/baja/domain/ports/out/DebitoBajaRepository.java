package um.tesoreria.core.hexagonal.chequera.baja.domain.ports.out;

import um.tesoreria.core.hexagonal.chequera.baja.domain.model.DebitoBaja;

import java.util.List;

public interface DebitoBajaRepository {

    List<DebitoBaja> findAllAsociados(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                                      Integer productoId, Integer alternativaId, Integer cuotaId);

    void limpiarFechaBaja(Long debitoId);

}
