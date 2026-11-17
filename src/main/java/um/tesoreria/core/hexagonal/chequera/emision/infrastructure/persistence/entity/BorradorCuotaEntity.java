package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import um.tesoreria.core.model.Auditable;
import um.tesoreria.core.util.Jsonifyable;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "chequera_borrador_cuota",
        uniqueConstraints = @UniqueConstraint(columnNames = {
                "chbc_borrador_id", "chbc_producto_id", "chbc_alternativa_id", "chbc_cuota_id"}))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BorradorCuotaEntity extends Auditable implements Jsonifyable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chbc_id")
    private Long borradorCuotaId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chbc_borrador_id", nullable = false)
    private BorradorChequeraEntity borrador;

    @Column(name = "chbc_chequera_cuota_id")
    private Long chequeraCuotaId;

    @Column(name = "chbc_producto_id")
    private Integer productoId;

    @Column(name = "chbc_alternativa_id")
    private Integer alternativaId;

    @Column(name = "chbc_cuota_id")
    private Integer cuotaId;

    @Column(name = "chbc_mes")
    private Integer mes;

    @Column(name = "chbc_anio")
    private Integer anio;

    @Column(name = "chbc_1er_vencimiento")
    private OffsetDateTime vencimiento1;

    @Column(name = "chbc_2do_vencimiento")
    private OffsetDateTime vencimiento2;

    @Column(name = "chbc_3er_vencimiento")
    private OffsetDateTime vencimiento3;

    @Column(name = "chbc_1er_importe_original")
    private BigDecimal importe1Original;

    @Column(name = "chbc_2do_importe_original")
    private BigDecimal importe2Original;

    @Column(name = "chbc_3er_importe_original")
    private BigDecimal importe3Original;

    @Column(name = "chbc_1er_importe_borrador")
    private BigDecimal importe1Borrador;

    @Column(name = "chbc_2do_importe_borrador")
    private BigDecimal importe2Borrador;

    @Column(name = "chbc_3er_importe_borrador")
    private BigDecimal importe3Borrador;

    @Column(name = "chbc_arancel_tipo_id")
    private Integer arancelTipoId;

    @Column(name = "chbc_tramo_id")
    private Integer tramoId;

    @Column(name = "chbc_descuento_pt")
    private BigDecimal descuentoPagoTermino;

    @Column(name = "chbc_pagado")
    private Byte pagado;

    @Column(name = "chbc_baja")
    private Byte baja;

    @Column(name = "chbc_manual")
    private Byte manual;

    @Column(name = "chbc_compensada")
    private Byte compensada;

    @Column(name = "chbc_modificado")
    private Byte modificado;

    @Column(name = "chbc_bloqueado")
    private Byte bloqueado;
}
