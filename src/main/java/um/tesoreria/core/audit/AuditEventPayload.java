package um.tesoreria.core.audit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

/**
 * Payload que viaja al servicio de auditoría y se guarda en audit_outbox.
 * Los nombres coinciden con el contrato de tesoreria-auditoria.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditEventPayload {

    private String eventId;

    private OffsetDateTime occurredAt;

    private String correlationId;

    private String service;

    private String level;

    private String operation;

    private String entity;

    private String entityId;

    private String actorType;

    private Long actorId;

    private String actorLogin;

    private String actorTrust;

    private String clientIp;

    private String httpMethod;

    private String endpoint;

    private String outcome;

    private Integer httpStatus;

    private Integer affectedRows;

    private List<String> changedFields;

    private Map<String, Object> beforeData;

    private Map<String, Object> afterData;

    private Map<String, Object> metadata;
}
