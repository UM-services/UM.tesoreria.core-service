package um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.domain.model.ArancelTipo;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera;
import um.tesoreria.core.hexagonal.domicilio.domain.model.Domicilio;
import um.tesoreria.core.hexagonal.facultad.domain.model.Facultad;
import um.tesoreria.core.hexagonal.geografica.domain.model.Geografica;
import um.tesoreria.core.hexagonal.lectivo.domain.model.Lectivo;
import um.tesoreria.core.hexagonal.persona.domain.model.Persona;
import um.tesoreria.core.util.Jsonifier;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChequeraSerie {

    private Long chequeraId;
    private Integer facultadId;
    private Integer tipoChequeraId;
    private Long chequeraSerieId;
    private BigDecimal personaId;
    private Integer documentoId;
    private Integer lectivoId;
    private Integer arancelTipoId;
    private Integer cursoId;
    private Byte asentado;
    private Integer geograficaId;
    private OffsetDateTime fecha;
    private Integer cuotasPagadas;
    private String observaciones;
    private Integer alternativaId;
    private Byte algoPagado;
    private Integer tipoImpresionId;
    private Byte flagPayperTic;
    private String usuarioId;
    private Byte enviado;
    private Byte retenida;
    private Long version;
    private Byte hpum;
    private BigDecimal becaPorcentaje;
    private String becaResolucion;
    private OffsetDateTime becaFecha;
    private Long becaUserId;

    private ArancelTipo arancelTipo;
    private Domicilio domicilio;
    private Facultad facultad;
    private Geografica geografica;
    private Lectivo lectivo;
    private Persona persona;
    private TipoChequera tipoChequera;

    // Transient fields
    private int cuotasDeuda;
    private BigDecimal importeDeuda;
    private OffsetDateTime ultimoEnvio;

    public String getPersonaKey() {
        return this.personaId.toString() + "." + this.documentoId;
    }

    public String getFacultadKey() {
        return this.facultadId.toString() + "." + this.lectivoId + "." + this.geograficaId + "." + this.getPersonaKey();
    }

    public String jsonify() {
        return Jsonifier.builder(this).build();
    }
}
