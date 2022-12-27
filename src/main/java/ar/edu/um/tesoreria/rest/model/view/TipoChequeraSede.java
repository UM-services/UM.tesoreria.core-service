/**
 * 
 */
package ar.edu.um.tesoreria.rest.model.view;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.Immutable;

import lombok.Data;

/**
 * @author daniel
 *
 */
@Data
@Entity
@Table(name = "vw_tipochequera_sede")
@Immutable
public class TipoChequeraSede implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -353181481094333221L;

	@Id
	private Integer tipoChequeraId;
	
	private String nombre;
	private Integer geograficaId;
	private String sede;
	private Integer claseChequeraId;

}
