/**
 * 
 */
package um.tesoreria.core.model.view;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.Immutable;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import um.tesoreria.core.kotlin.model.Auditable;

/**
 * @author daniel
 *
 */
@Data
@Entity
@Table(name = "vw_domicilio_key")
@Immutable
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class DomicilioKey extends Auditable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6464259836244238332L;

	@Id
	private String unified;

	@Column(name = "dom_id")
	private Long domicilioId;

	@Column(name = "dom_per_id")
	private BigDecimal personaId;

	@Column(name = "dom_doc_id")
	private Integer documentoId;

	@Column(name = "dom_fecha")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime fecha = OffsetDateTime.now();

	@Column(name = "dom_calle")
	private String calle;

	@Column(name = "dom_puerta")
	private String puerta;

	@Column(name = "dom_piso")
	private String piso;

	@Column(name = "dom_dpto")
	private String dpto;

	@Column(name = "dom_telefono")
	private String telefono;

	@Column(name = "dom_movil")
	private String movil;

	@Column(name = "dom_observ")
	private String observaciones;

	@Column(name = "dom_codpostal")
	private String codigoPostal;

	@Column(name = "dom_fac_id")
	private Integer facultadId;

	@Column(name = "dom_prv_id")
	private Integer provinciaId;

	@Column(name = "dom_loc_id")
	private Integer localidadId;

	@Column(name = "dom_e_mail")
	private String emailPersonal;

	@Column(name = "mail_institucional")
	private String emailInstitucional;

	@Column(name = "dom_laboral")
	private String laboral;

}
