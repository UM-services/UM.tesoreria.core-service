package um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.usecases;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.in.FindAllInconsistenciasUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.out.ChequeraCuotaRepository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FindAllInconsistenciasUseCaseImpl implements FindAllInconsistenciasUseCase {

    private final ChequeraCuotaRepository repository;

    public FindAllInconsistenciasUseCaseImpl(ChequeraCuotaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ChequeraCuota> findAllInconsistencias(OffsetDateTime desde, OffsetDateTime hasta, Boolean reduced) {
        final BigDecimal MULTIPLICADOR = new BigDecimal(49);

        return repository.findAllByVencimiento1Between(desde, hasta).stream()
                .filter(cuota -> {
                    boolean vencimientosInvalidos = cuota.getVencimiento1() == null
                            || cuota.getVencimiento2() == null || cuota.getVencimiento3() == null
                            || cuota.getVencimiento1().isAfter(cuota.getVencimiento2())
                            || cuota.getVencimiento2().isAfter(cuota.getVencimiento3());

                    boolean importesInvalidos = cuota.getImporte1() == null || cuota.getImporte2() == null
                            || cuota.getImporte3() == null
                            || cuota.getImporte1().compareTo(cuota.getImporte2()) > 0
                            || cuota.getImporte2().compareTo(cuota.getImporte3()) > 0;

                    boolean multiplicadoresInvalidos = multiplicadorInvalido(cuota.getImporte1Original(), cuota.getImporte1(), MULTIPLICADOR)
                            || multiplicadorInvalido(cuota.getImporte2Original(), cuota.getImporte2(), MULTIPLICADOR)
                            || multiplicadorInvalido(cuota.getImporte3Original(), cuota.getImporte3(), MULTIPLICADOR);

                    return vencimientosInvalidos || importesInvalidos || multiplicadoresInvalidos;
                })
                .collect(Collectors.toList());
    }

    private boolean multiplicadorInvalido(BigDecimal original, BigDecimal importe, BigDecimal multiplicador) {
        return original == null || importe == null || original.multiply(multiplicador).compareTo(importe) < 0;
    }
}
