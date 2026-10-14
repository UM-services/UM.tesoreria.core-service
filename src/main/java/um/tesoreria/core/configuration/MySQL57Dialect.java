package um.tesoreria.core.configuration;

import org.hibernate.dialect.MySQLDialect;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;

/**
 * {@link MySQLDialect} para MySQL 5.7: con {@code PESSIMISTIC_WRITE}, Hibernate 7 agrega la lista de alias
 * ({@code FOR UPDATE OF t}), que solo existe desde MySQL 8. Este dialecto genera {@code FOR UPDATE} simple, así
 * {@code @Lock} funciona en los repositorios. Específico de MySQL: revisar al migrar a Postgres (el dialecto
 * estándar de Postgres lo reemplaza).
 */
public class MySQL57Dialect extends MySQLDialect {

    /** Sin la información del servidor, MySQLDialect asume la versión mínima (8.0) y la configuración por defecto. */
    public MySQL57Dialect(DialectResolutionInfo info) {
        super(info);
    }

    public MySQL57Dialect() {
        super();
    }

    @Override
    protected boolean supportsAliasLocks() {
        return false;
    }

}
