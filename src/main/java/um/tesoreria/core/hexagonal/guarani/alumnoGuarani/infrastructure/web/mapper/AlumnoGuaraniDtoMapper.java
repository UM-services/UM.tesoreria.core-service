package um.tesoreria.core.hexagonal.guarani.alumnoGuarani.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.AlumnoGuarani;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.ContactoGuarani;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.DocumentoPrincipalGuarani;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.PersonaGuarani;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.PropuestaGuarani;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.PropuestaTipoGuarani;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.TipoDocumentoGuarani;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.UbicacionGuarani;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AlumnoGuaraniDtoMapper {

    public AlumnoGuarani toDomain(um.tesoreria.core.hexagonal.guarani.alumnoGuarani.infrastructure.web.dto.AlumnoGuaraniRequest request) {
        if (request == null) {
            return null;
        }
        return AlumnoGuarani.builder()
                .alumnoId(request.getAlumnoId())
                .legajo(request.getLegajo())
                .persona(request.getPersona())
                .propuesta(request.getPropuesta())
                .planVersion(request.getPlanVersion())
                .ubicacion(request.getUbicacion())
                .modalidad(request.getModalidad())
                .division(request.getDivision())
                .anioCursada(request.getAnioCursada())
                .cantidadReadmisiones(request.getCantidadReadmisiones())
                .regular(request.getRegular())
                .calidad(request.getCalidad())
                .coeficiente(request.getCoeficiente())
                .personaRel(toPersonaGuarani(request.getPersonaRel()))
                .propuestaRel(toPropuestaGuarani(request.getPropuestaRel()))
                .ubicacionRel(toUbicacionGuarani(request.getUbicacionRel()))
                .build();
    }

    private PersonaGuarani toPersonaGuarani(um.tesoreria.core.hexagonal.guarani.alumnoGuarani.infrastructure.web.dto.PersonaGuarani dto) {
        if (dto == null) {
            return null;
        }
        return PersonaGuarani.builder()
                .personaId(dto.getPersonaId())
                .apellido(dto.getApellido())
                .nombres(dto.getNombres())
                .apellidoElegido(dto.getApellidoElegido())
                .nombresElegido(dto.getNombresElegido())
                .sexo(dto.getSexo())
                .identidadGenero(dto.getIdentidadGenero())
                .identidadGeneroOtro(dto.getIdentidadGeneroOtro())
                .fechaNacimiento(dto.getFechaNacimiento())
                .localidadNacimiento(dto.getLocalidadNacimiento())
                .nacionalidad(dto.getNacionalidad())
                .fechaIngresoPais(dto.getFechaIngresoPais())
                .paisOrigen(dto.getPaisOrigen())
                .documentoPrincipal(dto.getDocumentoPrincipal())
                .documentoPrincipalRel(toDocumentoPrincipalGuarani(dto.getDocumentoPrincipalRel()))
                .contactos(toContactoGuaraniList(dto.getContactos()))
                .usuario(dto.getUsuario())
                .clave(dto.getClave())
                .fechaVencimientoClave(dto.getFechaVencimientoClave())
                .autenticacion(dto.getAutenticacion())
                .bloqueado(dto.getBloqueado())
                .token(dto.getToken())
                .tokenAlta(dto.getTokenAlta())
                .emailTemporal(dto.getEmailTemporal())
                .emailValido(dto.getEmailValido())
                .idImagen(dto.getIdImagen())
                .tipoUsuarioInicial(dto.getTipoUsuarioInicial())
                .pertenecePuebloOriginario(dto.getPertenecePuebloOriginario())
                .puebloOriginario(dto.getPuebloOriginario())
                .puebloOriginarioOtro(dto.getPuebloOriginarioOtro())
                .araiIdentificadorSso(dto.getAraiIdentificadorSso())
                .araiUuid(dto.getAraiUuid())
                .build();
    }

    private DocumentoPrincipalGuarani toDocumentoPrincipalGuarani(um.tesoreria.core.hexagonal.guarani.alumnoGuarani.infrastructure.web.dto.DocumentoPrincipalGuarani dto) {
        if (dto == null) {
            return null;
        }
        return DocumentoPrincipalGuarani.builder()
                .documento(dto.getDocumento())
                .persona(dto.getPersona())
                .paisDocumento(dto.getPaisDocumento())
                .tipoDocumento(dto.getTipoDocumento())
                .tipoDocumentoRel(toTipoDocumentoGuarani(dto.getTipoDocumentoRel()))
                .nroDocumento(dto.getNroDocumento())
                .fechaOtorgamiento(dto.getFechaOtorgamiento())
                .fechaVencimiento(dto.getFechaVencimiento())
                .validadoConRenaper(dto.getValidadoConRenaper())
                .build();
    }

    private TipoDocumentoGuarani toTipoDocumentoGuarani(um.tesoreria.core.hexagonal.guarani.alumnoGuarani.infrastructure.web.dto.TipoDocumentoGuarani dto) {
        if (dto == null) {
            return null;
        }
        return TipoDocumentoGuarani.builder()
                .tipoDocumento(dto.getTipoDocumento())
                .descripcion(dto.getDescripcion())
                .descAbreviada(dto.getDescAbreviada())
                .ordenPrincipal(dto.getOrdenPrincipal())
                .habilitaInscripcion(dto.getHabilitaInscripcion())
                .tipoDeDato(dto.getTipoDeDato())
                .puedeEliminarse(dto.getPuedeEliminarse())
                .expRegularValidacion(dto.getExpRegularValidacion())
                .expRegularMensaje(dto.getExpRegularMensaje())
                .build();
    }

    private List<ContactoGuarani> toContactoGuaraniList(List<um.tesoreria.core.hexagonal.guarani.alumnoGuarani.infrastructure.web.dto.ContactoGuarani> dtoList) {
        if (dtoList == null) {
            return Collections.emptyList();
        }
        return dtoList.stream().map(this::toContactoGuarani).collect(Collectors.toList());
    }

    private ContactoGuarani toContactoGuarani(um.tesoreria.core.hexagonal.guarani.alumnoGuarani.infrastructure.web.dto.ContactoGuarani dto) {
        if (dto == null) {
            return null;
        }
        return ContactoGuarani.builder()
                .personaContacto(dto.getPersonaContacto())
                .persona(dto.getPersona())
                .contactoTipo(dto.getContactoTipo())
                .otrosContactos(dto.getOtrosContactos())
                .email(dto.getEmail())
                .telefonoCodigoArea(dto.getTelefonoCodigoArea())
                .telefonoNumero(dto.getTelefonoNumero())
                .build();
    }

    private PropuestaGuarani toPropuestaGuarani(um.tesoreria.core.hexagonal.guarani.alumnoGuarani.infrastructure.web.dto.PropuestaGuarani dto) {
        if (dto == null) {
            return null;
        }
        return PropuestaGuarani.builder()
                .propuestaId(dto.getPropuestaId())
                .nombre(dto.getNombre())
                .nombreAbreviado(dto.getNombreAbreviado())
                .codigo(dto.getCodigo())
                .propuestaTipo(dto.getPropuestaTipo())
                .propuestaTipoRel(toPropuestaTipoGuarani(dto.getPropuestaTipoRel()))
                .publica(dto.getPublica())
                .documentoAlta(dto.getDocumentoAlta())
                .fechaAlta(dto.getFechaAlta())
                .campoDisciplinar(dto.getCampoDisciplinar())
                .escalaCumplimiento(dto.getEscalaCumplimiento())
                .documentoBaja(dto.getDocumentoBaja())
                .fechaBaja(dto.getFechaBaja())
                .aTermino(dto.getATermino())
                .entidad(dto.getEntidad())
                .estado(dto.getEstado())
                .build();
    }

    private PropuestaTipoGuarani toPropuestaTipoGuarani(um.tesoreria.core.hexagonal.guarani.alumnoGuarani.infrastructure.web.dto.PropuestaTipoGuarani dto) {
        if (dto == null) {
            return null;
        }
        return PropuestaTipoGuarani.builder()
                .propuestaTipoId(dto.getPropuestaTipoId())
                .descripcion(dto.getDescripcion())
                .otorgaTitulo(dto.getOtorgaTitulo())
                .reportaAraucano(dto.getReportaAraucano())
                .permiteMatricular(dto.getPermiteMatricular())
                .permiteInscribir(dto.getPermiteInscribir())
                .controlRegularidad(dto.getControlRegularidad())
                .disponibleEnAutogestion(dto.getDisponibleEnAutogestion())
                .informarMovilidadInternacional(dto.getInformarMovilidadInternacional())
                .build();
    }

    private UbicacionGuarani toUbicacionGuarani(um.tesoreria.core.hexagonal.guarani.alumnoGuarani.infrastructure.web.dto.UbicacionGuarani dto) {
        if (dto == null) {
            return null;
        }
        return UbicacionGuarani.builder()
                .ubicacionId(dto.getUbicacionId())
                .nombre(dto.getNombre())
                .ubicacionTipo(dto.getUbicacionTipo())
                .localidad(dto.getLocalidad())
                .calle(dto.getCalle())
                .numero(dto.getNumero())
                .codigoPostal(dto.getCodigoPostal())
                .telefono(dto.getTelefono())
                .fax(dto.getFax())
                .email(dto.getEmail())
                .institucionAraucano(dto.getInstitucionAraucano())
                .latitud(dto.getLatitud())
                .longitud(dto.getLongitud())
                .build();
    }

}
