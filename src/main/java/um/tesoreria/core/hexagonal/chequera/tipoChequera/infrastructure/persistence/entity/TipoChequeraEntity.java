package um.tesoreria.core.hexagonal.chequera.tipoChequera.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import um.tesoreria.core.hexagonal.geografica.infrastructure.persistence.entity.GeograficaEntity;
import um.tesoreria.core.hexagonal.chequera.claseChequera.infrastructure.persistence.entity.ClaseChequeraEntity;
import um.tesoreria.core.model.Auditable;
import um.tesoreria.core.util.Jsonifier;

@Getter
@Setter
@Entity
@Table(name = "tipo_chequera")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoChequeraEntity extends Auditable {

    @Id
    @Column(name = "tch_id")
    private Integer tipoChequeraId;

    @Builder.Default
    @Column(name = "tch_nombre")
    private String nombre = "";

    @Builder.Default
    @Column(name = "tch_prefijo")
    private String prefijo = "";

    @Builder.Default
    @Column(name = "tch_geo_id")
    private Integer geograficaId = 1;

    @Builder.Default
    @Column(name = "tch_cch_id")
    private Integer claseChequeraId = 2;

    @Builder.Default
    @Column(name = "tch_imprimir")
    private Byte imprimir = 0;

    @Builder.Default
    @Column(name = "tch_contado")
    private Byte contado = 0;

    @Builder.Default
    @Column(name = "multiple")
    private Byte multiple = 0;

    private String emailCopia;

    @OneToOne(optional = true)
    @JoinColumn(name = "tch_geo_id", insertable = false, updatable = false)
    private GeograficaEntity geografica;

    @OneToOne(optional = true)
    @JoinColumn(name = "tch_cch_id", insertable = false, updatable = false)
    private ClaseChequeraEntity claseChequera;

    public String jsonify() {
        return Jsonifier.builder(this).build();
    }

}
