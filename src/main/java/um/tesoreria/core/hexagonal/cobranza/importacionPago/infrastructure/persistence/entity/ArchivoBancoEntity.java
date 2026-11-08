package um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "archivo_banco")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArchivoBancoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ArB_ID")
    private Long id;

    @Column(name = "ArB_Codigo_Empresa")
    private String codigoEmpresa;

    @Column(name = "ArB_Identificacion")
    private Integer identificacion;

    @Column(name = "ArB_Pro_ID")
    private Integer productoId;

    @Column(name = "ArB_Mes_Pago")
    private Integer mesPago;

    @Column(name = "ArB_Anio_Pago")
    private Integer anhoPago;

    @Column(name = "ArB_Cuo_ID")
    private Integer cuotaId;

    @Column(name = "ArB_Importe")
    private BigDecimal importe;

    @Column(name = "ArB_Fecha_Pago")
    private OffsetDateTime fechaPago;

    @Column(name = "ArB_Marca_Acred")
    private String marcaAcreditacion;

    @Column(name = "ArB_Fecha_Acred")
    private OffsetDateTime fechaAcreditacion;

    @Column(name = "ArB_Verificador")
    private String verificador;

    @Column(name = "ArB_Path")
    private String path;

    @Column(name = "ArB_Archivo")
    private String archivo;

    @Column(name = "arb_cuando", insertable = false, updatable = false)
    private OffsetDateTime cuando;

    @Column(name = "ArB_Pasado")
    private Byte pasado;

    @Column(name = "ArB_TPa_ID")
    private Integer tipoPagoId;

    @Column(name = "ArB_Repetido")
    private Byte repetido;

}
