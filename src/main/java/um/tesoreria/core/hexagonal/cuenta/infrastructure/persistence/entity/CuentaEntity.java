package um.tesoreria.core.hexagonal.cuenta.infrastructure.persistence.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import um.tesoreria.core.hexagonal.geografica.infrastructure.persistence.entity.GeograficaEntity;
import um.tesoreria.core.kotlin.model.Auditable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "plancta")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuentaEntity extends Auditable {

    @Id
    @Column(name = "pla_cuenta")
    private BigDecimal numeroCuenta;

    @Builder.Default
    @Column(name = "pla_nombre")
    private String nombre = "";

    @Builder.Default
    @Column(name = "pla_integra")
    private Byte integradora = 0;

    @Builder.Default
    @Column(name = "pla_grado")
    private Integer grado = 0;

    @Column(name = "pla_grado1")
    private BigDecimal grado1;

    @Column(name = "pla_grado2")
    private BigDecimal grado2;

    @Column(name = "pla_grado3")
    private BigDecimal grado3;

    @Column(name = "pla_grado4")
    private BigDecimal grado4;

    @Column(name = "geograficaId")
    private Integer geograficaId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    private OffsetDateTime fechaBloqueo;

    @Builder.Default
    private Byte visible = 0;

    @Column(name = "id")
    private Long cuentaContableId;

    @OneToOne(optional = true)
    @JoinColumn(name = "geograficaId", insertable = false, updatable = false)
    private GeograficaEntity geografica;

}
