/**
 * 
 */
package ar.edu.um.tesoreria.rest.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

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
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "chc_fac_id", "chc_tch_id",
		"chc_chs_id", "chc_pro_id", "chc_alt_id", "chc_cuo_id" }) })
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ChequeraCuota extends Auditable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7500885090012899044L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "chc_id")
	private Long chequeraCuotaId;

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

	private Integer arancelTipoId;

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
	private BigDecimal importe2Original = BigDecimal.ZERO;

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
	
	private Byte compensada = 0;

	@Column(name = "chc_tra_id")
	private Integer tramoId = 0;
	
	@OneToOne
	@JoinColumn(name = "chc_pro_id", insertable = false, updatable = false)
	private Producto producto;

	public String cuotaKey() {
		return this.facultadId + "." + this.tipoChequeraId + "." + this.chequeraSerieId + "." + this.productoId + "."
				+ this.alternativaId + "." + this.cuotaId;
	}

}
