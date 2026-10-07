package um.tesoreria.core.e2e;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Escrituras de prueba sobre tablas reales de la base de desarrollo, aisladas por ids reservados y una marca en el
 * nombre del artículo. Al terminar deja las tablas como estaban: barre lo marcado, restaura los AUTO_INCREMENT y
 * compara el CHECKSUM con la foto inicial. Solo para pruebas a pedido (*E2E), nunca para las que corren solas.
 */
final class DevDbReservas implements AutoCloseable {

    /** Marca de esta corrida: el barrido solo toca lo propio aunque otra corrida use el mismo rango a la vez. */
    static final String MARCA = "E2E-405-" + java.util.UUID.randomUUID().toString().substring(0, 8);
    static final long DESDE = 999_001L, HASTA = 999_404L;
    static final List<String> TABLAS = List.of("articulos", "ubicacion_articulo", "gestion_escritura_historial");
    static final List<String> CON_AUTO_INCREMENT = List.of("articulos", "ubicacion_articulo");

    final Connection db;
    private Map<String, Long> estadoInicial;
    private Map<String, Long> autoIncrementInicial;

    private DevDbReservas(Connection db) {
        this.db = db;
    }

    static DevDbReservas abrir() throws SQLException {
        return new DevDbReservas(DriverManager.getConnection("jdbc:mysql://%s:%s/%s?useSSL=false&connectTimeout=5000".formatted(
                        System.getenv("IT_DB_HOST"), System.getenv().getOrDefault("IT_DB_PORT", "3306"),
                        System.getenv().getOrDefault("IT_DB_NAME", "tesium")),
                System.getenv("IT_DB_USER"), System.getenv("IT_DB_PASSWORD")));
    }

    /** Sin filas en el rango reservado: si hay alguna, la prueba no debe tocar nada. */
    boolean rangoLibre() throws SQLException {
        return numero("SELECT COUNT(*) FROM articulos WHERE Art_ID BETWEEN ? AND ?", DESDE, HASTA) == 0
                && numero("SELECT COUNT(*) FROM ubicacion_articulo WHERE articulo_id BETWEEN ? AND ?", DESDE, HASTA) == 0;
    }

    void fotografiar() throws SQLException {
        estadoInicial = checksums();
        autoIncrementInicial = autoIncrements();
    }

    boolean fotografiado() {
        return estadoInicial != null;
    }

    /** Borra solo artículos del rango con la marca y sus vínculos; lo que no tiene la marca lo creó otro. */
    void barrer() throws SQLException {
        try (var st = db.prepareStatement("DELETE u FROM ubicacion_articulo u JOIN articulos a ON a.Art_ID = u.articulo_id"
                + " WHERE u.articulo_id BETWEEN ? AND ? AND a.Art_Nombre LIKE ?")) {
            st.setLong(1, DESDE);
            st.setLong(2, HASTA);
            st.setString(3, MARCA + "%");
            st.executeUpdate();
        }
        try (var st = db.prepareStatement("DELETE FROM articulos WHERE Art_ID BETWEEN ? AND ? AND Art_Nombre LIKE ?")) {
            st.setLong(1, DESDE);
            st.setLong(2, HASTA);
            st.setString(3, MARCA + "%");
            st.executeUpdate();
        }
    }

    /** Barre, vuelve los AUTO_INCREMENT a la foto y exige que las tablas estén idénticas a antes. */
    void restaurarYVerificar() throws SQLException {
        try {
            barrer();
        } finally {
            restaurarAutoIncrements(); // aunque falle el barrido: un contador cerca de 999xxx afectaría a VB6 y a la app
        }
        assertThat(autoIncrements()).as("AUTO_INCREMENT como antes").isEqualTo(autoIncrementInicial);
        assertThat(checksums()).as("tablas idénticas a antes de la prueba (si difiere, ver si otro usuario escribió en dev)")
                .isEqualTo(estadoInicial);
    }

    private void restaurarAutoIncrements() throws SQLException {
        try (var st = db.createStatement()) {
            // Sin trabar a otros: si alguien tiene la tabla tomada, falla rápido en lugar de encolar
            st.execute("SET SESSION lock_wait_timeout = 5");
            var actuales = autoIncrements();
            for (var e : autoIncrementInicial.entrySet()) {
                if (!e.getValue().equals(actuales.get(e.getKey()))) {
                    st.execute("ALTER TABLE " + e.getKey() + " AUTO_INCREMENT = " + e.getValue());
                }
            }
        }
    }

