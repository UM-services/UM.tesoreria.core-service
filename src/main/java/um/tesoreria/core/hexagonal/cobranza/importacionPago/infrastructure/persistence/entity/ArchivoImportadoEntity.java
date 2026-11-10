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

import java.time.OffsetDateTime;

@Entity
@Table(name = "archivo_importado")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArchivoImportadoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AIm_ID")
    private Integer id;

    @Column(name = "AIm_Nombre", nullable = false, unique = true)
    private String nombre;

    @Column(name = "AIm_Fecha")
    private OffsetDateTime fecha;

    @Column(name = "AIm_Registros")
    private Integer registros;

}
