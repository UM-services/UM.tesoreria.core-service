package um.tesoreria.core.hexagonal.articulo.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import um.tesoreria.core.hexagonal.cuenta.infrastructure.persistence.entity.CuentaEntity;
import um.tesoreria.core.kotlin.model.Auditable;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "articulos")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticuloEntity extends Auditable {

    @Id
    @Column(name = "art_id")
    private Long articuloId;

    @Builder.Default
    @Column(name = "art_nombre")
    private String nombre = "";

    @Builder.Default
    @Column(name = "art_descripcion")
    private String descripcion = "";

    @Builder.Default
    @Column(name = "art_unidad")
    private String unidad = "";

    @Builder.Default
    @Column(name = "art_precio")
    private BigDecimal precio = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "art_inventariable")
    private Byte inventariable = 0;

    @Builder.Default
    @Column(name = "art_stockminimo")
    private Long stockMinimo = 0L;

    @Column(name = "art_cuenta")
    private BigDecimal numeroCuenta;

    @Builder.Default
    @Column(name = "art_tipo")
    private String tipo = "";

    @Builder.Default
    @Column(name = "art_directo")
    private Byte directo = 0;

    @Builder.Default
    @Column(name = "art_habilitado")
    private Byte habilitado = 0;

    @OneToOne(optional = true)
    @JoinColumn(name = "art_cuenta", insertable = false, updatable = false)
    private CuentaEntity cuenta;

}
