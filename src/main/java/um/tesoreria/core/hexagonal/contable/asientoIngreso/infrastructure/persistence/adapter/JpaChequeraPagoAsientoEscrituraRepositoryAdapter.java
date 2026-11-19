package um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.PagoIngreso;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.ports.out.ChequeraPagoAsientoEscrituraRepository;
import um.tesoreria.core.kotlin.model.ChequeraPagoAsiento;
import um.tesoreria.core.repository.ChequeraPagoAsientoRepository;

import java.math.BigDecimal;

/**
 * Escritura de {@code chequera_pago_asiento}. La tabla la siguen leyendo {@code /pago/items} y los
 * reportes del legacy, por eso el asiento se sigue construyendo a través de ella.
 */
@Component
@RequiredArgsConstructor
public class JpaChequeraPagoAsientoEscrituraRepositoryAdapter implements ChequeraPagoAsientoEscrituraRepository {

    private final ChequeraPagoAsientoRepository repository;

    @Override
    public void deleteAllByPago(PagoIngreso pago) {
        repository.deleteAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndProductoIdAndAlternativaIdAndCuotaIdAndOrden(
                pago.getFacultadId(), pago.getTipoChequeraId(), pago.getChequeraSerieId(),
                pago.getProductoId(), pago.getAlternativaId(), pago.getCuotaId(), pago.getOrden());
        // La baja derivada queda encolada; si no se ejecuta ahora, el INSERT del mismo pago choca
        // contra la fila vieja (Hibernate ordena INSERT antes que DELETE en el mismo flush).
        repository.flush();
    }

    @Override
    public void create(PagoIngreso pago, Integer item, BigDecimal cuenta, Byte debita, BigDecimal importe) {
        ChequeraPagoAsiento entity = new ChequeraPagoAsiento(
                null,
                pago.getChequeraPagoId(),
                pago.getChequeraPagoReemplazoId(),
                pago.getTipoPagoId(),
                pago.getFecha(),
                pago.getFacultadId(),
                pago.getTipoChequeraId(),
                pago.getChequeraSerieId(),
                pago.getProductoId(),
                pago.getAlternativaId(),
                pago.getCuotaId(),
                pago.getOrden(),
                item,
                cuenta,
                debita,
                importe,
                null,
                null);
        repository.save(entity);
    }

    @Override
    public void flush() {
        repository.flush();
    }

}
