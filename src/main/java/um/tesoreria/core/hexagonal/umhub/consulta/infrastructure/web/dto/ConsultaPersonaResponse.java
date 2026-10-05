package um.tesoreria.core.hexagonal.umhub.consulta.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaPersonaResponse {
    private String numeroDocumento;
    private String nombre;
    private String apellido;
    private String sexo;
    private String numeroPrefijo;
    private String numeroPosfijo;
    private List<TipoDocumentoResponse> documentos;
    private DomicilioResponse domicilio;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TipoDocumentoResponse {
        private Integer documentoId;
        private String nombre;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DomicilioResponse {
        private String calle;
        private String puerta;
        private String piso;
        private String dpto;
        private String codigoPostal;
        private Integer provinciaId;
        private String provinciaNombre;
        private Integer localidadId;
        private String localidadNombre;
        private String telefono;
        private String movil;
        private String emailPersonal;
        private String emailInstitucional;
    }
}
