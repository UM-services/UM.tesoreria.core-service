package um.tesoreria.core.hexagonal.persona.infrastructure.persistence.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.MessageFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.*;
import um.tesoreria.core.kotlin.model.Auditable;

@Getter
@Setter
@Entity
@Table(name = "persona", uniqueConstraints = { @UniqueConstraint(columnNames = { "per_id", "per_doc_id" }) })
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class PersonaEntity extends Auditable implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "clave")
    private Long uniqueId;

    @Column(name = "per_id")
    private BigDecimal personaId;

    @Column(name = "per_doc_id")
    private Integer documentoId;

    @Column(name = "Per_Apellido")
    private String apellido;

    @Column(name = "Per_Nombre")
    private String nombre;

    @Column(name = "per_sexo")
    private String sexo;

    @Column(name = "per_primero")
    private Byte primero = 0;

    @Column(name = "per_cuit")
    private String cuit = "";

    @Column(name = "per_cbu")
    private String cbu = "";

    @Column(name = "per_contrasenha")
    private String password;

    public String getApellidoNombre() {
        return MessageFormat.format("{0}, {1}", apellido, nombre);
    }
}
