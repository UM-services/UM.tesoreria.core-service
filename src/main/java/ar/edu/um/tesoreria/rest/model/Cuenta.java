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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
@Table(name = "plancta")
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Cuenta extends Auditable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3113447418041300185L;

	@Id
	@Column(name = "pla_cuenta")
	private BigDecimal numeroCuenta;

	@Column(name = "pla_nombre")
	private String nombre;

	@Column(name = "pla_integra")
	private Byte integradora = 0;

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

	private Integer geograficaId;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime fechaBloqueo;

	private Byte visible = 0;

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long cuentaContableId;

	@OneToOne(optional = true)
	@JoinColumn(name = "geograficaId", insertable = false, updatable = false)
	private Geografica geografica;

}
