package um.tesoreria.core.hexagonal.mercadoPagoContext.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.mercadoPagoContext.application.service.VencimientoMercadoPagoResolver;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.MercadoPagoContext;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.out.MercadoPagoContextRepository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindRenovacionesUseCaseImplTest {

    private static final OffsetDateTime HOY = OffsetDateTime.of(2026, 10, 20, 0, 0, 0, 0, ZoneOffset.UTC);
    private static final OffsetDateTime V10 = OffsetDateTime.of(2026, 10, 10, 0, 0, 0, 0, ZoneOffset.UTC);
    private static final OffsetDateTime V20 = OffsetDateTime.of(2026, 10, 20, 0, 0, 0, 0, ZoneOffset.UTC);
    private static final OffsetDateTime V30 = OffsetDateTime.of(2026, 10, 30, 0, 0, 0, 0, ZoneOffset.UTC);

    @Mock
    private MercadoPagoContextRepository repository;

    private final VencimientoMercadoPagoResolver resolver = new VencimientoMercadoPagoResolver();

    @Test
    void devuelveElVencimientoSiguienteComoDestino() {
        var useCase = new FindRenovacionesUseCaseImpl(repository, resolver);
        when(repository.findRenovaciones(any(), any(), eq(false), eq(null), eq(null), eq(200)))
                .thenReturn(List.of(contexto(cuota())));

        var resultado = useCase.findRenovaciones(HOY, 3, false, null, null, 200);

        assertThat(resultado).hasSize(1);
        var renovacion = resultado.getFirst();
        assertThat(renovacion.fechaVencimientoDestino()).isEqualTo(V20);
        assertThat(renovacion.importeDestino()).isEqualByComparingTo(new BigDecimal("49500"));
        assertThat(renovacion.requiereActualizacion()).isTrue();
    }

    @Test
    void usaLaVentanaDeLookbackSobreElVencimiento() {
        var useCase = new FindRenovacionesUseCaseImpl(repository, resolver);
        when(repository.findRenovaciones(any(), any(), anyBoolean(), any(), any(), any(Integer.class))).thenReturn(List.of());

        useCase.findRenovaciones(HOY, 3, false, null, 10L, 50);

        verify(repository).findRenovaciones(HOY.minusDays(3), HOY, false, null, 10L, 50);
    }

    @Test
    void descartaContextosSinCuota() {
        var useCase = new FindRenovacionesUseCaseImpl(repository, resolver);
        var contexto = contexto(cuota());
        contexto.setChequeraCuota(null);
        when(repository.findRenovaciones(any(), any(), anyBoolean(), any(), any(), any(Integer.class))).thenReturn(List.of(contexto));

        assertThat(useCase.findRenovaciones(HOY, 3, false, null, null, 200)).isEmpty();
    }

    @Test
    void noRequiereActualizacionSiElContextoYaEstaEnElVencimientoDestino() {
        var useCase = new FindRenovacionesUseCaseImpl(repository, resolver);
        var contexto = contexto(cuota());
        contexto.setFechaVencimiento(V20);
        contexto.setImporte(new BigDecimal("49500"));
        when(repository.findRenovaciones(any(), any(), anyBoolean(), any(), any(), any(Integer.class))).thenReturn(List.of(contexto));

        assertThat(useCase.findRenovaciones(HOY, 3, false, null, null, 200).getFirst().requiereActualizacion()).isFalse();
    }

    private MercadoPagoContext contexto(ChequeraCuota cuota) {
        return MercadoPagoContext.builder()
                .mercadoPagoContextId(1L)
                .chequeraCuotaId(cuota.getChequeraCuotaId())
                .fechaVencimiento(V10)
                .importe(new BigDecimal("45000"))
                .changed((byte) 0)
                .activo((byte) 1)
                .chequeraCuota(cuota)
                .build();
    }

    private ChequeraCuota cuota() {
        return ChequeraCuota.builder()
                .chequeraCuotaId(7L)
                .vencimiento1(V10)
                .importe1(new BigDecimal("45000"))
                .vencimiento2(V20)
                .importe2(new BigDecimal("49500"))
                .vencimiento3(V30)
                .importe3(new BigDecimal("54000"))
                .pagado((byte) 0)
                .baja((byte) 0)
                .compensada((byte) 0)
                .build();
    }

}
