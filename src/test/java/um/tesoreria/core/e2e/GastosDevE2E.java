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
 * Aislamiento ({@link DevDbReservas}): solo ids reservados con la marca; si alguno existe, aborta sin escribir. Cada
 * escenario limpia lo suyo aunque falle; al final se restauran los AUTO_INCREMENT y se exige el mismo CHECKSUM que antes.
 * La única baja sobre un artículo real (con entregas) lleva respaldo y se repone si una regresión la dejara pasar.
 * La base se usa solo para verificar estado y limpiar; el comportamiento se ejerce únicamente por HTTP.
 */
@EnabledIfEnvironmentVariable(named = "E2E_BASE_URL", matches = ".+")
@EnabledIfEnvironmentVariable(named = "IT_DB_HOST", matches = ".+")
class GastosDevE2E {

    static final String MARCA = DevDbReservas.MARCA;
    static final long ID_ALTA = 999_001L, ID_ASIGNACION = 999_002L, ID_BAJA = 999_003L, ID_INEXISTENTE = 999_404L;

    static final HttpClient HTTP = HttpClient.newBuilder().connectTimeout(java.time.Duration.ofSeconds(5)).build();
    static final JsonMapper JSON = JsonMapper.builder().build();
    static String base;
    static DevDbReservas dev;

    Integer ubicacion;
    BigDecimal cuentaA, cuentaB, cuentaInexistente;

    @BeforeAll
    static void conectarYFotografiar() throws Exception {
        base = System.getenv("E2E_BASE_URL").replaceAll("/$", "");
        dev = DevDbReservas.abrir();
        assumeTrue(dev.rangoLibre(), "hay filas con ids reservados del E2E en dev: no se toca nada");
        dev.fotografiar();
    }

    @BeforeEach
    void datosDeReferencia() throws SQLException {
        ubicacion = (int) numero("SELECT MIN(ubicacion_id) FROM ubicacion");
        try (var st = dev.db.createStatement(); var cuentas = st.executeQuery("SELECT pla_cuenta FROM plancta ORDER BY pla_cuenta LIMIT 2")) {
            cuentas.next(); cuentaA = cuentas.getBigDecimal(1);
            cuentas.next(); cuentaB = cuentas.getBigDecimal(1);
        }
        cuentaInexistente = BigDecimal.valueOf(numero("SELECT MAX(pla_cuenta) + 1 FROM plancta"));
    }

    @AfterEach
    void limpiarLoPropio() throws SQLException {
        dev.barrer();
    }

    @AfterAll
    static void restaurarYVerificar() throws Exception {
        if (dev == null) return;
        try {
            if (dev.fotografiado()) dev.restaurarYVerificar(); // si abortó antes de escribir, no hay nada que restaurar
        } finally {
            dev.close();
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
        // Es un artículo real: si una regresión dejara pasar la baja, se repone tal cual estaba
        var respaldo = dev.respaldar(real);

        HttpResponse<String> baja;
        try {
            baja = delete("/articulo/" + real);
        } finally {
            assertThat(respaldo.restaurarSiFalta()).as("la baja de un artículo con entregas no debe borrar nada").isFalse();
        }

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

    // --- estado ---

    static long numero(String sql) throws SQLException {
        return dev.numero(sql);
    }

    static Map<String, Object> fila(long id) throws SQLException {
        try (var st = dev.db.createStatement(); var rs = st.executeQuery(
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
