package um.tesoreria.core.service.facade;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.web.reactive.function.client.WebClient;
import org.openpdf.text.pdf.PdfReader;
import org.openpdf.text.pdf.parser.PdfTextExtractor;

import um.tesoreria.core.hexagonal.chequera.arancelTipo.application.service.ArancelTipoService;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.infrastructure.persistence.entity.ArancelTipoEntity;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.application.service.ChequeraTotalService;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.domain.model.ChequeraTotal;
import um.tesoreria.core.hexagonal.chequera.producto.domain.model.Producto;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.application.service.TipoChequeraService;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera;
import um.tesoreria.core.hexagonal.dependencias.facultad.application.service.FacultadService;
import um.tesoreria.core.hexagonal.dependencias.facultad.domain.model.Facultad;
import um.tesoreria.core.hexagonal.lectivo.application.service.LectivoService;
import um.tesoreria.core.hexagonal.lectivo.domain.model.Lectivo;
import um.tesoreria.core.hexagonal.personas.legajo.application.service.LegajoService;
import um.tesoreria.core.hexagonal.personas.persona.application.service.PersonaService;
import um.tesoreria.core.hexagonal.personas.persona.domain.model.Persona;
import um.tesoreria.core.kotlin.model.ChequeraAlternativa;
import um.tesoreria.core.model.Debito;
import um.tesoreria.core.model.TipoImpresion;
import um.tesoreria.core.model.dto.ChequeraCuotaPagosDto;
import um.tesoreria.core.model.dto.ChequeraPagoDto;
import um.tesoreria.core.service.CarreraService;
import um.tesoreria.core.service.ChequeraAlternativaService;
import um.tesoreria.core.service.DebitoService;
import um.tesoreria.core.service.LectivoAlternativaService;
import um.tesoreria.core.service.TipoImpresionService;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import javax.imageio.ImageIO;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * El método de producción carga el logo con una ruta relativa
 * (Image.getInstance("marca_um.png")), que en el repo real vive en la raíz del módulo, no en
 * el classpath. Para que este test no dependa de cuál sea el working directory con el que lo
 * corra el IDE o Maven, {@link #asegurarLogoDisponible()} crea un PNG mínimo válido en esa
 * ruta relativa si todavía no existe uno ahí, y {@link #limpiarLogoDummy()} lo borra al
 * terminar — sin tocar un marca_um.png real si ya estaba presente.
 */
@ExtendWith(MockitoExtension.class)
class FormulariosToPdfServiceGenerateEstadoChequeraPdfTest {

    private static final File LOGO = new File("marca_um.png");
    private static boolean logoCreadoPorElTest;

    @BeforeAll
    static void asegurarLogoDisponible() throws IOException {
        if (!LOGO.exists()) {
            BufferedImage dummy = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
            ImageIO.write(dummy, "png", LOGO);
            logoCreadoPorElTest = true;
        }
    }

    @AfterAll
    static void limpiarLogoDummy() {
        if (logoCreadoPorElTest) {
            LOGO.delete();
        }
    }

    @Mock private Environment environment;
    @Mock private ChequeraSerieService chequeraSerieService;
    @Mock private FacultadService facultadService;
    @Mock private TipoChequeraService tipoChequeraService;
    @Mock private PersonaService personaService;
    @Mock private LectivoService lectivoService;
    @Mock private LegajoService legajoService;
    @Mock private CarreraService carreraService;
    @Mock private ChequeraCuotaService chequeraCuotaService;
    @Mock private LectivoAlternativaService lectivoAlternativaService;
    @Mock private SincronizeService sincronizeService;
    @Mock private WebClient.Builder webClientBuilder;
    @Mock private ChequeraService chequeraService;
    @Mock private DebitoService debitoService;
    @Mock private ChequeraTotalService chequeraTotalService;
    @Mock private ChequeraAlternativaService chequeraAlternativaService;
    @Mock private ArancelTipoService arancelTipoService;
    @Mock private TipoImpresionService tipoImpresionService;

    @InjectMocks
    private FormulariosToPdfService service;

    private static final Integer FACULTAD_ID = 1;
    private static final Integer TIPO_CHEQUERA_ID = 2;
    private static final Long CHEQUERA_SERIE_ID = 14308L;
    private static final Integer ALTERNATIVA_ID = 1;
    private static final Integer DEBITO_TIPO_ID = 2;
    private static final Integer PRODUCTO_MATRICULA_ID = 1;
    private static final Integer PRODUCTO_ARANCEL_ID = 2;

    private void mockearDatosBasicosDeLaChequera() {
        ChequeraSerie serie = ChequeraSerie.builder()
                .facultadId(FACULTAD_ID)
                .tipoChequeraId(TIPO_CHEQUERA_ID)
                .chequeraSerieId(CHEQUERA_SERIE_ID)
                .personaId(BigDecimal.valueOf(46546133))
                .documentoId(1)
                .lectivoId(37)
                .arancelTipoId(5)
                .tipoImpresionId(1)
                .build();
        when(chequeraSerieService.findByUnique(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID)).thenReturn(serie);

        when(facultadService.findByFacultadId(FACULTAD_ID))
                .thenReturn(Facultad.builder().facultadId(FACULTAD_ID).nombre("Facultad de Ingeniería").build());
        when(tipoChequeraService.findByTipoChequeraId(TIPO_CHEQUERA_ID))
                .thenReturn(TipoChequera.builder().tipoChequeraId(TIPO_CHEQUERA_ID).nombre("Matrícula y Arancel").build());
        when(personaService.findByUnique(BigDecimal.valueOf(46546133), 1))
                .thenReturn(Persona.builder().personaId(BigDecimal.valueOf(46546133)).apellido("YAÑEZ").nombre("Maria Guadalupe").build());
        when(lectivoService.findByLectivoId(37))
                .thenReturn(Lectivo.builder().lectivoId(37).nombre("Lectivo 2026 - 2027").build());
        when(arancelTipoService.findByArancelTipoId(5))
                .thenReturn(ArancelTipoEntity.builder().arancelTipoId(5).descripcion("Ciclo Completo").build());
        when(tipoImpresionService.findByTipoImpresionId(1)).thenReturn(tipoImpresionRapipago());

        ChequeraCuotaPagosDto cuotaMatricula1 = cuota(PRODUCTO_MATRICULA_ID, 1, 6, 2026, "182000.00", "Matrícula",
                pago(182000, "D2026062301_30518594466"));
        ChequeraCuotaPagosDto cuotaMatricula2 = cuota(PRODUCTO_MATRICULA_ID, 2, 11, 2026, "193000.00", "Matrícula", null);
        ChequeraCuotaPagosDto cuotaArancel1 = cuota(PRODUCTO_ARANCEL_ID, 1, 3, 2026, "331000.00", "Arancel",
                pago(331000, "MercadoPago"));
        when(chequeraService.findAllCuotaPagosByChequera(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID, ALTERNATIVA_ID))
                .thenReturn(List.of(cuotaMatricula1, cuotaMatricula2, cuotaArancel1));

        when(chequeraAlternativaService.findByUnique(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID, PRODUCTO_MATRICULA_ID, ALTERNATIVA_ID))
                .thenReturn(chequeraAlternativa("Matrícula", 2));
        when(chequeraAlternativaService.findByUnique(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID, PRODUCTO_ARANCEL_ID, ALTERNATIVA_ID))
                .thenReturn(chequeraAlternativa("Arancel Mensual", 1));
    }

    private static Debito debitoDeEjemplo() {
        Debito debito = new Debito();
        debito.setCuotaId(1);
        debito.setProductoId(PRODUCTO_ARANCEL_ID);
        debito.setAlternativaId(ALTERNATIVA_ID);
        debito.setFechaVencimiento(OffsetDateTime.now());
        debito.setCbu("0110628830062808193783");
        return debito;
    }

    @Test
    void generateEstadoChequeraPdf_writesAPdfWithMatriculaBeforeArancel(@TempDir Path tempDir) throws Exception {
        mockearDatosBasicosDeLaChequera();
        when(environment.getProperty("path.files")).thenReturn(tempDir.toString() + File.separator);
        when(debitoService.findAllByChequera(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID, DEBITO_TIPO_ID))
                .thenReturn(List.of(debitoDeEjemplo()));
        when(chequeraTotalService.findAllByChequera(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID))
                .thenReturn(List.of(
                        ChequeraTotal.builder().productoId(PRODUCTO_MATRICULA_ID)
                                .total(new BigDecimal("375000.00")).pagado(new BigDecimal("182000.00")).build(),
                        ChequeraTotal.builder().productoId(PRODUCTO_ARANCEL_ID)
                                .total(new BigDecimal("331000.00")).pagado(new BigDecimal("331000.00")).build()));

        String filename = service.generateEstadoChequeraPdf(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID,
                ALTERNATIVA_ID, DEBITO_TIPO_ID);

        assertThat(filename).isNotNull().endsWith(".pdf");
        File pdf = new File(filename);
        assertThat(pdf).exists();
        assertThat(pdf.length()).isGreaterThan(0);

        try (PdfReader reader = new PdfReader(filename)) {
            assertThat(reader.getNumberOfPages()).isEqualTo(2);
            String primeraHoja = new PdfTextExtractor(reader).getTextFromPage(1);
            assertThat(primeraHoja).contains("Primer vencimiento", "19/06/2026", "19/11/2026", "19/03/2026");
            assertThat(primeraHoja.indexOf("Matrícula: 1/2")).isLessThan(primeraHoja.indexOf("Arancel Mensual: 1/1"));
        }
    }

    @Test
    void generateEstadoChequeraPdf_returnsNullInsteadOfThrowingWhenGenerationFails(@TempDir Path tempDir) {
        mockearDatosBasicosDeLaChequera();
        when(environment.getProperty("path.files")).thenReturn(tempDir.toString() + File.separator);
        when(chequeraTotalService.findAllByChequera(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID))
                .thenReturn(List.of(
                        ChequeraTotal.builder().productoId(PRODUCTO_MATRICULA_ID)
                                .total(new BigDecimal("375000.00")).pagado(new BigDecimal("182000.00")).build(),
                        ChequeraTotal.builder().productoId(PRODUCTO_ARANCEL_ID)
                                .total(new BigDecimal("331000.00")).pagado(new BigDecimal("331000.00")).build()));
        // debitoService se llama DENTRO del bloque try (al armar la hoja 2), así que forzar una
        // falla acá sirve para verificar que el catch(Exception) del método la atrapa y devuelve
        // null en vez de propagarla — a diferencia de chequeraTotalService, que se llama ANTES del
        // try y por lo tanto una falla ahí se propagaría sin capturar.
        when(debitoService.findAllByChequera(any(), any(), any(), any())).thenThrow(new RuntimeException("boom"));

        String filename = service.generateEstadoChequeraPdf(FACULTAD_ID, TIPO_CHEQUERA_ID, CHEQUERA_SERIE_ID,
                ALTERNATIVA_ID, DEBITO_TIPO_ID);

        assertThat(filename).isNull();
    }

    private static TipoImpresion tipoImpresionRapipago() {
        TipoImpresion tipoImpresion = new TipoImpresion();
        tipoImpresion.setTipoImpresionId(1);
        tipoImpresion.setNombre("Rapipago");
        return tipoImpresion;
    }

    private static ChequeraAlternativa chequeraAlternativa(String titulo, int cuotas) {
        ChequeraAlternativa alternativa = new ChequeraAlternativa();
        alternativa.setTitulo(titulo);
        alternativa.setCuotas(cuotas);
        return alternativa;
    }

    private static ChequeraCuotaPagosDto cuota(Integer productoId, Integer cuotaId, Integer mes, Integer anho,
                                               String importe, String nombreProducto, ChequeraPagoDto pago) {
        return ChequeraCuotaPagosDto.builder()
                .productoId(productoId)
                .alternativaId(ALTERNATIVA_ID)
                .cuotaId(cuotaId)
                .mes(mes)
                .anho(anho)
                .vencimiento1(OffsetDateTime.of(anho, mes, 19, 0, 0, 0, 0, ZoneOffset.UTC))
                .importe1(new BigDecimal(importe))
                .producto(Producto.builder().productoId(productoId).nombre(nombreProducto).build())
                .chequeraPagos(pago == null ? List.of() : List.of(pago))
                .build();
    }

    private static ChequeraPagoDto pago(int importe, String archivo) {
        return ChequeraPagoDto.builder()
                .orden(0)
                .fecha(OffsetDateTime.now())
                .importe(BigDecimal.valueOf(importe))
                .archivo(archivo)
                .build();
    }

}
