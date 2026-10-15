package um.tesoreria.core.hexagonal.compras.articulo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.compras.articulo.application.exception.ArticuloException;
import um.tesoreria.core.hexagonal.compras.articulo.domain.model.Articulo;
import um.tesoreria.core.hexagonal.compras.articulo.domain.ports.in.UpdateArticuloUseCase;
import um.tesoreria.core.hexagonal.compras.articulo.domain.ports.out.ArticuloRepository;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.in.GetCuentaByNumeroCuentaUseCase;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.domain.ports.in.RegistrarEscrituraHistorialUseCase;

@Component
@RequiredArgsConstructor
public class UpdateArticuloUseCaseImpl implements UpdateArticuloUseCase {
    private final ArticuloRepository repository;
    // Excepción cross-slice autorizada: valida la cuenta con el puerto público de contable.cuenta
    // (patrón de CreateUsuarioChequeraFacultadUseCaseImpl; Articulo ya ancla Cuenta en su dominio).
    private final GetCuentaByNumeroCuentaUseCase getCuentaByNumeroCuentaUseCase;
    // Excepción cross-slice autorizada: historial #404 en la misma transacción (RegistrarEscrituraHistorialUseCase es MANDATORY)
    private final RegistrarEscrituraHistorialUseCase registrarEscrituraHistorialUseCase;
    @Override
    @Transactional
    public Articulo updateArticulo(Long id, Articulo cambios) {
        ArticuloReglas.validarCambios(cambios);
        ArticuloReglas.normalizarNumeros(cambios);
        // Antes del bloqueo: si la cuenta no existe no hace falta tomar el artículo
        ArticuloReglas.validarCuentaExistente(cambios, getCuentaByNumeroCuentaUseCase);
        // Bloqueada antes de leer el estado actual: dos ediciones del core se serializan
        Articulo actual = repository.findByIdForUpdate(id).orElseThrow(() -> new ArticuloException(id));
        // El "antes" sale de la lectura con bloqueo: es lo último confirmado
        var antes = ArticuloEstado.de(actual);
        var guardado = repository.update(actual.conCambios(cambios));
        var despues = ArticuloEstado.de(guardado);
        // Sin cambios no hay UPDATE (Hibernate no escribe una entidad sin modificar) y no se registra
        if (!antes.equals(despues)) {
            registrarEscrituraHistorialUseCase.registrarEdicion(ArticuloEstado.ENTIDAD, despues.clave(), antes, despues);
        }
        return guardado;
    }
}
