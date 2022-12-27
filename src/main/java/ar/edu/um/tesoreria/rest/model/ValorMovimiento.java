/**
 * 
 */
package ar.edu.um.tesoreria.rest.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author daniel
 *
 */
@Data
@Entity
@Table(name = "valores")
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ValorMovimiento extends Auditable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -639908708040229983L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "val_id")
	private Long valorMovimientoId;

	@Column(name = "val_tiv_id")
	private Integer valorId;

	@Column(name = "val_fechaemision")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime fechaEmision;

	@Column(name = "val_numero")
	private Long numero = 0L;

	@Column(name = "val_cuentaorigen")
	private Long cuentaOrigen;

	@Column(name = "val_fechaefectivizacion")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime fechaEfectivizacion;

	@Column(name = "val_importe")
	private BigDecimal importe = BigDecimal.ZERO;

	@Column(name = "val_estado")
	private String estado;

	@Column(name = "val_nombretitular")
	private String nombreTitular = "";

	@Column(name = "val_cuittitular")
	private String cuitTitular = "";

	@Column(name = "val_reemplazo")
	private Long reemplazo = 0L;

	@Column(name = "val_fechaasiento")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime fechaContable;

	@Column(name = "val_numeroasiento")
	private Integer ordenContable;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime fechaContableAnulacion;

	private Integer ordenContableAnulacion;

	@Column(name = "val_letras")
	private String letras = "";

}
