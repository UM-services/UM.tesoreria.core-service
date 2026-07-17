package um.tesoreria.core.hexagonal.track.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import um.tesoreria.core.model.Auditable;
import um.tesoreria.core.util.Jsonifyable;

@Getter
@Setter
@Entity
@Table(name = "track")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackEntity extends Auditable implements Jsonifyable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long trackId;

    @Builder.Default
    private String descripcion = "";

}
