package um.tesoreria.core.configuration;

import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/** Sin base: el dialecto se construye como lo hace Hibernate al arrancar contra MySQL 5.7.43. */
class MySQL57DialectTest {

    @Test
    void conservaLaVersionDelServidor_comoLoConstruyeHibernate() throws Exception {
        // Hibernate usa el constructor con DialectResolutionInfo si existe; si no, el vacío (que asume MySQL 8.0)
        var dialecto = MySQL57Dialect.class.getConstructor(DialectResolutionInfo.class).newInstance(servidor(5, 7, 43));

        assertThat(dialecto.getVersion().getDatabaseMajorVersion()).isEqualTo(5);
        assertThat(dialecto.getVersion().getDatabaseMinorVersion()).isEqualTo(7);
    }

    @Test
    void bloqueoSinListaDeAlias() {
        var dialecto = new MySQL57Dialect(servidor(5, 7, 43));

        assertThat(dialecto.getWriteLockString("a1_0", -1).trim()).isEqualToIgnoringCase("for update");
    }

    private static DialectResolutionInfo servidor(int mayor, int menor, int micro) {
        return new DialectResolutionInfo() {
            @Override public String getDatabaseName() { return "MySQL"; }
            @Override public String getDatabaseVersion() { return mayor + "." + menor + "." + micro; }
            @Override public int getDatabaseMajorVersion() { return mayor; }
            @Override public int getDatabaseMinorVersion() { return menor; }
            @Override public int getDatabaseMicroVersion() { return micro; }
            @Override public String getDriverName() { return "MySQL Connector/J"; }
            @Override public int getDriverMajorVersion() { return 9; }
            @Override public int getDriverMinorVersion() { return 7; }
            @Override public String getSQLKeywords() { return ""; }
        };
    }
}
