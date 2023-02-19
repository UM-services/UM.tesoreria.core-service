/**
 * 
 */
package ar.edu.um.tesoreria.rest.model;

import java.io.Serializable;
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
@Table
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Entrega extends Auditable implements Serializable {

	private static final long serialVersionUID = 1630377022408652492L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "noe_id")
	private Long entregaId = null;

	@Column(name = "noe_fecha")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime fecha = null;

	@Column(name = "noe_ubi_id")
	private Integer ubicacionId = null;

	@Column(name = "noe_recibe")
	private String recibe = "";

	@Column(name = "noe_observacion")
	private String observacion = "";

	@Column(name = "noe_mco_fecha")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime fechaContable = null;

	@Column(name = "noe_mco_nrocomp")
	private Integer ordenContable = 0;

	@Column(name = "noe_anulada")
	private Byte anulada = 0;

	@Column(name = "noe_tipo")
	private String tipo = "";

	@OneToOne(optional = true)
	@JoinColumn(name = "noe_ubi_id", insertable = false, updatable = false)
	private Ubicacion ubicacion = null;

}
