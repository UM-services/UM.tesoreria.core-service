package um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.LineaChequeraPagoAsiento;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.PagoIngreso;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.ports.out.ChequeraPagoAsientoEscrituraRepository;
import um.tesoreria.core.kotlin.model.ChequeraPagoAsiento;
import um.tesoreria.core.repository.ChequeraPagoAsientoRepository;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * Escritura de {@code chequera_pago_asiento}. La tabla la siguen leyendo {@code /pago/items} y los
 * reportes del legacy, por eso el asiento se sigue construyendo a través de ella.
 *
 * <p>Todo pasa por JPA: la baja por día es un derived delete (los {@code PostDelete} alimentan el
 * resumen de auditoría) y el alta es un {@code saveAll}. El flush después de la baja es necesario
 * porque Hibernate ejecuta los INSERT antes que los DELETE en un mismo flush y las líneas nuevas
 * comparten la clave única {@code (fac, tch, chs, pro, alt, cuo, orden, item)}.</p>
 */
@Component
@RequiredArgsConstructor
public class JpaChequeraPagoAsientoEscrituraRepositoryAdapter implements ChequeraPagoAsientoEscrituraRepository {

    private final ChequeraPagoAsientoRepository repository;

    @Override
    public void deleteAllByFechaAndTipoPago(OffsetDateTime fecha, Integer tipoPagoId) {
        LocalDate dia = fecha.withOffsetSameInstant(ZoneOffset.UTC).toLocalDate();
        OffsetDateTime desde = dia.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime hasta = dia.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);
        repository.deleteAllByTipoPagoIdAndFechaGreaterThanEqualAndFechaLessThan(tipoPagoId, desde, hasta);
        repository.flush();
    }

    @Override
    public void createAll(List<LineaChequeraPagoAsiento> lineas) {
        if (lineas.isEmpty()) {
            return;
        }
        repository.saveAll(lineas.stream().map(JpaChequeraPagoAsientoEscrituraRepositoryAdapter::entity).toList());
    }

    @Override
    public void flush() {
        repository.flush();
    }

    private static ChequeraPagoAsiento entity(LineaChequeraPagoAsiento linea) {
        PagoIngreso pago = linea.pago();
        return new ChequeraPagoAsiento(
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
                linea.item(),
                linea.cuenta(),
                linea.debita(),
                linea.importe(),
                null,
                null);
    }

}
