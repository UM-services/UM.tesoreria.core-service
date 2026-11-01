package um.tesoreria.core.hexagonal.contable.bancaria.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.contable.bancaria.application.exception.BancariaSolicitudInvalidaException;
import um.tesoreria.core.hexagonal.contable.bancaria.application.exception.BancariaSolicitudInvalidaException.Codigo;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.Bancaria;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.BancariaCambios;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.in.CreateBancariaUseCase;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.out.BancariaHistorialPort;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.out.BancariaReferenciasPort;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.out.BancariaRepository;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

/** El historial es MANDATORY: el caso de uso abre la transacción. */
@Component
@RequiredArgsConstructor
public class CreateBancariaUseCaseImpl implements CreateBancariaUseCase {
    private final BancariaRepository repository;
    private final BancariaReferenciasPort referencias;
    private final BancariaHistorialPort historial;
    private final Clock clock;

    @Override
    @Transactional
    public Bancaria createBancaria(BancariaCambios pedido) {
        // VB6 manda bancariaId 0 o vacío en un alta; un id real es un cliente que cree estar editando.
        Long id = pedido.getBancariaId();
        if (id != null && id > 0) {
            throw new BancariaSolicitudInvalidaException(Codigo.ID_NO_PERMITIDO,
                    "el id lo asigna el servicio; para editar usar PUT /bancaria/{id}");
        }
        Bancaria nueva = Bancaria.builder()
                .banco(BancariaValidaciones.texto("banco", pedido.getBanco(), ""))
                .numero(BancariaValidaciones.texto("numero", pedido.getNumero(), ""))
                .titular(BancariaValidaciones.texto("titular", pedido.getTitular(), ""))
                .cbu(BancariaValidaciones.cbu(pedido.getCbu(), "", ""))
                .cuit(BancariaValidaciones.cuit(pedido.getCuit(), "", ""))
                .numeroCuenta(BancariaValidaciones.cuenta(pedido.getNumeroCuenta(), null))
                .proveedorId(pedido.getProveedorId().valorOSi(null))
                .created(LocalDateTime.now(clock))
                .uuid(UUID.randomUUID().toString().replace("-", ""))
                .build();
        BancariaValidaciones.validarReferencias(referencias, nueva.getNumeroCuenta(), null, nueva.getProveedorId(), null);
        Bancaria creada = repository.create(nueva);
        historial.registrarAlta(creada);
        return creada;
    }
}
