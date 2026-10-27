package um.tesoreria.core.hexagonal.chequera.estadoChequera.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.model.EstadoChequera;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.model.CuotaEstado;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.model.DebitoEstado;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.model.CursoMaterias;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.model.MateriaInscripta;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.model.MateriasEstado;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.model.PeriodoMaterias;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.model.ProductoEstado;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.infrastructure.web.dto.EstadoChequeraResponse;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.infrastructure.web.dto.CuotaEstadoResponse;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.infrastructure.web.dto.DebitoEstadoResponse;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.infrastructure.web.dto.CursoMateriasResponse;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.infrastructure.web.dto.MateriaInscriptaResponse;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.infrastructure.web.dto.MateriasEstadoResponse;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.infrastructure.web.dto.PeriodoMateriasResponse;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.infrastructure.web.dto.ProductoEstadoResponse;

@Component
public class EstadoChequeraDtoMapper {

    public EstadoChequeraResponse toResponse(EstadoChequera estado) {
        if (estado == null) return null;
        return new EstadoChequeraResponse(
                estado.facultadId(),
                estado.facultadNombre(),
                estado.tipoChequeraId(),
                estado.tipoChequeraNombre(),
                estado.chequeraSerieId(),
                estado.personaId(),
                estado.personaApellido(),
                estado.personaNombre(),
                estado.arancelTipoDescripcion(),
                estado.lectivoNombre(),
                estado.becaPorcentaje(),
                estado.tipoImpresionNombre(),
                estado.alternativaId(),
                estado.hpum(),
                estado.productos().stream().map(this::toResponse).toList(),
                estado.debitos().stream().map(this::toResponse).toList(),
                estado.materias().stream().map(this::toResponse).toList());
    }

    private ProductoEstadoResponse toResponse(ProductoEstado producto) {
        return new ProductoEstadoResponse(
                producto.productoId(),
                producto.nombre(),
                producto.tituloCuota(),
                producto.totalCuotas(),
                producto.total(),
                producto.pagado(),
                producto.deuda(),
                producto.cuotas().stream().map(this::toResponse).toList());
    }

    private CuotaEstadoResponse toResponse(CuotaEstado cuota) {
        return new CuotaEstadoResponse(
                cuota.cuotaId(),
                cuota.mes(),
                cuota.anho(),
                cuota.primerVencimiento(),
                cuota.importe(),
                cuota.ordenPago(),
                cuota.fechaPago(),
                cuota.importePagado(),
                cuota.referenciaPago(),
                cuota.estado());
    }

    private DebitoEstadoResponse toResponse(DebitoEstado debito) {
        return new DebitoEstadoResponse(
                debito.cuotaId(),
                debito.importe(),
                debito.fechaVencimiento(),
                debito.cbu(),
                debito.tipoDebito(),
                debito.fechaEnvio(),
                debito.rechazado(),
                debito.motivoRechazo());
    }

    private MateriasEstadoResponse toResponse(MateriasEstado materias) {
        return new MateriasEstadoResponse(
                materias.fuente(),
                materias.estado(),
                materias.cursoInscripcion(),
                materias.plan(),
                materias.carrera(),
                materias.observaciones(),
                materias.cursos().stream().map(this::toResponse).toList());
    }

    private CursoMateriasResponse toResponse(CursoMaterias curso) {
        return new CursoMateriasResponse(
                curso.curso(),
                curso.periodos().stream().map(this::toResponse).toList());
    }

    private PeriodoMateriasResponse toResponse(PeriodoMaterias periodo) {
        return new PeriodoMateriasResponse(
                periodo.periodo(),
                periodo.materias().stream().map(this::toResponse).toList());
    }

    private MateriaInscriptaResponse toResponse(MateriaInscripta materia) {
        return new MateriaInscriptaResponse(materia.materiaId(), materia.nombre());
    }
}
