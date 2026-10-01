package um.tesoreria.core.hexagonal.gestion.escrituraHistorial.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.domain.model.EscrituraOperacion;

import java.time.OffsetDateTime;

/**
 * Persistencia del historial transaccional de escrituras de Gestión.
 * No extiende {@code Auditable}: la fecha del evento es {@code fecha}.
 */
@Getter
@Setter
@Entity
@Table(name = "gestion_escritura_historial")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EscrituraHistorialEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "escritura_historial_id")
    private Long escrituraHistorialId;

    @Column(name = "fecha", nullable = false)
    private OffsetDateTime fecha;

    @Enumerated(EnumType.STRING)
    @Column(name = "operacion", nullable = false, length = 16)
    private EscrituraOperacion operacion;

    @Column(name = "entidad", nullable = false, length = 128)
    private String entidad;

    @Column(name = "entidad_clave", nullable = false, length = 255)
    private String entidadClave;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "valor_anterior")
    private String valorAnterior;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "valor_nuevo")
    private String valorNuevo;
}