    /**
     * Copia un artículo real y sus vínculos para devolverlos si una prueba que espera un rechazo los borró.
     * Las filas que lo referencian con FK (entregas) no se pueden haber borrado: la FK es RESTRICT.
     */
    Respaldo respaldar(long articuloId) throws SQLException {
        return new Respaldo(filas("SELECT * FROM articulos WHERE Art_ID = ?", articuloId),
                filas("SELECT * FROM ubicacion_articulo WHERE articulo_id = ?", articuloId), articuloId);
    }

    final class Respaldo {
        private final List<Map<String, Object>> articulo;
        private final List<Map<String, Object>> vinculos;
        private final long articuloId;

        private Respaldo(List<Map<String, Object>> articulo, List<Map<String, Object>> vinculos, long articuloId) {
            this.articulo = articulo;
            this.vinculos = vinculos;
            this.articuloId = articuloId;
        }

        /**
         * Si el artículo ya no está (la prueba lo borró), lo repone con sus vínculos y sus ids originales; devuelve si
         * repuso algo. Con el artículo presente no toca nada: un vínculo faltante lo pudo borrar otro usuario.
         */
        boolean restaurarSiFalta() throws SQLException {
            if (numero("SELECT COUNT(*) FROM articulos WHERE Art_ID = ?", articuloId) > 0) {
                return false;
            }
            for (var fila : articulo) insertar("articulos", fila);
            for (var fila : vinculos) {
                if (numero("SELECT COUNT(*) FROM ubicacion_articulo WHERE ubicacion_articulo_id = ?", fila.get("ubicacion_articulo_id")) == 0) {
                    insertar("ubicacion_articulo", fila);
                }
            }
            return true;
        }
    }

    long numero(String sql, Object... parametros) throws SQLException {
        try (var st = db.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) st.setObject(i + 1, parametros[i]);
            try (var rs = st.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }
        }
    }

    private List<Map<String, Object>> filas(String sql, Object... parametros) throws SQLException {
        List<Map<String, Object>> r = new ArrayList<>();
        try (var st = db.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) st.setObject(i + 1, parametros[i]);
            try (var rs = st.executeQuery()) {
                var md = rs.getMetaData();
                while (rs.next()) {
                    Map<String, Object> fila = new LinkedHashMap<>();
                    for (int c = 1; c <= md.getColumnCount(); c++) fila.put(md.getColumnLabel(c), rs.getObject(c));
                    r.add(fila);
                }
            }
        }
        return r;
    }

    private void insertar(String tabla, Map<String, Object> fila) throws SQLException {
        var columnas = String.join(", ", fila.keySet());
        var marcas = fila.keySet().stream().map(k -> "?").collect(Collectors.joining(", "));
        try (var st = db.prepareStatement("INSERT INTO " + tabla + " (" + columnas + ") VALUES (" + marcas + ")")) {
            int i = 1;
            for (var valor : fila.values()) st.setObject(i++, valor);
            st.executeUpdate();
        }
    }

    private Map<String, Long> checksums() throws SQLException {
        Map<String, Long> r = new LinkedHashMap<>();
        for (var t : TABLAS) {
            try (var st = db.createStatement(); var rs = st.executeQuery("CHECKSUM TABLE " + t)) {
                rs.next();
                r.put(t + ".checksum", rs.getLong(2));
            }
            r.put(t + ".filas", numero("SELECT COUNT(*) FROM " + t));
        }
        return r;
    }

    private Map<String, Long> autoIncrements() throws SQLException {
        Map<String, Long> r = new LinkedHashMap<>();
        for (var t : CON_AUTO_INCREMENT) {
            // SHOW TABLE STATUS de MySQL 5.7 devuelve el contador vigente
            try (var st = db.createStatement(); var rs = st.executeQuery("SHOW TABLE STATUS LIKE '" + t + "'")) {
                rs.next();
                r.put(t, rs.getLong("Auto_increment"));
            }
        }
        return r;
    }

    @Override
    public void close() throws SQLException {
        db.close();
    }
}
