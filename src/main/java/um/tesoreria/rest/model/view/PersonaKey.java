/**
 * 
 */
package um.tesoreria.rest.model.view;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.hibernate.annotations.Immutable;

import um.tesoreria.rest.model.Auditable;
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
@Table(name = "vw_persona_key")
@Immutable
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class PersonaKey extends Auditable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1113380310482594229L;

	@Id
	private String unified = null;

	@Column(name = "clave")
	private Long uniqueId = null;

	@Column(name = "per_id")
	private BigDecimal personaId = null;

	@Column(name = "per_doc_id")
	private Integer documentoId = null;

	@Column(name = "Per_Apellido")
	private String apellido = "";

	@Column(name = "Per_Nombre")
	private String nombre = "";

	@Column(name = "per_sexo")
	private String sexo = "";

	@Column(name = "per_primero")
	private Byte primero = 0;

	@Column(name = "per_cuit")
	private String cuit = "";

	@Column(name = "per_cbu")
	private String cbu = "";

	@Column(name = "per_contrasenha")
	private String password = "";

	private String search = "";

	@Transient
	private Boolean mark_facultad = false;

	public String getApellidoNombre() {
		return this.apellido + ", " + this.nombre;
	}

}
