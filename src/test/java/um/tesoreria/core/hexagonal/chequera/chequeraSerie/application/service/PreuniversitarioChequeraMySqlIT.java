package um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.ContactoGuarani;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.DocumentoPrincipalGuarani;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.PersonaGuarani;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.PropuestaGuarani;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.PropuestaResponsableAcademicaGuarani;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.RequisitoPresentadoGuarani;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.TipoDocumentoGuarani;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.infrastructure.web.controller.AlumnoGuaraniController;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.infrastructure.web.dto.AlumnoGuaraniRequest;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.infrastructure.web.dto.PersonalesResponse;
import um.tesoreria.core.hexagonal.lectivo.application.service.LectivoService;
import um.tesoreria.core.listener.PaymentEventListener;
import um.tesoreria.core.service.facade.MailChequeraService;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Gate de integración del arreglo del PR #340. Comprueba el orden real de foreign keys de MySQL
 * y que los detalles emitidos sean coherentes con las cuotas activas.
 *
 * <p>Corre contra un tesium de prueba real porque esa foreign key sólo existe en el DDL de MySQL:
 * no hay relación JPA entre {@code ChequeraCuotaEntity} y {@code ChequeraAlternativa}, y el H2 de
 * la suite normal levanta con {@code REFERENTIAL_INTEGRITY FALSE}. Sobre ese H2 el bug es
 * invisible.
 *
 * <p>No lo levanta {@code mvn test}: surefire por defecto sólo toma {@code *Test}, {@code Test*} y
 * {@code *Tests}, así que un {@code *IT} sólo corre si se lo pide por nombre.
 *
 * <p><strong>Seguridad.</strong> Tres capas, en este orden:
 * <ol>
 *   <li>El método de test es {@code @Transactional} y Spring lo revierte siempre: nada se
 *       commitea. La foreign key se evalúa igual, porque MySQL las verifica por sentencia y no
 *       soporta constraints diferidas.</li>
 *   <li>{@link #limpiar()} borra igual, de hijo a padre, lo que cuelgue del DNI sintético. En
 *       condiciones normales borra cero filas.</li>
 *   <li>{@link #verificar(Connection)} comprueba que no quedó ninguna fila asociada al DNI ni
 *       huérfanos de la serie que el test iba a tomar. No compara conteos globales: en una base
 *       compartida otro proceso puede escribir entre ambas fotos y eso sería un rojo falso.</li>
 * </ol>
 */
@SpringBootTest
@ActiveProfiles("mysql-it")
// El application.yml de la suite normal trae ddl-auto=create-drop para H2. Si por lo que sea el
// perfil mysql-it no se aplicara, Hibernate borraría y recrearía el esquema del tesium de prueba.
// @TestPropertySource gana sobre cualquier yml, así que la garantía queda acá y no en el perfil.
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.sql.init.mode=never",
        "spring.datasource.url=jdbc:mysql://127.0.0.1:13306/tesium?useSSL=false&serverTimezone=UTC",
        "spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver",
        "spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect",
        // bootstrap.yml pide un pool de 100 y HikariCP iguala minimumIdle a maximumPoolSize, así
        // que sin esto el test abriría 100 conexiones contra un tesium de prueba compartido.
        "spring.datasource.hikari.maximum-pool-size=2",
        "spring.datasource.hikari.minimum-idle=1"
})
@Slf4j
class PreuniversitarioChequeraMySqlIT {

    /**
     * DNI inexistente, fuera del rango que emite el RENAPER, para no colisionar nunca con una
     * persona real. El test aborta antes de escribir nada si ya tuviera filas.
     */
    private static final BigDecimal PERSONA = new BigDecimal("99000017");
    private static final int DOCUMENTO_ID = 1;

