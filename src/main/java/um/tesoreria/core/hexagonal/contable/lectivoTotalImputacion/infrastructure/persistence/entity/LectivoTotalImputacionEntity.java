package um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.persistence.entity.CuentaEntity;
import um.tesoreria.core.hexagonal.facultad.infrastructure.persistence.entity.FacultadEntity;
import um.tesoreria.core.hexagonal.lectivo.infrastructure.persistence.entity.LectivoEntity;
import um.tesoreria.core.hexagonal.chequera.producto.infrastructure.persistence.entity.ProductoEntity;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.infrastructure.persistence.entity.TipoChequeraEntity;
import um.tesoreria.core.model.Auditable;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "lectivo_total_imputacion", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"facultadId", "lectivoId", "tipoChequeraId", "productoId"})})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LectivoTotalImputacionEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lectivoTotalImputacionId;

    private Integer facultadId;
    private Integer lectivoId;
    private Integer tipoChequeraId;
    private Integer productoId;

	@Column(name = "cuenta")
    private BigDecimal numeroCuenta;

	@OneToOne(optional = true)
	@JoinColumn(name = "facultadId", insertable = false, updatable = false)
	private FacultadEntity facultad;

	@OneToOne(optional = true)
	@JoinColumn(name = "lectivoId", insertable = false, updatable = false)
	private LectivoEntity lectivo;

	@OneToOne(optional = true)
	@JoinColumn(name = "tipoChequeraId", insertable = false, updatable = false)
	private TipoChequeraEntity tipoChequera;

	@OneToOne(optional = true)
	@JoinColumn(name = "productoId", insertable = false, updatable = false)
	private ProductoEntity producto;

	@OneToOne(optional = true)
	@JoinColumn(name = "cuenta", insertable = false, updatable = false)
	private CuentaEntity cuenta;

}
