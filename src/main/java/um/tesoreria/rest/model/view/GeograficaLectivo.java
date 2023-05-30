/**
 * 
 */
package um.tesoreria.rest.model.view;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.Immutable;

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
@Table(name = "vw_geografica_lectivo")
@Immutable
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class GeograficaLectivo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4010543774645507346L;

	@Id
	private String unified;

	@Column(name = "geo_id")
	private Integer geograficaId;

	@Column(name = "geo_nombre")
	private String nombre = "";

	@Column(name = "geo_sinchequera")
	private Byte sinchequera = 0;

	private Integer lectivoId;
	
}
