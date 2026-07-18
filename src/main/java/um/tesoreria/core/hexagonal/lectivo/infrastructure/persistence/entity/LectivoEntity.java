package um.tesoreria.core.hexagonal.lectivo.infrastructure.persistence.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import um.tesoreria.core.model.Auditable;
import um.tesoreria.core.util.Jsonifier;
import um.tesoreria.core.util.Jsonifyable;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "lectivo")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LectivoEntity extends Auditable implements Jsonifyable {

    @Id
    @Column(name = "lec_id")
    private Integer lectivoId;

    @Builder.Default
    @Column(name = "lec_nombre")
    private String nombre = "";

    @Column(name = "lec_inicio")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
    private OffsetDateTime fechaInicio;

    @Column(name = "lec_fin")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
    private OffsetDateTime fechaFinal;

}
