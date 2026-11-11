package um.tesoreria.core.hexagonal.chequera.baja.infrastructure.persistence.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Fila de la tabla {@code seguro} que se resetea al anular una baja.
 */
@Getter
@Setter
@Entity
@Table(name = "seguro")
@IdClass(SeguroId.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeguroEntity {

    @Id
    @Column(name = "Seg_Doc_ID")
    private Integer documentoId;

    @Id
    @Column(name = "Seg_Per_ID")
    private BigDecimal personaId;

    @Id
    @Column(name = "Seg_Lec_ID")
    private Integer lectivoId;

    @Column(name = "Seg_Geo_ID")
    private Integer geograficaId;

    @Column(name = "Seg_Fac_ID")
    private Integer facultadId;

    @Column(name = "Seg_FechaGen")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
    private OffsetDateTime fechaGen;

    @Column(name = "Seg_Baja")
    private Byte baja;

}
