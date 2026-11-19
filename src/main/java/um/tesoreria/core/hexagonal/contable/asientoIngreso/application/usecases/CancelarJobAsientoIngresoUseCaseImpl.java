package um.tesoreria.core.hexagonal.contable.asientoIngreso.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionOperations;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.application.exception.AsientoIngresoException;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.application.exception.AsientoIngresoException.Codigo;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJob;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJobEstado;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.ports.in.CancelarJobAsientoIngresoUseCase;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.ports.out.AsientoIngresoJobRepository;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CancelarJobAsientoIngresoUseCaseImpl implements CancelarJobAsientoIngresoUseCase {

    private final AsientoIngresoJobRepository repository;
    private final TransactionOperations transaction;

    @Override
    public AsientoIngresoJob cancelar(Long jobId) {
        return Objects.requireNonNull(transaction.execute(status -> {
            AsientoIngresoJob job = repository.findWithLockByJobId(jobId)
                    .orElseThrow(() -> new AsientoIngresoException(Codigo.JOB_NO_ENCONTRADO,
                            "No existe el job " + jobId));
            if (job.finalizado()) {
                throw new AsientoIngresoException(Codigo.JOB_YA_FINALIZADO,
                        "El job " + jobId + " ya está finalizado (" + job.getEstado() + ")");
            }
            job.setEstado(AsientoIngresoJobEstado.CANCELADO);
            job.setFinalizado(OffsetDateTime.now(ZoneOffset.UTC));
            return repository.update(job);
        }));
    }

}
