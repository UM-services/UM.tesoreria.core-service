package um.tesoreria.core.hexagonal.usuario.infrastructure.persistence.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import um.tesoreria.core.model.Auditable;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "usuario")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true)
    private String login;

    private String password;

    private String nombre;

    private Integer geograficaId;

    @Builder.Default
    private Byte imprimeChequera = 0;

    @Builder.Default
    private Byte numeroOpManual = 0;

    @Builder.Default
    private Byte habilitaOpEliminacion = 0;

    @Builder.Default
    private Byte eliminaChequera = 0;

    @Builder.Default
    private Byte modificaChequera = 0;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
    private OffsetDateTime lastLog;

    private String googleMail;

    @Builder.Default
    private Byte activo = 1;

}
