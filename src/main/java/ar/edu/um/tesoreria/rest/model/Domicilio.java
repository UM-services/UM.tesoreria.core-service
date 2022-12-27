package ar.edu.um.tesoreria.rest.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "dom_per_id", "dom_doc_id" }) })
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Domicilio extends Auditable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8282957336867319733L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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
	private String calle = "";

	@Column(name = "dom_puerta")
	private String puerta = "";

	@Column(name = "dom_piso")
	private String piso = "";

	@Column(name = "dom_dpto")
	private String dpto = "";

	@Column(name = "dom_telefono")
	private String telefono = "";

	@Column(name = "dom_movil")
	private String movil = "";

	@Column(name = "dom_observ")
	private String observaciones = "";

	@Column(name = "dom_codpostal")
	private String codigoPostal = "";

	@Column(name = "dom_fac_id")
	private Integer facultadId;

	@Column(name = "dom_prv_id")
	private Integer provinciaId;

	@Column(name = "dom_loc_id")
	private Integer localidadId;

	@Column(name = "dom_e_mail")
	private String emailPersonal = "";

	@Column(name = "mail_institucional")
	private String emailInstitucional = "";

	@Column(name = "dom_laboral")
	private String laboral = "";

}
