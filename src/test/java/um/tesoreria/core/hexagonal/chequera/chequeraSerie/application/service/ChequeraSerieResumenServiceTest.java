package um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import um.tesoreria.core.hexagonal.chequera.baja.application.service.BajaService;
import um.tesoreria.core.hexagonal.chequera.baja.domain.model.DatosBaja;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerieResumen;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.in.GetChequeraSerieByNumberUseCase;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.domain.model.ArancelTipo;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera;
import um.tesoreria.core.hexagonal.dependencias.facultad.domain.model.Facultad;
import um.tesoreria.core.hexagonal.dependencias.geografica.domain.model.Geografica;
import um.tesoreria.core.hexagonal.lectivo.domain.model.Lectivo;
import um.tesoreria.core.hexagonal.personas.persona.domain.model.Persona;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ChequeraSerieResumenServiceTest {

    @Mock
    private GetChequeraSerieByNumberUseCase getChequeraSerieByNumberUseCase;
    @Mock
    private BajaService bajaService;
    @Mock
    private ChequeraCuotaService chequeraCuotaService;

    private ChequeraSerieResumenService service;

    @BeforeEach
    void setUp() {
        service = new ChequeraSerieResumenService(getChequeraSerieByNumberUseCase, bajaService, chequeraCuotaService);
        when(getChequeraSerieByNumberUseCase.getAllByNumber(6, 123L)).thenReturn(List.of(serie()));
        when(chequeraCuotaService.findAllPendientes(6, 2, 123L, 1)).thenReturn(List.of(
                cuota(1, "50.00"), cuota(2, "30.00")));
        when(bajaService.findDatosByUnique(6, 2, 123L)).thenReturn(Optional.of(new DatosBaja(
                6, 2, 123L, 1, new BigDecimal("45719365"), 12345678,
                OffsetDateTime.parse("2026-03-15T00:00:00Z"), "Baja")));
    }

    @Test
    void enriqueceLaSerieConBajaYDeuda() {
        List<ChequeraSerieResumen> resumen = service.resumen(6, 123L);

        assertThat(resumen).hasSize(1);
        ChequeraSerieResumen fila = resumen.get(0);
        assertThat(fila.personaNombre()).isEqualTo("Pérez, Ana");
        assertThat(fila.facultadNombre()).isEqualTo("Ingeniería");
        assertThat(fila.tipoChequeraNombre()).isEqualTo("Grado");
        assertThat(fila.baja()).isTrue();
        assertThat(fila.cuotasDeuda()).isEqualTo(2);
        assertThat(fila.importeDeuda()).isEqualByComparingTo(new BigDecimal("80.00"));
    }

    @Test
    void marcaSinBajaCuandoNoExisteLaFila() {
        when(bajaService.findDatosByUnique(any(), any(), any())).thenReturn(Optional.empty());

        ChequeraSerieResumen fila = service.resumen(6, 123L).get(0);

        assertThat(fila.baja()).isFalse();
        assertThat(fila.bajaFecha()).isNull();
    }

    private ChequeraSerie serie() {
        return ChequeraSerie.builder()
                .chequeraId(1L)
                .facultadId(6)
                .tipoChequeraId(2)
                .chequeraSerieId(123L)
                .personaId(new BigDecimal("45719365"))
                .documentoId(12345678)
                .lectivoId(1)
                .alternativaId(1)
                .facultad(Facultad.builder().nombre("Ingeniería").build())
                .tipoChequera(TipoChequera.builder().nombre("Grado").build())
                .arancelTipo(ArancelTipo.builder().descripcion("Arancel").build())
                .geografica(new Geografica(1, "Centro", (byte) 0))
                .lectivo(Lectivo.builder().nombre("2026").build())
                .persona(Persona.builder().apellido("Pérez").nombre("Ana").build())
                .build();
    }

    private ChequeraCuota cuota(Integer cuotaId, String importe) {
        return ChequeraCuota.builder().cuotaId(cuotaId).importe1(new BigDecimal(importe)).build();
    }

}
