package um.tesoreria.core.hexagonal.asiento.infrastructure.persistence.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import um.tesoreria.core.kotlin.model.Track;
import um.tesoreria.core.model.Auditable;
import um.tesoreria.core.util.Jsonifier;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "asiento",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"fecha", "orden"}),
                @UniqueConstraint(columnNames = {"fechaContra", "ordenContra"})
        }
)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsientoEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long asientoId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
    private OffsetDateTime fecha;

    @Builder.Default
    private Integer orden = 0;

    @Builder.Default
    private String vinculo = "";

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
    private OffsetDateTime fechaContra;

    private Integer ordenContra;

    private Long trackId;

    @OneToOne(optional = true)
    @JoinColumn(name = "trackId", insertable = false, updatable = false)
    private Track track;

    public String jsonify() {
        return Jsonifier.builder(this).build();
    }

}
