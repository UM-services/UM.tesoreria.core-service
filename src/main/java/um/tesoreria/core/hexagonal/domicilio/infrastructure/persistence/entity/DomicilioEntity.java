package um.tesoreria.core.hexagonal.domicilio.infrastructure.persistence.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import um.tesoreria.core.model.Auditable;
import um.tesoreria.core.util.Jsonifier;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "domicilio", uniqueConstraints = @UniqueConstraint(columnNames = {"dom_per_id", "dom_doc_id"}))
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DomicilioEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dom_id")
    private Long domicilioId;

    @Column(name = "dom_per_id")
    private BigDecimal personaId;

    @Column(name = "dom_doc_id")
    private Integer documentoId;

    @Column(name = "dom_fecha")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    @Builder.Default
    private OffsetDateTime fecha = OffsetDateTime.now();

    @Column(name = "dom_calle")
    @Builder.Default
    private String calle = "";

    @Column(name = "dom_puerta")
    @Builder.Default
    private String puerta = "";

    @Column(name = "dom_piso")
    @Builder.Default
    private String piso = "";

    @Column(name = "dom_dpto")
    @Builder.Default
    private String dpto = "";

    @Column(name = "dom_telefono")
    @Builder.Default
    private String telefono = "";

    @Column(name = "dom_movil")
    @Builder.Default
    private String movil = "";

    @Column(name = "dom_observ")
    @Builder.Default
    private String observaciones = "";

    @Column(name = "dom_codpostal")
    @Builder.Default
    private String codigoPostal = "";

    @Column(name = "dom_fac_id")
    private Integer facultadId;

    @Column(name = "dom_prv_id")
    private Integer provinciaId;

    @Column(name = "dom_loc_id")
    private Integer localidadId;

    @Column(name = "dom_e_mail")
    @Builder.Default
    private String emailPersonal = "";

    @Column(name = "mail_institucional")
    @Builder.Default
    private String emailInstitucional = "";

    @Column(name = "dom_laboral")
    @Builder.Default
    private String laboral = "";

    @Transient
    @Builder.Default
    private String emailPagador = "";

    public String jsonify() {
        return Jsonifier.builder(this).build();
    }

}
