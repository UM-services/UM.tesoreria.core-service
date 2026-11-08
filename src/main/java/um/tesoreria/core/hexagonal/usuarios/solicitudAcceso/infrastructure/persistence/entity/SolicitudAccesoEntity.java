package um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import um.tesoreria.core.model.Auditable;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "solicitud_acceso")
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudAccesoEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "solicitud_acceso_id")
    private Long solicitudAccesoId;

    @Column(name = "user_id")
    private Long userId;

    @Builder.Default
    @Column(name = "aplicacion", nullable = false)
    private String aplicacion = "TESORERIA";

    @Column(name = "clave")
    private String clave;

    @Column(name = "modulo")
    private String modulo;

    @Column(name = "comentario")
    private String comentario;

    @Builder.Default
    @Column(name = "estado", nullable = false)
    private String estado = "PENDIENTE";

    @Column(name = "observacion")
    private String observacion;

    @Column(name = "resuelto_por")
    private Long resueltoPor;

    @Column(name = "resuelto")
    private LocalDateTime resuelto;
}
