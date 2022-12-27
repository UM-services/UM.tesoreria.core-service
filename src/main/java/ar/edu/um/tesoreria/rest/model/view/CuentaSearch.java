/**
 * 
 */
package ar.edu.um.tesoreria.rest.model.view;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.Immutable;

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
@Table(name = "vw_cuenta_search")
@Immutable
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class CuentaSearch implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -6874387076738500442L;

	@Id
	private BigDecimal numeroCuenta;

	private String nombre;
	private Byte integradora;
	private Integer grado;
	private BigDecimal grado1;
	private BigDecimal grado2;
	private BigDecimal grado3;
	private BigDecimal grado4;
	private Integer geograficaId;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime fechaBloqueo;
	
	private Long cuentaContableId;
	private Byte visible = 0;
	private String search;

}
