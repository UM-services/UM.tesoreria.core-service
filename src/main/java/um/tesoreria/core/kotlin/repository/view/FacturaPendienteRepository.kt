package um.tesoreria.core.kotlin.repository.view

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import um.tesoreria.core.kotlin.model.view.FacturaPendiente
import java.time.OffsetDateTime

@Repository
interface FacturaPendienteRepository : JpaRepository<FacturaPendiente, Long> {

    @Query(
        value = """
        SELECT DISTINCT m.mvp_id AS proveedor_movimiento_id,
            p.Prv_Razon AS razon_social,
            p.Prv_CUIT AS cuit,
            m.MvP_FechaComprob AS fecha_comprobante,
            t.TCo_Descripcion AS comprobante,
            t.Tco_Debita as debita,
            m.MvP_Prefijo AS prefijo,
            m.MvP_NroComprob AS numero_comprobante,
            m.MvP_Importe AS importe_factura,
            op.mvp_id AS orden_pago_id,
            op.MvP_FechaComprob AS fecha_orden_pago,
            op.MvP_Prefijo AS prefijo_orden_pago,
            op.MvP_NroComprob AS numero_orden_pago,
            op.MvP_Importe AS importe_orden_pago,
            MIN(v.Val_FechaEmision) AS fecha_minima_pago,
            MAX(v.Val_FechaEmision) AS fecha_maxima_pago,
            SUM(v.Val_Importe) AS importe_pagado
        FROM movprov m
        JOIN proveedores p ON m.MvP_Prv_ID = p.Prv_ID
        JOIN tiposcomprob t ON m.MvP_TCo_ID = t.TCo_ID
        LEFT JOIN movprov_detallecomprobantes dc ON dc.OpC_MvP_ID = m.MvP_ID
        LEFT JOIN movprov op ON dc.OpC_OrP_ID = op.MvP_ID
        LEFT JOIN movprov_detallevalores dv ON dv.OpV_OrP_ID = op.MvP_ID
        LEFT JOIN valores v ON dv.OpV_Val_ID = v.Val_ID
        AND v.Val_FechaEmision <= :fechaHasta
        AND v.Val_Estado = 'Emitido'
        LEFT JOIN valor l ON l.TiV_ID = v.Val_TiV_ID
        WHERE m.MvP_TCo_ID != 6
        AND m.MvP_Anulado IS NULL
        AND m.MvP_FechaComprob BETWEEN :fechaDesde AND :fechaHasta
        GROUP BY m.MvP_ID
        ORDER BY p.Prv_Razon, m.MvP_FechaComprob, m.MvP_ID
    """, nativeQuery = true)
    fun findFacturasPendientes(
        @Param("fechaDesde") fechaDesde: OffsetDateTime,
        @Param("fechaHasta") fechaHasta: OffsetDateTime
    ): List<FacturaPendiente>
}