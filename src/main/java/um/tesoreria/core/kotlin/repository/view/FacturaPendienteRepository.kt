package um.tesoreria.core.kotlin.repository.view

import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import um.tesoreria.core.kotlin.model.view.FacturaPendiente
import java.time.OffsetDateTime

@Repository
interface FacturaPendienteRepository : JpaRepository<FacturaPendiente, Long> {

    @Modifying
    @Transactional
    @Query(
        value = """
        UPDATE movprov_detallecomprobantes d
        JOIN movprov_detallevalores v ON v.OpV_OrP_ID = d.OpC_OrP_ID
        JOIN valores p ON v.OpV_Val_ID = p.val_id
        SET d.fecha_pago = p.Val_FechaEmision
        """, nativeQuery = true
    )
    fun updateFechaPagoInProveedorPago();

    @Query(
        value = """
SELECT DISTINCT m.mvp_id AS proveedor_movimiento_id, p.`Prv_Razon` AS razon_social, p.`Prv_CUIT` AS cuit, m.`MvP_FechaComprob` AS fecha_comprobante
, t.`TCo_Descripcion` AS comprobante, t.`TCo_Debita` AS debita, m.`MvP_Prefijo` AS prefijo, m.`MvP_NroComprob` AS numero_comprobante
, m.`MvP_Importe` AS importe_factura, SUM(d.`OpC_Importe`) AS importe_pagado
FROM movprov m
JOIN proveedores p
ON m.`MvP_Prv_ID` = p.`Prv_ID`
JOIN tiposcomprob t
ON m.`MvP_TCo_ID` = t.`TCo_ID`
LEFT JOIN `movprov_detallecomprobantes` d
ON m.`MvP_ID` = d.`OpC_MvP_ID`
AND d.`fecha_pago` <= :fechaHasta
WHERE m.`MvP_TCo_ID` != 6
AND m.`MvP_FechaComprob` BETWEEN :fechaDesde AND :fechaHasta
GROUP BY m.mvp_id
ORDER BY p.prv_id, m.`MvP_FechaComprob`    """, nativeQuery = true)
    fun findFacturasPendientes(
        @Param("fechaDesde") fechaDesde: OffsetDateTime,
        @Param("fechaHasta") fechaHasta: OffsetDateTime
    ): List<FacturaPendiente>
}