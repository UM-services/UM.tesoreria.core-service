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
import jakarta.persistence.UniqueConstraint;

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
@Table(name = "vw_chequera_cuota_persona", uniqueConstraints = { @UniqueConstraint(columnNames = { "chc_fac_id",
		"chc_tch_id", "chc_chs_id", "chc_pro_id", "chc_alt_id", "chc_cuo_id" }) })
@Immutable
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ChequeraCuotaPersona extends Auditable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7322874494834259825L;

	@Id
	@Column(name = "chc_id")
	private Long chequeraCuotaId;

	@Column(name = "persona_id")
	private BigDecimal personaId;

	@Column(name = "documento_id")
	private Integer documentoId;

	@Column(name = "chc_fac_id")
	private Integer facultadId;

	@Column(name = "chc_tch_id")
	private Integer tipoChequeraId;

	@Column(name = "chc_chs_id")
	private Long chequeraSerieId;

	@Column(name = "chc_pro_id")
	private Integer productoId;

	@Column(name = "chc_alt_id")
	private Integer alternativaId;

	@Column(name = "chc_cuo_id")
	private Integer cuotaId;

	@Column(name = "chc_mes")
	private Integer mes = 0;

	@Column(name = "chc_anio")
	private Integer anho = 0;

	@Column(name = "chc_1er_vencimiento")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime vencimiento1;

	@Column(name = "chc_1er_importe")
	private BigDecimal importe1 = BigDecimal.ZERO;

	@Column(name = "importe1_original")
	private BigDecimal importe1Original = BigDecimal.ZERO;

	@Column(name = "chc_2do_vencimiento")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime vencimiento2;

	@Column(name = "chc_2do_importe")
	private BigDecimal importe2 = BigDecimal.ZERO;

	@Column(name = "importe2_original")
	private BigDecimal importe2Original;

	@Column(name = "chc_3er_vencimiento")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime vencimiento3;

	@Column(name = "chc_3er_importe")
	private BigDecimal importe3 = BigDecimal.ZERO;

	@Column(name = "importe3_original")
	private BigDecimal importe3Original = BigDecimal.ZERO;

	@Column(name = "chc_codigo_barras")
	private String codigoBarras = "";

	@Column(name = "chc_i2of5")
	private String i2Of5 = "";

	@Column(name = "chc_pagado")
	private Byte pagado = 0;

	@Column(name = "chc_baja")
	private Byte baja = 0;

	@Column(name = "chc_manual")
	private Byte manual = 0;

	@Column(name = "chc_tra_id")
	private Integer tramoId = 0;

}
