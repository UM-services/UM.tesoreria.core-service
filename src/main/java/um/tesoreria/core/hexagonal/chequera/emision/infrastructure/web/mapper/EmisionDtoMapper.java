package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.ActualizarBorradorData;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.AvisoEmision;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.BorradorChequera;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.BorradorCuota;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.ContextoEmision;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.CrearBorradorData;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.ResultadoEmision;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto.ActualizarBorradorRequest;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto.BorradorResponse;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto.ContextoEmisionResponse;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto.CrearBorradorRequest;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto.ResultadoEmisionResponse;

import java.util.List;

@Component
public class EmisionDtoMapper {

    public ContextoEmisionResponse toResponse(ContextoEmision contexto) {
        return ContextoEmisionResponse.builder()
                .lectivoId(contexto.getLectivoId())
                .persona(contexto.getPersona() == null ? null : ContextoEmisionResponse.PersonaDto.builder()
                        .personaId(contexto.getPersona().getPersonaId())
                        .documentoId(contexto.getPersona().getDocumentoId())
                        .apellido(contexto.getPersona().getApellido())
                        .nombre(contexto.getPersona().getNombre())
                        .build())
                .domicilio(contexto.getDomicilio() == null ? null : ContextoEmisionResponse.DomicilioDto.builder()
                        .domicilioId(contexto.getDomicilio().getDomicilioId())
                        .emailPersonal(contexto.getDomicilio().getEmailPersonal())
                        .emailInstitucional(contexto.getDomicilio().getEmailInstitucional())
                        .emailPagador(contexto.getDomicilio().getEmailPagador())
                        .emailPersonalValido(contexto.getDomicilio().isEmailPersonalValido())
                        .emailInstitucionalValido(contexto.getDomicilio().isEmailInstitucionalValido())
                        .build())
                .deuda(contexto.getDeuda() == null ? null : ContextoEmisionResponse.DeudaDto.builder()
                        .cuotas(contexto.getDeuda().getCuotas())
                        .importe(contexto.getDeuda().getImporte())
                        .cuotasPermitidas(contexto.getDeuda().getCuotasPermitidas())
                        .excedida(contexto.getDeuda().isExcedida())
                        .build())
                .permisos(contexto.getPermisos() == null ? null : ContextoEmisionResponse.PermisosDto.builder()
                        .puedeEmitir(contexto.getPermisos().isPuedeEmitir())
                        .puedeImprimir(contexto.getPermisos().isPuedeImprimir())
                        .puedeModificar(contexto.getPermisos().isPuedeModificar())
                        .puedeForzarDeuda(contexto.getPermisos().isPuedeForzarDeuda())
                        .build())
                .series(contexto.getSeries().stream().map(serie -> ContextoEmisionResponse.SerieDto.builder()
                        .chequeraId(serie.getChequeraId())
                        .facultadId(serie.getFacultadId())
                        .facultadNombre(serie.getFacultadNombre())
                        .tipoChequeraId(serie.getTipoChequeraId())
                        .tipoChequeraNombre(serie.getTipoChequeraNombre())
                        .chequeraSerieId(serie.getChequeraSerieId())
                        .claseChequeraId(serie.getClaseChequeraId())
                        .geograficaId(serie.getGeograficaId())
                        .geograficaNombre(serie.getGeograficaNombre())
                        .arancelTipoId(serie.getArancelTipoId())
                        .arancelTipoDescripcion(serie.getArancelTipoDescripcion())
                        .alternativaId(serie.getAlternativaId())
                        .cursoId(serie.getCursoId())
                        .fecha(serie.getFecha())
                        .ultimoEnvio(serie.getUltimoEnvio())
                        .enviado(serie.getEnviado())
                        .retenida(serie.getRetenida())
                        .pagada(serie.isPagada())
                        .deBaja(serie.isDeBaja())
                        .bajaFecha(serie.getBajaFecha())
                        .bajaObservaciones(serie.getBajaObservaciones())
                        .build()).toList())
                .avisos(toContextoAvisos(contexto.getAvisos()))
                .build();
    }

