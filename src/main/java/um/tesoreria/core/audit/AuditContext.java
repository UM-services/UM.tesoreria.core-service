package um.tesoreria.core.audit;

import lombok.Getter;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Contexto del request actual, leído por los listeners de Hibernate.
 *
 * <p>Tesorería todavía no emite JWT: el actor viaja por header ({@code X-User-Id}) y se
 * marca como {@code CLIENT}. Si no viene, el evento queda como {@code ANONYMOUS}.</p>
 */
public final class AuditContext {

    private static final ThreadLocal<Data> HOLDER = new ThreadLocal<>();

    private AuditContext() {
    }

    public static void set(Data data) {
        HOLDER.set(data);
    }

    public static Data get() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }

    public static String correlationId() {
        Data data = HOLDER.get();
        return data == null ? null : data.getCorrelationId();
    }

    public static Long actorId() {
        Data data = HOLDER.get();
        return data == null ? null : data.getActorId();
    }

    public static String actorLogin() {
        Data data = HOLDER.get();
        return data == null ? null : data.getActorLogin();
    }

    public static String clientIp() {
        Data data = HOLDER.get();
        return data == null ? null : data.getClientIp();
    }

    public static String httpMethod() {
        Data data = HOLDER.get();
        return data == null ? null : data.getHttpMethod();
    }

    public static String endpoint() {
        Data data = HOLDER.get();
        return data == null ? null : data.getEndpoint();
    }

    public static String actorType() {
        Data data = HOLDER.get();
        if (data == null) {
            return "SYSTEM";
        }
        return data.getActorId() != null ? "USER" : "ANONYMOUS";
    }

    /** Mientras el actor venga del cliente (header), la identidad no es no repudiable. */
    public static String actorTrust() {
        return HOLDER.get() == null ? "SYSTEM" : "CLIENT";
    }

    public static long startedAtMillis() {
        Data data = HOLDER.get();
        return data == null ? System.currentTimeMillis() : data.getStartedAtMillis();
    }

    public static void incrementChanges() {
        Data data = HOLDER.get();
        if (data != null) {
            data.getChanges().incrementAndGet();
        }
    }

    public static int changes() {
        Data data = HOLDER.get();
        return data == null ? 0 : data.getChanges().get();
    }

    @Getter
    public static final class Data {
        private final String correlationId;
        private final Long actorId;
        private final String actorLogin;
        private final String clientIp;
        private final String httpMethod;
        private final String endpoint;
        private final long startedAtMillis;
        private final AtomicInteger changes = new AtomicInteger();

        public Data(String correlationId, Long actorId, String actorLogin, String clientIp,
                    String httpMethod, String endpoint) {
            this.correlationId = correlationId;
            this.actorId = actorId;
            this.actorLogin = actorLogin;
            this.clientIp = clientIp;
            this.httpMethod = httpMethod;
            this.endpoint = endpoint;
            this.startedAtMillis = System.currentTimeMillis();
        }
    }
}
