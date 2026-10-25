package um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.web.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import um.tesoreria.core.hexagonal.contable.bancaria.application.exception.BancariaSolicitudInvalidaException;
import um.tesoreria.core.hexagonal.contable.bancaria.application.exception.BancariaSolicitudInvalidaException.Codigo;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.BancariaCambios;
import um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.web.dto.BancariaRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BancariaDtoMapperTest {
    BancariaDtoMapper mapper = new BancariaDtoMapper();

    BancariaCambios conCuenta(String numero) {
        BancariaRequest request = new BancariaRequest();
        request.setNumeroCuenta(new BigDecimal(numero));
        return mapper.toDomain(request);
    }

    /** Antes tardaba ~25 s (toBigIntegerExact de 10^99999999) y con exponentes mayores agotaba la memoria. */
    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    void unExponenteEnormeSeRechazaEnSeguidaSinMaterializarElEntero() {
        for (String numero : new String[]{"1e99999999", "1e999999999", "1E+11", "-1e99999999", "1e-99999999", "1e2147483647", "9e2147483647", "1e-2147483647"}) {
            assertThatThrownBy(() -> conCuenta(numero)).as(numero)
                    .isInstanceOfSatisfying(BancariaSolicitudInvalidaException.class,
                            e -> assertThat(e.getCodigo()).isEqualTo(Codigo.NUMERO_INVALIDO));
        }
    }

    @Test
    void losLimitesValidosDeNumeroCuentaPasanConEscalaCero() {
        assertThat(conCuenta("99999999999").getNumeroCuenta().valor()).isEqualTo(new BigDecimal("99999999999"));
        assertThat(conCuenta("11101010001.0").getNumeroCuenta().valor()).isEqualTo(new BigDecimal("11101010001"));
        assertThat(conCuenta("1").getNumeroCuenta().valor().scale()).isZero();
    }

    @Test
    void ceroNegativosDecimalesYMasDeOnceDigitosSonNumeroInvalido() {
        for (String numero : new String[]{"0", "-5", "1.5", "123456789012", "0.0"}) {
            assertThatThrownBy(() -> conCuenta(numero)).as(numero)
                    .isInstanceOfSatisfying(BancariaSolicitudInvalidaException.class,
                            e -> assertThat(e.getCodigo()).isEqualTo(Codigo.NUMERO_INVALIDO));
        }
    }

    @Test
    void updatedSinZonaSeAceptaYConZonaSeRechazaPorqueLaBaseNoEsUtc() {
        BancariaRequest request = new BancariaRequest();
        request.setUpdated("2026-10-01T12:30:15");
        assertThat(mapper.toDomain(request).getUpdated()).isEqualTo(LocalDateTime.parse("2026-10-01T12:30:15"));
        for (String conZona : new String[]{"2026-10-01T09:30:15-03:00", "2026-10-01T12:30:15Z", "ayer"}) {
            request.setUpdated(conZona);
            assertThatThrownBy(() -> mapper.toDomain(request)).as(conZona)
                    .isInstanceOfSatisfying(BancariaSolicitudInvalidaException.class,
                            e -> assertThat(e.getCodigo()).isEqualTo(Codigo.SOLICITUD_INVALIDA));
        }
    }
}
