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

@Entity
@Table(name = "chequera_total_reemplazo")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReemplazoTotalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CTR_ID")
    private Long id;

    @Column(name = "CTR_Fac_ID")
    private Integer facultadId;

    @Column(name = "CTR_TCh_ID")
    private Integer tipoChequeraId;

    @Column(name = "CTR_ChS_ID")
    private Long chequeraSerieId;

    @Column(name = "CTR_Pro_ID")
    private Integer productoId;

    @Column(name = "CTR_Total")
    private BigDecimal total;

    @Column(name = "CTR_Pagado")
    private BigDecimal pagado;

}