    /** Identificadores ficticios: no corresponden a registros personales de Guaraní. */
    private static final long ALUMNO_GUARANI = 99000018L;
    private static final int PERSONA_GUARANI = 99000019;
    private static final long DOCUMENTO_GUARANI = 99000020L;
    private static final long CONTACTO_TELEFONO_GUARANI = 99000021L;
    private static final long CONTACTO_EMAIL_GUARANI = 99000022L;

    /** El lectivo que el servicio resuelve por fecha; se afirma, no se supone. */
    private static final int LECTIVO_ESPERADO = 37;

    private static final int FACULTAD_ID = 1;
    private static final int TIPO_CHEQUERA_ID = 1;

    /** Valores académicos necesarios para reproducir la configuración del caso reportado. */
    private static final int PROPUESTA = 125;
    private static final int RESPONSABLE_ACADEMICA = 2;
    private static final int UBICACION = 1;
    private static final List<Integer> REQUISITOS = List.of(1013, 1021, 1022);

    /** Tablas identificables por (persona, documento), en orden hijo -> padre para el borrado. */
    private static final Map<String, String[]> TABLAS_POR_DNI = new LinkedHashMap<>();

    /** Tablas identificables por serie, con su prefijo de columnas. */
    private static final Map<String, String> TABLAS_POR_SERIE = new LinkedHashMap<>();

    static {
        TABLAS_POR_DNI.put("chequera_serie", new String[]{"chs_per_id", "chs_doc_id"});
        TABLAS_POR_DNI.put("aluleg", new String[]{"ale_per_id", "ale_doc_id"});
        TABLAS_POR_DNI.put("domicilio", new String[]{"dom_per_id", "dom_doc_id"});
        TABLAS_POR_DNI.put("persona", new String[]{"per_id", "per_doc_id"});

        TABLAS_POR_SERIE.put("chequera_cuota", "chc");
        TABLAS_POR_SERIE.put("chequera_total", "cht");
        TABLAS_POR_SERIE.put("chequera_alternativa", "cha");
    }

    private static DataSource sharedDataSource;

    /**
     * Serie que el flujo va a tomar: {@code MAX(csc_chs_id) + 1}, calculada igual que
     * {@code nextChequeraSerieId}. Sirve para detectar huérfanos si la transacción se escapara.
     */
    private static long serieCandidata;

    /**
     * Sólo se limpia si el preflight pasó. Si el DNI ya tenía filas, esas filas son de otro y el
     * borrado las destruiría: pasó exactamente eso en la primera corrida contra el tesium de
     * prueba, que borró una fila preexistente de {@code persona}.
     */
    private static boolean preflightOk;

    @Autowired
    private AlumnoGuaraniController alumnoGuaraniController;

    @Autowired
    private LectivoService lectivoService;

    @Autowired
    private EntityManager entityManager;

    /** Evita publicar el evento send-chequera: la reproducción termina en la persistencia. */
    @MockitoBean
    private MailChequeraService mailChequeraService;

    /** El único @KafkaListener del contexto se sustituye para que el IT no arranque un consumer. */
    @MockitoBean
    private PaymentEventListener paymentEventListener;

    @Autowired
    void capturarDataSource(DataSource dataSource) {
        sharedDataSource = dataSource;
    }

