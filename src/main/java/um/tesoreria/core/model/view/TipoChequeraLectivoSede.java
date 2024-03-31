/**
 * 
 */
package um.tesoreria.core.model.view;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import org.hibernate.annotations.Immutable;

import um.tesoreria.core.model.view.pk.TipoChequeraLectivoSedePk;
import lombok.Data;

/**
 * @author daniel
 *
 */
@Data
@Entity
@Immutable
@Table(name = "vw_tipochequera_lectivo_sede")
@IdClass(value = TipoChequeraLectivoSedePk.class)
public class TipoChequeraLectivoSede implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8111647200829890278L;

	@Id
	@Column(name = "tch_id")
	private Integer tipoChequeraId;
	
	@Id
	private Integer facultadId;

	@Id
	private Integer lectivoId;

	@Id
	@Column(name = "tch_geo_id")
	private Integer geograficaId;

	@Column(name = "tch_nombre")
	private String nombre;

	@Column(name = "tch_prefijo")
	private String prefijo;

	@Column(name = "tch_cch_id")
	private Integer claseChequeraId;

	@Column(name = "tch_imprimir")
	private Byte imprimir;

	@Column(name = "tch_contado")
	private Byte contado;

	private String sede;
	
}