    public BorradorResponse toResponse(BorradorChequera borrador) {
        return BorradorResponse.builder()
                .borradorId(borrador.getBorradorId())
                .estado(borrador.getEstado() == null ? null : borrador.getEstado().name())
                .personaId(borrador.getPersonaId())
                .documentoId(borrador.getDocumentoId())
                .facultadId(borrador.getFacultadId())
                .facultadNombre(borrador.getFacultadNombre())
                .lectivoId(borrador.getLectivoId())
                .tipoChequeraId(borrador.getTipoChequeraId())
                .tipoChequeraNombre(borrador.getTipoChequeraNombre())
                .arancelTipoId(borrador.getArancelTipoId())
                .alternativaId(borrador.getAlternativaId())
                .cursoId(borrador.getCursoId())
                .chequeraSerieId(borrador.getChequeraSerieId())
                .fecha(borrador.getFecha())
                .hpum(borrador.getHpum())
                .becaPorcentaje(borrador.getBecaPorcentaje())
                .becaResolucion(borrador.getBecaResolucion())
                .becaFecha(borrador.getBecaFecha())
                .becaUserId(borrador.getBecaUserId())
                .calculaBeca(borrador.isCalculaBeca())
                .emailPersonal(borrador.getEmailPersonal())
                .emailInstitucional(borrador.getEmailInstitucional())
                .emailCopia(borrador.getEmailCopia())
                .incluyeBarras(borrador.isIncluyeBarras())
                .copiaFacultad(borrador.isCopiaFacultad())
                .chequeraId(borrador.getChequeraId())
                .chequeraSerieIdEmitida(borrador.getChequeraSerieIdEmitida())
                .auditoriaId(borrador.getAuditoriaId())
                .version(borrador.getVersion())
                .createdAt(borrador.getCreatedAt())
                .updatedAt(borrador.getUpdatedAt())
                .emitidoAt(borrador.getEmitidoAt())
                .totalBorrador(borrador.totalBorrador())
                .cuotas(borrador.getCuotas().stream().map(this::toCuota).toList())
                .alternativas(borrador.getAlternativas().stream().map(alternativa ->
                        BorradorResponse.AlternativaDto.builder()
                                .alternativaId(alternativa.getAlternativaId())
                                .productoId(alternativa.getProductoId())
                                .titulo(alternativa.getTitulo())
                                .cuotas(alternativa.getCuotas())
                                .descuentoPagoTermino(alternativa.getDescuentoPagoTermino())
                                .build()).toList())
                .arancelTipos(borrador.getArancelTipos().stream().map(tipo ->
                        BorradorResponse.ArancelTipoDto.builder()
                                .arancelTipoId(tipo.getArancelTipoId())
                                .nombre(tipo.getNombre())
                                .build()).toList())
                .avisos(toAvisos(borrador.getAvisos()))
                .build();
    }

    public ResultadoEmisionResponse toResponse(ResultadoEmision resultado) {
        return ResultadoEmisionResponse.builder()
                .chequeraId(resultado.getChequeraId())
                .facultadId(resultado.getFacultadId())
                .tipoChequeraId(resultado.getTipoChequeraId())
                .chequeraSerieId(resultado.getChequeraSerieId())
                .alternativaId(resultado.getAlternativaId())
                .numeroNuevo(resultado.isNumeroNuevo())
                .estabaPagada(resultado.isEstabaPagada())
                .borradorEstado(resultado.getBorradorEstado() == null ? null : resultado.getBorradorEstado().name())
                .auditoriaId(resultado.getAuditoriaId())
                .avisos(toAvisos(resultado.getAvisos()))
                .build();
    }

    public CrearBorradorData toData(CrearBorradorRequest request) {
        return CrearBorradorData.builder()
                .personaId(request.personaId())
                .documentoId(request.documentoId())
                .facultadId(request.facultadId())
                .lectivoId(request.lectivoId())
                .tipoChequeraId(request.tipoChequeraId())
                .arancelTipoId(request.arancelTipoId())
                .alternativaId(request.alternativaId())
                .cursoId(request.cursoId())
                .chequeraSerieId(request.chequeraSerieId())
                .becaPorcentaje(request.becaPorcentaje())
                .calculaBeca(request.calculaBeca())
                .fecha(request.fecha())
                .fuente(request.fuente())
                .emailPersonal(request.emailPersonal())
                .emailInstitucional(request.emailInstitucional())
                .incluyeBarras(request.incluyeBarras())
                .copiaFacultad(request.copiaFacultad())
                .build();
    }