    @Test
    @Transactional
    void create_persistsPreuniversitarioDetailsInForeignKeyOrder() throws Exception {
        try (Connection connection = sharedDataSource.getConnection()) {
            exigirDniLibre(connection);
            preflightOk = true;
            serieCandidata = proximaSerie(connection);
        }
        log.info("Serie que el flujo va a tomar -> {}", serieCandidata);

        // El servicio resuelve el lectivo con la fecha de hoy. Si no fuera 37, el caso que
        // reproducimos no es el del caso reportado y cualquier conclusión sería inválida.
        int lectivoId = lectivoService.findByFecha(OffsetDateTime.now()).getLectivoId();
        assertThat(lectivoId)
                .as("el lectivo resuelto por fecha tiene que ser el mismo que el del caso reportado")
                .isEqualTo(LECTIVO_ESPERADO);

        // Misma coreografía del cliente Guaraní. El segundo endpoint exige propuestaGuarani, que
        // no forma parte de la respuesta pública del primero; se conserva desde el payload para
        // armar explícitamente su contrato de entrada, sin ampliar el DTO de salida.
        var request = requestDesdePayload();
        var personales = alumnoGuaraniController.createPersonales(request);
        assertThat(personales.getStatusCode().is2xxSuccessful()).isTrue();
        var response = personales.getBody();
        assertThat(response).isNotNull();
        assertThat(response.getResult()).isTrue();
        assertThat(response.getPersona()).isNotNull();
        assertThat(response.getPersona().getPersonaId()).isEqualByComparingTo(PERSONA);
        assertThat(response.getPersona().getDocumentoId()).isEqualTo(DOCUMENTO_ID);
        assertThat(response.getDomicilio()).isNotNull();
        assertThat(response.getDomicilio().getPersonaId()).isEqualByComparingTo(PERSONA);
        assertThat(response.getDomicilio().getDocumentoId()).isEqualTo(DOCUMENTO_ID);
        var preuniversitarioRequest = PersonalesResponse.builder()
                .result(response.getResult())
                .persona(response.getPersona())
                .domicilio(response.getDomicilio())
                .alumnoGuarani(response.getAlumnoGuarani())
                .propuestaGuarani(request.getPropuestaRel())
                .build();

        var preuniversitario = alumnoGuaraniController.createPreuniversitario(preuniversitarioRequest);
        assertThat(preuniversitario.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(preuniversitario.getBody()).isNotNull();

        entityManager.flush();
        Connection connection = DataSourceUtils.getConnection(sharedDataSource);
        try {
            verificarDetallesPersistidos(connection, serieCandidata);
        } finally {
            DataSourceUtils.releaseConnection(connection, sharedDataSource);
        }
    }

    @AfterAll
    static void limpiar() throws Exception {
        if (sharedDataSource == null || !preflightOk) {
            log.info("Sin limpieza: el preflight no pasó, nada de lo que hay es nuestro.");
            return;
        }
        try (Connection connection = sharedDataSource.getConnection()) {
            connection.setAutoCommit(false);

            // Sólo se borran series que se puede probar que son nuestras: las que tienen una
            // chequera_serie a nombre del DNI sintético. Nunca por rango, para que sea imposible
            // tocar series preexistentes.
            List<Long> series = seriesDelDni(connection);
            log.info("Limpieza: series propias a borrar -> {}", series);
            for (Long serie : series) {
                for (Map.Entry<String, String> tabla : TABLAS_POR_SERIE.entrySet()) {
                    borrar(connection, "DELETE FROM " + tabla.getKey()
                                    + " WHERE " + tabla.getValue() + "_fac_id = ?"
                                    + " AND " + tabla.getValue() + "_tch_id = ?"
                                    + " AND " + tabla.getValue() + "_chs_id = ?",
                            FACULTAD_ID, TIPO_CHEQUERA_ID, serie);
                }
            }
            for (Map.Entry<String, String[]> tabla : TABLAS_POR_DNI.entrySet()) {
                borrar(connection, "DELETE FROM " + tabla.getKey()
                                + " WHERE " + tabla.getValue()[0] + " = ? AND " + tabla.getValue()[1] + " = ?",
                        PERSONA, DOCUMENTO_ID);
            }
            for (Long serie : series) {
                borrar(connection, "DELETE FROM chequera_serie_control"
                                + " WHERE csc_fac_id = ? AND csc_tch_id = ? AND csc_chs_id = ?",
                        FACULTAD_ID, TIPO_CHEQUERA_ID, serie);
            }
            connection.commit();

            verificar(connection);
        }
    }

    /**
     * No compara conteos globales: verifica que el DNI sintético no dejó ninguna fila, y que la
     * serie candidata no quedó con hijos huérfanos. Si otro proceso tomó esa serie legítimamente,
     * lo informa y no la revisa, en vez de dar un rojo falso.
     */
    private static void verificar(Connection connection) throws Exception {
        for (Map.Entry<String, String[]> tabla : TABLAS_POR_DNI.entrySet()) {
            long filas = contarPorDni(connection, tabla.getKey(), tabla.getValue());
            assertThat(filas)
                    .as("el test dejó filas en %s para el DNI %s", tabla.getKey(), PERSONA)
                    .isZero();
        }
        if (serieCandidata <= 0) {
            return;
        }
        BigDecimal duenho = duenhoDeLaSerie(connection, serieCandidata);
        if (duenho != null && duenho.compareTo(PERSONA) != 0) {
            log.warn("La serie {} quedó a nombre de {}: otro proceso la tomó. Se omite la "
                    + "verificación de huérfanos.", serieCandidata, duenho);
            return;
        }
        assertThat(duenho)
                .as("quedó una chequera_serie %s a nombre del DNI sintético", serieCandidata)
                .isNull();
        for (Map.Entry<String, String> tabla : TABLAS_POR_SERIE.entrySet()) {
            long filas = contarPorSerie(connection, tabla.getKey(), tabla.getValue(), serieCandidata);
            assertThat(filas)
                    .as("quedaron huérfanos en %s para la serie %s", tabla.getKey(), serieCandidata)
                    .isZero();
        }
    }

    /** Mismo cálculo que {@code PreuniversitarioChequeraService.nextChequeraSerieId}. */
    private static long proximaSerie(Connection connection) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT COALESCE(MAX(csc_chs_id), 0) + 1 FROM chequera_serie_control"
                        + " WHERE csc_fac_id = ? AND csc_tch_id = ?")) {
            statement.setInt(1, FACULTAD_ID);
            statement.setInt(2, TIPO_CHEQUERA_ID);
            try (ResultSet rs = statement.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }
        }
    }

    private static void verificarDetallesPersistidos(Connection connection, long serie) throws Exception {
        assertThat(contarPorSerie(connection, "chequera_total", "cht", serie))
                .as("la serie %s debe tener chequera_total", serie)
                .isPositive();
        assertThat(contarPorSerie(connection, "chequera_alternativa", "cha", serie))
                .as("la serie %s debe tener chequera_alternativa", serie)
                .isPositive();
        assertThat(contarPorSerie(connection, "chequera_cuota", "chc", serie))
                .as("la serie %s debe tener chequera_cuota", serie)
                .isPositive();
        assertThat(contarCuotasSinTotal(connection, serie))
                .as("cada cuota debe tener su chequera_total padre")
                .isZero();
        assertThat(contarCuotasSinAlternativa(connection, serie))
                .as("cada cuota debe tener su chequera_alternativa padre")
                .isZero();

        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT total.cht_pro_id, total.cht_total, "
                        + "COALESCE(SUM(CASE WHEN cuota.chc_baja = 0 THEN cuota.chc_1er_importe END), 0) "
                        + "FROM chequera_total total LEFT JOIN chequera_cuota cuota "
                        + "ON cuota.chc_fac_id = total.cht_fac_id AND cuota.chc_tch_id = total.cht_tch_id "
                        + "AND cuota.chc_chs_id = total.cht_chs_id AND cuota.chc_pro_id = total.cht_pro_id "
                        + "WHERE total.cht_fac_id = ? AND total.cht_tch_id = ? AND total.cht_chs_id = ? "
                        + "GROUP BY total.cht_pro_id, total.cht_total")) {
            statement.setInt(1, FACULTAD_ID);
            statement.setInt(2, TIPO_CHEQUERA_ID);
            statement.setLong(3, serie);
            try (ResultSet result = statement.executeQuery()) {
                int totals = 0;
                while (result.next()) {
                    totals++;
                    assertThat(result.getBigDecimal(2))
                            .as("total del producto %s", result.getInt(1))
                            .isEqualByComparingTo(result.getBigDecimal(3));
                }
                assertThat(totals).isPositive();
            }
        }
    }

    private static long contarCuotasSinTotal(Connection connection, long serie) throws Exception {
        return contarCuotasSinPadre(connection, serie, "chequera_total total",
                "total.cht_fac_id = cuota.chc_fac_id AND total.cht_tch_id = cuota.chc_tch_id "
                        + "AND total.cht_chs_id = cuota.chc_chs_id AND total.cht_pro_id = cuota.chc_pro_id",
                "total.cht_id");
    }

    private static long contarCuotasSinAlternativa(Connection connection, long serie) throws Exception {
        return contarCuotasSinPadre(connection, serie, "chequera_alternativa alternativa",
                "alternativa.cha_fac_id = cuota.chc_fac_id AND alternativa.cha_tch_id = cuota.chc_tch_id "
                        + "AND alternativa.cha_chs_id = cuota.chc_chs_id AND alternativa.cha_pro_id = cuota.chc_pro_id "
                        + "AND alternativa.cha_alt_id = cuota.chc_alt_id",
                "alternativa.cha_id");
    }

    private static long contarCuotasSinPadre(Connection connection, long serie, String tablaPadre,
                                              String condicionPadre, String idPadre) throws Exception {
        String sql = "SELECT COUNT(*) FROM chequera_cuota cuota LEFT JOIN " + tablaPadre + " ON "
                + condicionPadre + " WHERE cuota.chc_fac_id = ? AND cuota.chc_tch_id = ? "
                + "AND cuota.chc_chs_id = ? AND " + idPadre + " IS NULL";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, FACULTAD_ID);
            statement.setInt(2, TIPO_CHEQUERA_ID);
            statement.setLong(3, serie);
            try (ResultSet result = statement.executeQuery()) {
                result.next();
                return result.getLong(1);
            }
        }
    }

    private static List<Long> seriesDelDni(Connection connection) throws Exception {
        List<Long> series = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT chs_id FROM chequera_serie WHERE chs_per_id = ? AND chs_doc_id = ?")) {
            statement.setBigDecimal(1, PERSONA);
            statement.setInt(2, DOCUMENTO_ID);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    series.add(rs.getLong(1));
                }
            }
        }
        return series;
    }

    private static BigDecimal duenhoDeLaSerie(Connection connection, long serie) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT chs_per_id FROM chequera_serie"
                        + " WHERE chs_fac_id = ? AND chs_tch_id = ? AND chs_id = ?")) {
            statement.setInt(1, FACULTAD_ID);
            statement.setInt(2, TIPO_CHEQUERA_ID);
            statement.setLong(3, serie);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? rs.getBigDecimal(1) : null;
            }
        }
    }

    private static long contarPorDni(Connection connection, String tabla, String[] columnas)
            throws Exception {
        try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM " + tabla
                + " WHERE " + columnas[0] + " = ? AND " + columnas[1] + " = ?")) {
            statement.setBigDecimal(1, PERSONA);
            statement.setInt(2, DOCUMENTO_ID);
            try (ResultSet rs = statement.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }
        }
    }

    private static long contarPorSerie(Connection connection, String tabla, String prefijo, long serie)
            throws Exception {
        try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM " + tabla
                + " WHERE " + prefijo + "_fac_id = ? AND " + prefijo + "_tch_id = ?"
                + " AND " + prefijo + "_chs_id = ?")) {
            statement.setInt(1, FACULTAD_ID);
            statement.setInt(2, TIPO_CHEQUERA_ID);
            statement.setLong(3, serie);
            try (ResultSet rs = statement.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }
        }
    }

    /** Si el DNI sintético ya tuviera datos en cualquier tabla, el test aborta sin escribir nada. */
    private static void exigirDniLibre(Connection connection) throws Exception {
        for (Map.Entry<String, String[]> tabla : TABLAS_POR_DNI.entrySet()) {
            long filas = contarPorDni(connection, tabla.getKey(), tabla.getValue());
            assertThat(filas)
                    .as("el DNI sintético %s ya tiene filas en %s: elegir otro DNI", PERSONA, tabla.getKey())
                    .isZero();
        }
    }

    private static void borrar(Connection connection, String sql, Object... parametros) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) {
                statement.setObject(i + 1, parametros[i]);
            }
            int borradas = statement.executeUpdate();
            if (borradas > 0) {
                log.warn("Limpieza: {} filas borradas por [{}]", borradas, sql);
            }
        }
    }

    /**
     * Conserva únicamente los datos académicos que determinan el flujo reportado. Todos los datos
     * personales e identificadores de Guaraní son sintéticos. propuestaRel se conserva para armar
     * el request que consume createPreuniversitario.
     */
    private static AlumnoGuaraniRequest requestDesdePayload() {
        return AlumnoGuaraniRequest.builder()
                .alumno(ALUMNO_GUARANI)
                .persona((long) PERSONA_GUARANI)
                .propuesta(PROPUESTA)
                .planVersion(280)
                .ubicacion(UBICACION)
                .modalidad("P")
                .division("43")
                .cantidadReadmisiones(0)
                .regular("S")
                .calidad("A")
                .personaRel(PersonaGuarani.builder()
                        .persona((long) PERSONA_GUARANI)
                        .apellido("APELLIDO PRUEBA")
                        .nombres("Nombre Prueba")
                        .sexo("M")
                        .documentoPrincipalRel(DocumentoPrincipalGuarani.builder()
                                .documento(DOCUMENTO_GUARANI)
                                .persona((long) PERSONA_GUARANI)
                                .paisDocumento(54)
                                .tipoDocumento(0)
                                .tipoDocumentoRel(TipoDocumentoGuarani.builder().tipoDocumento(0).build())
                                .nroDocumento(PERSONA.toPlainString())
                                .validadoConRenaper("N")
                                .build())
                        .contactos(List.of(
                                ContactoGuarani.builder().personaContacto(CONTACTO_TELEFONO_GUARANI)
                                        .persona((long) PERSONA_GUARANI).contactoTipo("C")
                                        .telefonoCodigoArea("000").telefonoNumero("0000000").build(),
                                ContactoGuarani.builder().personaContacto(CONTACTO_EMAIL_GUARANI)
                                        .persona((long) PERSONA_GUARANI).contactoTipo("MP")
                                        .email("integracion@example.invalid").build()))
                        .requisitosPresentados(requisitos())
                        .usuario(PERSONA.toPlainString())
                        .autenticacion("md5")
                        .bloqueado(0)
                        .emailValido(0)
                        .tipoUsuarioInicial("Alumno")
                        .build())
                .propuestaRel(PropuestaGuarani.builder()
                        .propuesta(PROPUESTA)
                        .nombre("Preuniversitario FI - Informática - Sep 2026")
                        .nombreAbreviado("Preuniversitario FI - Informática - Sep 2026")
                        .codigo("07PRE01")
                        .propuestaTipo(204)
                        .responsablesAcademicas(List.of(PropuestaResponsableAcademicaGuarani.builder()
                                .propuesta(PROPUESTA)
                                .responsableAcademica(RESPONSABLE_ACADEMICA)
                                .informaAraucanoCodigoUa("N")
                                .build()))
                        .publica("S")
                        .entidad(7076L)
                        .estado("A")
                        .build())
                .build();
    }

    private static List<RequisitoPresentadoGuarani> requisitos() {
        return REQUISITOS.stream().map(requisito -> RequisitoPresentadoGuarani.builder()
                .persona(PERSONA_GUARANI)
                .requisito(requisito)
                .build()).toList();
    }
}
