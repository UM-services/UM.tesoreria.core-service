/**
 * 
 */
package ar.edu.um.tesoreria.rest.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;

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
@Table(uniqueConstraints = {
		@UniqueConstraint(columnNames = { "car_fac_id", "car_pla_id", "car_id" }) })
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Carrera extends Auditable implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5296330648103423266L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "clave")
	private Long uniqueId;

	@Column(name = "car_fac_id")
	private Integer facultadId;

	@Column(name = "car_pla_id")
	private Integer planId;

	@Column(name = "car_id")
	private Integer carreraId;

	@Column(name = "car_nombre")
	private String nombre = "";

	@Column(name = "car_iniciales")
	private String iniciales = "";

	@Column(name = "car_titulo")
	private String titulo = "";

	@Column(name = "trabajo_final")
	@NotNull
	private Byte trabajofinal = 0;
	
	private String resolucion = "";
	
	@Column(name = "car_chequnica")
	private Byte chequeraunica = 0;
	
	private Integer bloqueId;
	private Integer obligatorias = 0;
	private Integer optativas = 0;
	private Byte vigente = 0;
	
	public String getCarreraKey() {
		return this.facultadId + "." + this.planId + "." + this.carreraId;
	}

}
