package um.tesoreria.core.hexagonal.chequera.envio.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioSolicitudEstado;
import um.tesoreria.core.model.Auditable;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@DynamicUpdate
@Table(name = "chequera_envio_solicitud", uniqueConstraints = {
        @UniqueConstraint(name = "uk_chequera_envio_solicitud_chequera", columnNames = "chequera_id"),
        @UniqueConstraint(name = "uk_chequera_envio_solicitud_dedupe", columnNames = "dedupe_key")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnvioSolicitudEntity extends Auditable {

    @Id
    @Column(name = "solicitud_id", nullable = false, updatable = false)
    private UUID solicitudId;

    @Column(name = "chequera_id", nullable = false)
    private Long chequeraId;

    @Column(name = "facultad_id", nullable = false)
    private Integer facultadId;

    @Column(name = "tipo_chequera_id", nullable = false)
    private Integer tipoChequeraId;

    @Column(name = "chequera_serie_id", nullable = false)
    private Long chequeraSerieId;

    @Column(name = "lectivo_id")
    private Integer lectivoId;

    @Column(name = "job_id")
    private Long jobId;

    @Column(name = "origen", length = 20, nullable = false)
    private String origen;

    @Column(name = "dedupe_key", length = 120, nullable = false)
    private String dedupeKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 30, nullable = false)
    private EnvioSolicitudEstado estado;

    @Builder.Default
    @Column(name = "intentos", nullable = false)
    private int intentos = 0;

    @Column(name = "lock_hasta")
    private OffsetDateTime lockHasta;

    @Column(name = "enviado_at")
    private OffsetDateTime enviadoAt;

    @Column(name = "ultimo_error", length = 500)
    private String ultimoError;

}
