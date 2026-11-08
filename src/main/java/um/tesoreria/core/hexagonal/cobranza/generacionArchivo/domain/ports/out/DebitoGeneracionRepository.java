package um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.ports.out;

import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.DebitoPendiente;

import java.time.OffsetDateTime;
import java.util.List;

public interface DebitoGeneracionRepository {

    List<DebitoPendiente> findPendientes(OffsetDateTime desde, OffsetDateTime hasta,
                                         boolean soloSantander, boolean soloOtrosBancos,
                                         Integer debitoTipoId);

    List<DebitoPendiente> findNoEnviadosAltas(OffsetDateTime desde, OffsetDateTime hasta,
                                              Integer debitoTipoId);

    List<DebitoPendiente> findByFechaEnvio(OffsetDateTime fechaEnvio, boolean soloSantander,
                                           boolean soloOtrosBancos, Integer debitoTipoId);

    List<DebitoPendiente> findAllAsociados(Integer facultadId, Integer tipoChequeraId,
                                           Long chequeraSerieId, Integer productoId,
                                           Integer alternativaId, Integer cuotaId);

    boolean existsEnviadoByCuota(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                                 Integer productoId, Integer alternativaId, Integer cuotaId,
                                 Integer debitoTipoId);

    void marcarFechaEnvio(Long debitoId, OffsetDateTime fechaEnvio);

    void actualizarCbu(Long debitoId, String cbu);

    void actualizarFechaVencimiento(Long debitoId, OffsetDateTime fechaVencimiento);

}