    public ActualizarBorradorData toData(ActualizarBorradorRequest request) {
        return ActualizarBorradorData.builder()
                .version(request.version())
                .arancelTipoId(request.arancelTipoId())
                .alternativaId(request.alternativaId())
                .cursoId(request.cursoId())
                .fecha(request.fecha())
                .hpum(request.hpum())
                .becaPorcentaje(request.becaPorcentaje())
                .becaResolucion(request.becaResolucion())
                .becaFecha(request.becaFecha())
                .becaUserId(request.becaUserId())
                .calculaBeca(request.calculaBeca())
                .emailPersonal(request.emailPersonal())
                .emailInstitucional(request.emailInstitucional())
                .incluyeBarras(request.incluyeBarras())
                .copiaFacultad(request.copiaFacultad())
                .cuotas(request.cuotas() == null ? List.of() : request.cuotas().stream()
                        .map(this::toCuota).toList())
                .build();
    }

    private BorradorResponse.CuotaDto toCuota(BorradorCuota cuota) {
        return BorradorResponse.CuotaDto.builder()
                .borradorCuotaId(cuota.getBorradorCuotaId())
                .chequeraCuotaId(cuota.getChequeraCuotaId())
                .productoId(cuota.getProductoId())
                .alternativaId(cuota.getAlternativaId())
                .cuotaId(cuota.getCuotaId())
                .mes(cuota.getMes())
                .anio(cuota.getAnio())
                .vencimiento1(cuota.getVencimiento1())
                .vencimiento2(cuota.getVencimiento2())
                .vencimiento3(cuota.getVencimiento3())
                .importe1Original(cuota.getImporte1Original())
                .importe2Original(cuota.getImporte2Original())
                .importe3Original(cuota.getImporte3Original())
                .importe1Borrador(cuota.getImporte1Borrador())
                .importe2Borrador(cuota.getImporte2Borrador())
                .importe3Borrador(cuota.getImporte3Borrador())
                .arancelTipoId(cuota.getArancelTipoId())
                .tramoId(cuota.getTramoId())
                .descuentoPagoTermino(cuota.getDescuentoPagoTermino())
                .pagado(cuota.getPagado())
                .baja(cuota.getBaja())
                .manual(cuota.getManual())
                .compensada(cuota.getCompensada())
                .modificado(cuota.getModificado())
                .bloqueado(cuota.getBloqueado())
                .editable(!cuota.inmutable())
                .build();
    }

    private BorradorCuota toCuota(ActualizarBorradorRequest.CuotaRequest cuota) {
        return BorradorCuota.builder()
                .productoId(cuota.productoId())
                .alternativaId(cuota.alternativaId())
                .cuotaId(cuota.cuotaId())
                .mes(cuota.mes())
                .anio(cuota.anio())
                .vencimiento1(cuota.vencimiento1())
                .vencimiento2(cuota.vencimiento2())
                .vencimiento3(cuota.vencimiento3())
                .importe1Borrador(cuota.importe1Borrador())
                .importe2Borrador(cuota.importe2Borrador())
                .importe3Borrador(cuota.importe3Borrador())
                .manual(cuota.manual())
                .compensada(cuota.compensada())
                .baja(cuota.baja())
                .build();
    }

    private List<BorradorResponse.AvisoDto> toAvisos(List<AvisoEmision> avisos) {
        if (avisos == null) {
            return List.of();
        }
        return avisos.stream().map(aviso -> BorradorResponse.AvisoDto.builder()
                .codigo(aviso.getCodigo())
                .mensaje(aviso.getMensaje())
                .bloqueante(aviso.isBloqueante())
                .build()).toList();
    }

    private List<ContextoEmisionResponse.AvisoDto> toContextoAvisos(List<AvisoEmision> avisos) {
        if (avisos == null) {
            return List.of();
        }
        return avisos.stream().map(aviso -> ContextoEmisionResponse.AvisoDto.builder()
                .codigo(aviso.getCodigo())
                .mensaje(aviso.getMensaje())
                .bloqueante(aviso.isBloqueante())
                .build()).toList();
    }
}
