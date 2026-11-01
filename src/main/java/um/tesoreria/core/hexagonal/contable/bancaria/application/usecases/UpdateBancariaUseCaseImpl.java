package um.tesoreria.core.hexagonal.contable.bancaria.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.contable.bancaria.application.exception.BancariaConflictoException;
import um.tesoreria.core.hexagonal.contable.bancaria.application.exception.BancariaConflictoException.Codigo;
import um.tesoreria.core.hexagonal.contable.bancaria.application.exception.BancariaNoEncontradaException;
import um.tesoreria.core.hexagonal.contable.bancaria.application.exception.BancariaSolicitudInvalidaException;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.Bancaria;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.BancariaCambios;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.in.UpdateBancariaUseCase;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.out.BancariaHistorialPort;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.out.BancariaReferenciasPort;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.out.BancariaRepository;

import java.math.BigDecimal;

/**
 * Edición por presencia: lo ausente conserva, un null explícito vacía (texto '' o proveedor NULL; la cuenta contable
 * no se puede vaciar). El id de la URL manda. El historial es MANDATORY: el caso de uso abre la transacción.
 */
@Component
@RequiredArgsConstructor
public class UpdateBancariaUseCaseImpl implements UpdateBancariaUseCase {
    private final BancariaRepository repository;
    private final BancariaReferenciasPort referencias;
    private final BancariaHistorialPort historial;

    @Override
    @Transactional
    public Bancaria updateBancaria(Long bancariaId, BancariaCambios cambios) {
        Long idCuerpo = cambios.getBancariaId();
        if (idCuerpo != null && idCuerpo > 0 && !idCuerpo.equals(bancariaId)) {
            throw new BancariaSolicitudInvalidaException(BancariaSolicitudInvalidaException.Codigo.ID_NO_COINCIDE,
                    "bancariaId " + idCuerpo + " del cuerpo no coincide con el " + bancariaId + " de la URL");
        }
        // Primera lectura de la fila en la transacción: con bloqueo, así dos PUT (o un PUT y un DELETE) se serializan.
        Bancaria anterior = repository.findWithLockByBancariaId(bancariaId)
                .orElseThrow(() -> new BancariaNoEncontradaException(bancariaId));
        if (cambios.getUpdated() != null && !BancariaValidaciones.mismoInstante(cambios.getUpdated(), anterior.getUpdated())) {
            throw new BancariaConflictoException(Codigo.CAMBIO_CONCURRENTE,
                    "La cuenta bancaria fue modificada después de que se leyó (updated " + anterior.getUpdated() + "); recargar");
        }
        BigDecimal cuenta = BancariaValidaciones.cuenta(cambios.getNumeroCuenta(), anterior.getNumeroCuenta());
        Integer proveedorId = cambios.getProveedorId().valorOSi(anterior.getProveedorId());
        Bancaria nueva = anterior.toBuilder()
                .banco(BancariaValidaciones.texto("banco", cambios.getBanco(), anterior.getBanco()))
                .numero(BancariaValidaciones.texto("numero", cambios.getNumero(), anterior.getNumero()))
                .titular(BancariaValidaciones.texto("titular", cambios.getTitular(), anterior.getTitular()))
                .cbu(BancariaValidaciones.cbu(cambios.getCbu(), anterior.getCbu(), anterior.getCbu()))
                .cuit(BancariaValidaciones.cuit(cambios.getCuit(), anterior.getCuit(), anterior.getCuit()))
                .numeroCuenta(cuenta)
                .proveedorId(proveedorId)
                .build();
        BancariaValidaciones.validarReferencias(referencias, cuenta, anterior.getNumeroCuenta(),
                proveedorId, anterior.getProveedorId());
        if (nueva.mismosDatos(anterior)) {
            return anterior;
        }
        Bancaria guardada = repository.update(nueva);
        historial.registrarEdicion(anterior, guardada);
        return guardada;
    }
}
