package um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.bancaria.application.exception.BancariaSolicitudInvalidaException;
import um.tesoreria.core.hexagonal.contable.bancaria.application.exception.BancariaSolicitudInvalidaException.Codigo;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.Bancaria;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.BancariaCambios;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.BancariaUso;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.Campo;
import um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.web.dto.BancariaRequest;
import um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.web.dto.BancariaResponse;
import um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.web.dto.BancariaUsoResponse;

import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.function.Function;

@Component
public class BancariaDtoMapper {

    /** numeroCuenta es decimal(11,0). */
    private static final int DIGITOS_CUENTA = 11;
    /** CuB_Prv_ID es smallint. */
    private static final int PROVEEDOR_MAXIMO = Short.MAX_VALUE;

    public BancariaCambios toDomain(BancariaRequest request) {
        if (request == null) return null;
        return BancariaCambios.builder()
                .bancariaId(request.presente("bancariaId") ? idDeCuenta(request.getBancariaId()) : null)
                .updated(fecha(request.getUpdated()))
                .banco(campo(request, "banco", request.getBanco()))
                .numero(campo(request, "numero", request.getNumero()))
                .cbu(campo(request, "cbu", request.getCbu()))
                .titular(campo(request, "titular", request.getTitular()))
                .cuit(campo(request, "cuit", request.getCuit()))
                .numeroCuenta(campo(request, "numeroCuenta", request.getNumeroCuenta(), BancariaDtoMapper::numeroCuenta))
                .proveedorId(campo(request, "proveedorId", request.getProveedorId(), BancariaDtoMapper::proveedorId))
                .build();
    }

    private static <T> Campo<T> campo(BancariaRequest request, String nombre, T valor) {
        return request.presente(nombre) ? Campo.de(valor) : Campo.ausente();
    }

    private static <T, R> Campo<R> campo(BancariaRequest request, String nombre, T valor, Function<T, R> convertir) {
        return request.presente(nombre) ? Campo.de(valor == null ? null : convertir.apply(valor)) : Campo.ausente();
    }

    /** Un id de cuenta bancaria: entero exacto. El 0 y los negativos pasan (VB6 los manda como «sin id»). */
    private static Long idDeCuenta(BigDecimal valor) {
        if (valor == null) {
            return null;
        }
        try {
            return valor.longValueExact();
        } catch (ArithmeticException ex) {
            throw numeroInvalido("bancariaId tiene que ser un entero");
        }
    }

    private static BigDecimal numeroCuenta(BigDecimal valor) {
        // Se acota antes de convertir: toBigIntegerExact de 1e99999999 (o de 1e-99999999) materializa un entero gigante.
        // stripTrailingZeros es proporcional a los dígitos, no al exponente: 1.0 pasa a 1 y 1e-9 queda con escala positiva.
        BigDecimal sinCeros = valor.stripTrailingZeros();
        // long: con escala -2147483647 la resta en int desborda y la magnitud pasaría el chequeo.
        if (valor.signum() <= 0 || sinCeros.scale() > 0 || (long) sinCeros.precision() - sinCeros.scale() > DIGITOS_CUENTA) {
            throw numeroInvalido("numeroCuenta tiene que ser un entero positivo de hasta " + DIGITOS_CUENTA + " dígitos");
        }
        return new BigDecimal(sinCeros.toBigIntegerExact());
    }

    /** Un entero exacto dentro del rango de la columna; un decimal o un desborde es una solicitud inválida, nunca se trunca. */
    private static Integer proveedorId(BigDecimal valor) {
        try {
            int exacto = valor.intValueExact();
            if (exacto < 1 || exacto > PROVEEDOR_MAXIMO) {
                throw new ArithmeticException();
            }
            return exacto;
        } catch (ArithmeticException ex) {
            throw numeroInvalido("proveedorId tiene que ser un entero entre 1 y " + PROVEEDOR_MAXIMO);
        }
    }

    /**
     * updated tal como lo devuelve el servicio: hora local de la base, sin zona (el MySQL de dev corre en -03 y el valor
     * no es UTC). Un valor con zona no se puede comparar sin conocer la zona de la base, así que se rechaza.
     */
    private static LocalDateTime fecha(String texto) {
        if (texto == null) {
            return null;
        }
        try {
            return LocalDateTime.parse(texto);
        } catch (DateTimeException ex) {
            throw new BancariaSolicitudInvalidaException(Codigo.SOLICITUD_INVALIDA,
                    "updated tiene que ser la fecha y hora sin zona que devuelve el servicio, por ejemplo 2026-10-01T09:30:15");
        }
    }

    private static BancariaSolicitudInvalidaException numeroInvalido(String mensaje) {
        return new BancariaSolicitudInvalidaException(Codigo.NUMERO_INVALIDO, mensaje);
    }

    public BancariaResponse toResponse(Bancaria domain) {
        if (domain == null) return null;
        BancariaResponse response = new BancariaResponse();
        response.setBancariaId(domain.getBancariaId());
        response.setBanco(domain.getBanco());
        response.setNumero(domain.getNumero());
        response.setCbu(domain.getCbu());
        response.setTitular(domain.getTitular());
        response.setCuit(domain.getCuit());
        response.setNumeroCuenta(domain.getNumeroCuenta());
        response.setProveedorId(domain.getProveedorId());
        response.setCreated(domain.getCreated());
        response.setUpdated(domain.getUpdated());
        return response;
    }

    public BancariaUsoResponse toResponse(BancariaUso uso) {
        return new BancariaUsoResponse(uso.valores(), uso.libroBanco(), uso.eliminable());
    }
}
