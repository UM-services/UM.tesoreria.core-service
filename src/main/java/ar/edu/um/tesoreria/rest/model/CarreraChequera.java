/**
 * 
 */
package ar.edu.um.tesoreria.rest.model;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

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
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "lectivoId", "facultadId", "planId", "carreraId",
		"claseChequeraId", "curso", "geograficaId" }) })
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class CarreraChequera extends Auditable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2815851181961541861L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long carreraChequeraId;

	private Integer facultadId;
	private Integer lectivoId;
	private Integer planId;
	private Integer carreraId;
	private Integer claseChequeraId;
	private Integer curso = 0;
	private Integer geograficaId;
	private Integer tipoChequeraId;

	public String getCarreraKey() {
		return this.facultadId + "." + this.planId + "." + this.carreraId;
	}

}
