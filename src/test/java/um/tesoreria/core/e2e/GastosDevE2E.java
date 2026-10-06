package um.tesoreria.core.e2e;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * E2E de gastos (#405): HTTP contra la app levantada ({@code E2E_BASE_URL}) conectada a la base de desarrollo real.
 * Escribe en tablas reales de dev, por eso solo corre a pedido: requiere {@code E2E_BASE_URL} además de {@code IT_DB_*}
 * y no lo levantan ni surefire ni failsafe por nombre ({@code -Dit.test=GastosDevE2E} lo ejecuta).
 * <p>
 * Aislamiento: solo usa ids reservados (999001..999004) marcados con {@link #MARCA}; si alguno existe, aborta sin escribir.
 * Cada escenario limpia lo suyo aunque falle; al final se barre por la marca, se restauran los AUTO_INCREMENT y se
 * comprueba que el CHECKSUM de las tablas tocadas es el de antes. La base se usa solo para verificar estado y limpiar;
 * el comportamiento se ejerce únicamente por HTTP.
 */
@EnabledIfEnvironmentVariable(named = "E2E_BASE_URL", matches = ".+")
@EnabledIfEnvironmentVariable(named = "IT_DB_HOST", matches = ".+")
class GastosDevE2E {

    static final String MARCA = "E2E-405";
    static final long ID_ALTA = 999_001L, ID_ASIGNACION = 999_002L, ID_BAJA = 999_003L, ID_INEXISTENTE = 999_404L;
    static final List<String> TABLAS = List.of("articulos", "ubicacion_articulo", "gestion_escritura_historial");

    static final HttpClient HTTP = HttpClient.newBuilder().connectTimeout(java.time.Duration.ofSeconds(5)).build();
    static final JsonMapper JSON = JsonMapper.builder().build();
    static String base;
    static Connection db;
    static Map<String, Long> estadoInicial;
    static Map<String, Long> autoIncrementInicial;

    Integer ubicacion;
    BigDecimal cuentaA, cuentaB, cuentaInexistente;

    @BeforeAll
    static void conectarYFotografiar() throws Exception {
        base = System.getenv("E2E_BASE_URL").replaceAll("/$", "");
        db = DriverManager.getConnection("jdbc:mysql://%s:%s/%s?useSSL=false&connectTimeout=5000".formatted(
                        System.getenv("IT_DB_HOST"), System.getenv().getOrDefault("IT_DB_PORT", "3306"),
                        System.getenv().getOrDefault("IT_DB_NAME", "tesium")),
                System.getenv("IT_DB_USER"), System.getenv("IT_DB_PASSWORD"));
        assumeTrue(numero("SELECT COUNT(*) FROM articulos WHERE Art_ID BETWEEN 999001 AND 999404") == 0,
                "los ids reservados del E2E ya existen en dev: no se toca nada");
        assumeTrue(numero("SELECT COUNT(*) FROM ubicacion_articulo WHERE articulo_id BETWEEN 999001 AND 999404") == 0,
                "hay vínculos con ids reservados del E2E: no se toca nada");
        estadoInicial = checksums();
        autoIncrementInicial = autoIncrements();
    }

    @BeforeEach
    void datosDeReferencia() throws SQLException {
        ubicacion = (int) numero("SELECT MIN(ubicacion_id) FROM ubicacion");
        var cuentas = db.createStatement().executeQuery("SELECT pla_cuenta FROM plancta ORDER BY pla_cuenta LIMIT 2");
        cuentas.next(); cuentaA = cuentas.getBigDecimal(1);
        cuentas.next(); cuentaB = cuentas.getBigDecimal(1);
        cuentaInexistente = BigDecimal.valueOf(numero("SELECT MAX(pla_cuenta) + 1 FROM plancta"));
    }

    @AfterEach
    void limpiarLoPropio() throws SQLException {
        barrer();
    }

    @AfterAll
    static void restaurarYVerificar() throws Exception {
        if (db == null) return;
        try {
            if (estadoInicial == null) return; // abortó antes de escribir
            barrer();
            restaurarAutoIncrements();
            assertThat(autoIncrements()).as("AUTO_INCREMENT como antes").isEqualTo(autoIncrementInicial);
            assertThat(checksums()).as("tablas tocadas idénticas a antes del E2E (si difiere, ver si otro usuario escribió en dev)")
                    .isEqualTo(estadoInicial);
        } finally {
            db.close();
        }
    }

    @Test
    void altaYEdicion() throws Exception {
        var alta = post("/articulo/", gasto(ID_ALTA, MARCA + " alta"));
        assertThat(alta.statusCode()).isEqualTo(201);
        assertThat(fila(ID_ALTA)).containsEntry("nombre", MARCA + " alta").containsEntry("habilitado", 1L).containsEntry("tipo", "gasto");

        // Doble clic o id tomado: 409 y la fila no cambia
        var repetida = post("/articulo/", gasto(ID_ALTA, MARCA + " pisado"));
        assertProblema(repetida, 409, "ARTICULO_ID_DUPLICADO");
        assertThat(fila(ID_ALTA)).containsEntry("nombre", MARCA + " alta");

        // PUT parcial: lo ausente se conserva
        assertThat(put("/articulo/" + ID_ALTA, "{\"nombre\":\"" + MARCA + " editado\"}").statusCode()).isEqualTo(200);
        assertThat(fila(ID_ALTA)).containsEntry("nombre", MARCA + " editado").containsEntry("habilitado", 1L).containsEntry("tipo", "gasto");

        // Cero explícito se aplica
        assertThat(put("/articulo/" + ID_ALTA, "{\"habilitado\":0}").statusCode()).isEqualTo(200);
        assertThat(fila(ID_ALTA)).containsEntry("habilitado", 0L);

        // Cuenta inexistente: la FK real articulos_ibfk_1 lo rechaza; 400 con el campo y sin cambios
        var cuentaMala = put("/articulo/" + ID_ALTA, "{\"numeroCuenta\":" + cuentaInexistente + ",\"nombre\":\"" + MARCA + " no\"}");
        assertProblema(cuentaMala, 400, "CAMPO_INVALIDO");
        assertThat(campo(cuentaMala, "campo")).isEqualTo("numeroCuenta");
        assertThat(fila(ID_ALTA)).containsEntry("nombre", MARCA + " editado").containsEntry("cuenta", null);

        // Cuenta existente se aplica
        assertThat(put("/articulo/" + ID_ALTA, "{\"numeroCuenta\":" + cuentaA + "}").statusCode()).isEqualTo(200);
        assertThat((BigDecimal) fila(ID_ALTA).get("cuenta")).isEqualByComparingTo(cuentaA);
    }

    @Test
    void asignacionIdempotenteYFkReales() throws Exception {
        assertThat(post("/articulo/", gasto(ID_ASIGNACION, MARCA + " asignacion")).statusCode()).isEqualTo(201);

        var primera = post("/ubicacionArticulo/", asignacion(ubicacion, ID_ASIGNACION, cuentaA));
        assertThat(primera.statusCode()).isEqualTo(200);
        var segunda = post("/ubicacionArticulo/", asignacion(ubicacion, ID_ASIGNACION, cuentaB));
        assertThat(segunda.statusCode()).isEqualTo(200);
        assertThat(campo(segunda, "ubicacionArticuloId")).isEqualTo(campo(primera, "ubicacionArticuloId"));
        assertThat(new BigDecimal(campo(segunda, "numeroCuenta"))).isEqualByComparingTo(cuentaB);
        assertThat(new BigDecimal(json(segunda).at("/cuenta/numeroCuenta").asString())).as("cuenta releída")
                .isEqualByComparingTo(cuentaB);
        assertThat(numero("SELECT COUNT(*) FROM ubicacion_articulo WHERE articulo_id = " + ID_ASIGNACION)).isEqualTo(1);

        // FK reales: nada se inserta
        var ubicacionMala = post("/ubicacionArticulo/", asignacion((int) numero("SELECT MAX(ubicacion_id) + 1000 FROM ubicacion"), ID_ASIGNACION, cuentaA));
        assertProblema(ubicacionMala, 400, "CAMPO_INVALIDO");
        assertThat(campo(ubicacionMala, "campo")).isEqualTo("ubicacionId");
        var articuloMalo = post("/ubicacionArticulo/", asignacion(ubicacion, ID_INEXISTENTE, cuentaA));
        assertProblema(articuloMalo, 400, "CAMPO_INVALIDO");
        assertThat(campo(articuloMalo, "campo")).isEqualTo("articuloId");
        var cuentaMala = post("/ubicacionArticulo/", asignacion(ubicacion, ID_ASIGNACION, cuentaInexistente));
        assertProblema(cuentaMala, 400, "CAMPO_INVALIDO");
        assertThat(campo(cuentaMala, "campo")).isEqualTo("numeroCuenta");
        assertThat(numero("SELECT COUNT(*) FROM ubicacion_articulo WHERE articulo_id BETWEEN 999001 AND 999404")).isEqualTo(1);

        // Baja de un artículo con vínculo: FK real ubicacion_articulo_ibfk_2 -> 409 y nada se borra (hasta el PR 2)
        var baja = delete("/articulo/" + ID_ASIGNACION);
        assertProblema(baja, 409, "ARTICULO_REFERENCIADO");
        assertThat(json(baja).at("/referencias/0/tabla").asString()).isEqualTo("ubicacion_articulo");
        assertThat(numero("SELECT COUNT(*) FROM articulos WHERE Art_ID = " + ID_ASIGNACION)).isEqualTo(1);
    }

    @Test
    void bajaDeArticuloRealConEntregas_409SinBorrar() throws Exception {
        long real = numero("SELECT MIN(NeD_Art_ID) FROM entrega_detalle WHERE NeD_Art_ID > 0");
        var antes = numero("SELECT COUNT(*) FROM articulos WHERE Art_ID = " + real);

        var baja = delete("/articulo/" + real);

        assertProblema(baja, 409, "ARTICULO_REFERENCIADO");
        // MySQL informa la primera FK que falla; este artículo tiene entregas y vínculos
        assertThat(json(baja).at("/referencias/0/tabla").asString()).isIn("entrega_detalle", "ubicacion_articulo");
        assertThat(numero("SELECT COUNT(*) FROM articulos WHERE Art_ID = " + real)).isEqualTo(antes).isEqualTo(1);
    }

    @Test
    void bajaLibre_204YDesaparece() throws Exception {
        assertThat(post("/articulo/", gasto(ID_BAJA, MARCA + " baja")).statusCode()).isEqualTo(201);

        assertThat(delete("/articulo/" + ID_BAJA).statusCode()).isEqualTo(204);

        assertThat(numero("SELECT COUNT(*) FROM articulos WHERE Art_ID = " + ID_BAJA)).isZero();
        assertProblema(get("/articulo/" + ID_BAJA), 404, "ARTICULO_NO_ENCONTRADO");
        assertProblema(delete("/articulo/" + ID_BAJA), 404, "ARTICULO_NO_ENCONTRADO");
    }

    // --- limpieza y estado ---

    /** Borra solo filas de ids reservados; un artículo sin la marca no se toca (lo habría creado otro). */
    static void barrer() throws SQLException {
        try (var st = db.createStatement()) {
            st.executeUpdate("DELETE FROM ubicacion_articulo WHERE articulo_id BETWEEN 999001 AND 999404");
            st.executeUpdate("DELETE FROM articulos WHERE Art_ID BETWEEN 999001 AND 999404 AND Art_Nombre LIKE '" + MARCA + "%'");
        }
    }

    static void restaurarAutoIncrements() throws SQLException {
        try (var st = db.createStatement()) {
            // Sin trabar a otros: si alguien tiene la tabla tomada, falla rápido en lugar de encolar
            st.execute("SET SESSION lock_wait_timeout = 5");
            for (var e : autoIncrementInicial.entrySet()) {
                if (!e.getValue().equals(autoIncrements().get(e.getKey()))) {
                    st.execute("ALTER TABLE " + e.getKey() + " AUTO_INCREMENT = " + e.getValue());
                }
            }
        }
    }

    static Map<String, Long> checksums() throws SQLException {
        Map<String, Long> r = new LinkedHashMap<>();
        for (var t : TABLAS) {
            try (var rs = db.createStatement().executeQuery("CHECKSUM TABLE " + t)) {
                rs.next();
                r.put(t + ".checksum", rs.getLong(2));
            }
            r.put(t + ".filas", numero("SELECT COUNT(*) FROM " + t));
        }
        return r;
    }

    static Map<String, Long> autoIncrements() throws SQLException {
        Map<String, Long> r = new LinkedHashMap<>();
        // information_schema.TABLES cachea AUTO_INCREMENT en 5.7: se fuerza la lectura
        try (var st = db.createStatement()) {
            st.execute("SET SESSION information_schema_stats_expiry = 0");
        } catch (SQLException sinVariable) {
            // MySQL 5.7 no tiene esa variable y lee el valor actual
        }
        for (var t : List.of("articulos", "ubicacion_articulo")) {
            try (var rs = db.createStatement().executeQuery("SHOW TABLE STATUS LIKE '" + t + "'")) {
                rs.next();
                r.put(t, rs.getLong("Auto_increment"));
            }
        }
        return r;
    }

    static long numero(String sql) throws SQLException {
        try (var rs = db.createStatement().executeQuery(sql)) {
            rs.next();
            return rs.getLong(1);
        }
    }

    static Map<String, Object> fila(long id) throws SQLException {
        try (var rs = db.createStatement().executeQuery(
                "SELECT Art_Nombre, Art_Tipo, Art_Habilitado + 0, Art_Cuenta FROM articulos WHERE Art_ID = " + id)) {
            assertThat(rs.next()).as("existe el artículo " + id).isTrue();
            Map<String, Object> r = new LinkedHashMap<>();
            r.put("nombre", rs.getString(1));
            r.put("tipo", rs.getString(2));
            r.put("habilitado", rs.getLong(3));
            r.put("cuenta", rs.getBigDecimal(4));
            return r;
        }
    }

    // --- HTTP ---

    static String gasto(long id, String nombre) {
        return "{\"articuloId\":%d,\"nombre\":\"%s\",\"descripcion\":\"E2E\",\"unidad\":\"UN\",\"tipo\":\"gasto\",\"directo\":0,\"habilitado\":1}"
                .formatted(id, nombre);
    }

    static String asignacion(int ubicacionId, long articuloId, BigDecimal cuenta) {
        return "{\"ubicacionId\":%d,\"articuloId\":%d,\"numeroCuenta\":%s}".formatted(ubicacionId, articuloId, cuenta);
    }

    static HttpResponse<String> post(String ruta, String cuerpo) throws Exception {
        return enviar(HttpRequest.newBuilder(URI.create(base + ruta)).header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(cuerpo)));
    }

    static HttpResponse<String> put(String ruta, String cuerpo) throws Exception {
        return enviar(HttpRequest.newBuilder(URI.create(base + ruta)).header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(cuerpo)));
    }

    static HttpResponse<String> delete(String ruta) throws Exception {
        return enviar(HttpRequest.newBuilder(URI.create(base + ruta)).DELETE());
    }

    static HttpResponse<String> get(String ruta) throws Exception {
        return enviar(HttpRequest.newBuilder(URI.create(base + ruta)).GET());
    }

    static HttpResponse<String> enviar(HttpRequest.Builder pedido) throws Exception {
        var respuesta = HTTP.send(pedido.timeout(java.time.Duration.ofSeconds(30)).build(), HttpResponse.BodyHandlers.ofString());
        LOG.add(pedido.build().method() + " " + pedido.build().uri().getPath() + " -> " + respuesta.statusCode() + " " + respuesta.body());
        return respuesta;
    }

    static final List<String> LOG = new ArrayList<>();

    static JsonNode json(HttpResponse<String> r) {
        return JSON.readTree(r.body());
    }

    static String campo(HttpResponse<String> r, String nombre) {
        var nodo = json(r).get(nombre);
        return nodo == null || nodo.isNull() ? null : nodo.asString();
    }

    static void assertProblema(HttpResponse<String> r, int estado, String codigo) {
        assertThat(r.statusCode()).as(r.body()).isEqualTo(estado);
        assertThat(r.headers().firstValue("Content-Type")).hasValueSatisfying(ct -> assertThat(ct).startsWith("application/problem+json"));
        assertThat(campo(r, "codigo")).isEqualTo(codigo);
        assertThat(campo(r, "detail")).isNotBlank();
    }

    @AfterAll
    static void imprimirIntercambios() {
        LOG.forEach(System.out::println);
    }
}
