package um.tesoreria.core.hexagonal.personas.persona.infrastructure.persistence.entity;

import java.math.BigDecimal;

import lombok.*;
import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import um.tesoreria.core.model.Auditable;
import um.tesoreria.core.util.Jsonifyable;

@Getter
@Setter
@Entity
@Immutable
@Table(name = "vw_persona_key")
@NoArgsConstructor
@AllArgsConstructor
public class PersonaKeyEntity extends Auditable implements Jsonifyable {

    @Id
    private String unified;

    @Column(name = "clave")
    private Long uniqueId;

    @Column(name = "per_id")
    private BigDecimal personaId;

    private String numeroPrefijo;
    private String numeroPosfijo;

    @Column(name = "per_doc_id")
    private Integer documentoId;

    @Column(name = "Per_Apellido")
    private String apellido = "";

    @Column(name = "Per_Nombre")
    private String nombre = "";

    @Column(name = "per_sexo")
    private String sexo = "";

    @Column(name = "per_primero")
    private Byte primero = 0;

    @Column(name = "per_cuit")
    private String cuit = "";

    @Column(name = "per_cbu")
    private String cbu = "";

    @Column(name = "per_contrasenha")
    private String password = "";

    private Byte hpum;
    private Long guaraniPersona;

    private String search = "";

    @Transient
    private Boolean mark_facultad = false;

}
