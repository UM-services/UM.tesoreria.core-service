package um.tesoreria.core.hexagonal.contable.bancaria.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.contable.bancaria.application.exception.BancariaConflictoException;
import um.tesoreria.core.hexagonal.contable.bancaria.application.exception.BancariaConflictoException.Codigo;
import um.tesoreria.core.hexagonal.contable.bancaria.application.exception.BancariaNoEncontradaException;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.Bancaria;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.BancariaUso;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.in.DeleteBancariaUseCase;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.out.BancariaHistorialPort;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.out.BancariaRepository;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.out.BancariaUsoPort;

/** El historial es MANDATORY: el caso de uso abre la transacción. */
@Component
@RequiredArgsConstructor
public class DeleteBancariaUseCaseImpl implements DeleteBancariaUseCase {
    private final BancariaRepository repository;
    private final BancariaUsoPort uso;
    private final BancariaHistorialPort historial;

    @Override
    @Transactional
    public void deleteBancaria(Long bancariaId) {
        // Primera lectura de la fila en la transacción: con bloqueo, el historial guarda el último estado confirmado.
        Bancaria anterior = repository.findWithLockByBancariaId(bancariaId)
                .orElseThrow(() -> new BancariaNoEncontradaException(bancariaId));
        if (uso.enUso(bancariaId)) {
            BancariaUso conteos = uso.contar(bancariaId);
            throw new BancariaConflictoException(Codigo.EN_USO, "La cuenta bancaria " + bancariaId + " tiene "
                    + conteos.valores() + " valores y " + conteos.libroBanco() + " movimientos de libro banco");
        }
        // El delete con flush traduce a EN_USO una referencia que apareció después del chequeo.
        repository.deleteByBancariaId(bancariaId);
        historial.registrarBaja(anterior);
    }
}
