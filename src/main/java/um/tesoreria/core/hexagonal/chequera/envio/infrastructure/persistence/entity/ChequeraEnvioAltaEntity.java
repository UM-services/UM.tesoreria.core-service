package um.tesoreria.core.hexagonal.chequera.envio.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.infrastructure.persistence.entity.ArancelTipoEntity;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.infrastructure.persistence.entity.TipoChequeraEntity;
import um.tesoreria.core.hexagonal.personas.domicilio.infrastructure.persistence.entity.DomicilioEntity;
import um.tesoreria.core.hexagonal.personas.persona.infrastructure.persistence.entity.PersonaEntity;

import java.math.BigDecimal;

/**
 * Vista legacy {@code vw_chequera_alta}: series sin baja y con clase de chequera 2 o 5 (la
 * definición de «alta» del legacy vive en la vista, no acá). Es de sólo lectura.
 *
 * <p>Las asociaciones son de lectura y replican los joins que hacía el adaptador JDBC, con el mismo
 * patrón que {@code ChequeraSerieEntity}: {@code persona} y {@code tipoChequera} obligatorios,
 * {@code domicilio} y {@code arancelTipo} opcionales.</p>
 */
@Getter
@Setter
@Entity
@Immutable
@Table(name = "vw_chequera_alta")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChequeraEnvioAltaEntity {

    @Id
    @Column(name = "clave")
    private Long chequeraId;

    @Column(name = "ChS_Fac_ID")
    private Integer facultadId;

    @Column(name = "ChS_TCh_ID")
    private Integer tipoChequeraId;

    @Column(name = "ChS_ID")
    private Long chequeraSerieId;

    @Column(name = "ChS_Alt_ID")
    private Integer alternativaId;

    @Column(name = "ChS_Per_ID")
    private BigDecimal personaId;

    @Column(name = "ChS_Doc_ID")
    private Integer documentoId;

    @Column(name = "ChS_Lec_ID")
    private Integer lectivoId;

    @Column(name = "ChS_Geo_ID")
    private Integer geograficaId;

    @Column(name = "ChS_ArT_ID")
    private Integer arancelTipoId;

    @Column(name = "enviado")
    private Byte enviado;

    @OneToOne(optional = false)
    @JoinColumn(name = "ChS_TCh_ID", referencedColumnName = "tch_id", insertable = false, updatable = false)
    private TipoChequeraEntity tipoChequera;

    @OneToOne(optional = false)
    @JoinColumns({
            @JoinColumn(name = "ChS_Per_ID", referencedColumnName = "per_id", insertable = false, updatable = false),
            @JoinColumn(name = "ChS_Doc_ID", referencedColumnName = "per_doc_id", insertable = false, updatable = false)})
    private PersonaEntity persona;

    @OneToOne(optional = true)
    @JoinColumns({
            @JoinColumn(name = "ChS_Per_ID", referencedColumnName = "dom_per_id", insertable = false, updatable = false),
            @JoinColumn(name = "ChS_Doc_ID", referencedColumnName = "dom_doc_id", insertable = false, updatable = false)})
    private DomicilioEntity domicilio;

    @OneToOne(optional = true)
    @JoinColumn(name = "ChS_ArT_ID", referencedColumnName = "art_id", insertable = false, updatable = false)
    private ArancelTipoEntity arancelTipo;

}
