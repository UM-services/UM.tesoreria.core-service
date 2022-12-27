/**
 * 
 */
package ar.edu.um.tesoreria.rest.model;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author daniel
 *
 */
@Entity
@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Dependencia extends Auditable implements Serializable {
	
	private static final long serialVersionUID = -7619347846778510021L;

	@Id
	private Integer dependenciaId;

	private String nombre = "";
	private String acronimo = "";
	private Integer facultadId;
	private Integer geograficaId;
	
	@OneToOne(optional = false)
	@JoinColumn(name = "facultadId", insertable = false, updatable = false)
	private Facultad facultad;

	@OneToOne(optional = false)
	@JoinColumn(name = "geograficaId", insertable = false, updatable = false)
	private Geografica geografica;

	public String getSedeKey() {
		return this.facultadId + "." + this.geograficaId;
	}

}
