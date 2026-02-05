/**
 * 
 */
package um.tesoreria.core.model;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import um.tesoreria.core.kotlin.model.Auditable;
import um.tesoreria.core.kotlin.model.Facultad;
import um.tesoreria.core.kotlin.model.Geografica;
import um.tesoreria.core.hexagonal.persona.infrastructure.persistence.entity.PersonaEntity;
import um.tesoreria.core.util.Jsonifier;

/**
 * @author daniel
 *
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Contrato extends Auditable implements Serializable {
	/**
	 * 
	 */
	@Serial
    private static final long serialVersionUID = -6511834189234951853L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long contratoId;

	@Column(name = "con_per_id")
	private BigDecimal personaId;

	@Column(name = "con_doc_id")
	private Integer documentoId;

	@Column(name = "con_desde")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime desde;

	@Column(name = "con_fac_id")
	private Integer facultadId;

	@Column(name = "con_pla_id")
	private Integer planId;

	@Column(name = "con_mat_id")
	private String materiaId;

	@Column(name = "con_geo_id")
	private Integer geograficaId;

	@Column(name = "con_cam_id")
	private Integer cargoMateriaId;

	@Column(name = "con_primervenc")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime primerVencimiento;

	@Column(name = "con_cargo")
	private String cargo = "";

	private BigDecimal montoFijo = BigDecimal.ZERO;

	@Column(name = "con_canonmensual")
	private BigDecimal canonMensual = BigDecimal.ZERO;

	@Column(name = "con_canonmensualsa")
	private BigDecimal canonMensualSinAjuste = BigDecimal.ZERO;

	@Column(name = "con_hasta")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime hasta;

	@Column(name = "con_canonmensualletras")
	private String canonMensualLetras = "";

	@Column(name = "con_canontotal")
	private BigDecimal canonTotal = BigDecimal.ZERO;

	@Column(name = "con_canontotalletras")
	private String canonTotalLetras = "";

	@Column(name = "con_meses")
	private Integer meses = 0;

	@Column(name = "con_mesesletras")
	private String mesesLetras = "";

	@Column(name = "con_ajuste")
	private Byte ajuste = 0;

    @OneToOne(optional = true)
	@JoinColumn(name = "con_fac_id", insertable = false, updatable = false)
	private Facultad facultad;

    @OneToOne(optional = true)
	@JoinColumn(name = "con_geo_id", insertable = false, updatable = false)
	private Geografica geografica;

    @OneToOne(optional = true)
    @JoinColumns({
            @JoinColumn(name = "con_per_id", referencedColumnName = "per_id", insertable = false, updatable = false),
            @JoinColumn(name = "con_doc_id", referencedColumnName = "per_doc_id", insertable = false, updatable = false)
    })
    private PersonaEntity personaEntity = null;

    public String jsonify() {
        return Jsonifier.builder(this).build();
    }

}
