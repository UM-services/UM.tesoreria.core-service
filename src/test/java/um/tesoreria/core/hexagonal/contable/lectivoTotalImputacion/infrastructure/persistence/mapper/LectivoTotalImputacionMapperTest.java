package um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.infrastructure.persistence.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.persistence.entity.CuentaEntity;
import um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.persistence.mapper.CuentaMapper;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.domain.model.LectivoTotalImputacion;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.infrastructure.persistence.entity.LectivoTotalImputacionEntity;
import um.tesoreria.core.hexagonal.chequera.producto.domain.model.Producto;
import um.tesoreria.core.hexagonal.chequera.producto.infrastructure.persistence.entity.ProductoEntity;
import um.tesoreria.core.hexagonal.chequera.producto.infrastructure.persistence.mapper.ProductoMapper;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.infrastructure.persistence.entity.TipoChequeraEntity;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.infrastructure.persistence.mapper.TipoChequeraMapper;
import um.tesoreria.core.hexagonal.facultad.domain.model.Facultad;
import um.tesoreria.core.hexagonal.facultad.infrastructure.persistence.entity.FacultadEntity;
import um.tesoreria.core.hexagonal.facultad.infrastructure.persistence.mapper.FacultadMapper;
import um.tesoreria.core.hexagonal.lectivo.domain.model.Lectivo;
import um.tesoreria.core.hexagonal.lectivo.infrastructure.persistence.entity.LectivoEntity;
import um.tesoreria.core.hexagonal.lectivo.infrastructure.persistence.mapper.LectivoMapper;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LectivoTotalImputacionMapperTest {

    @Mock
    private FacultadMapper facultadMapper;
    @Mock
    private LectivoMapper lectivoMapper;
    @Mock
    private TipoChequeraMapper tipoChequeraMapper;
    @Mock
    private ProductoMapper productoMapper;
    @Mock
    private CuentaMapper cuentaMapper;

    @InjectMocks
    private LectivoTotalImputacionMapper mapper;

    @Test
    void toDomain_whenEntityIsNull_returnsNull() {
        assertThat(mapper.toDomain(null)).isNull();
    }

    @Test
    void toDomain_mapsScalarFields() {
        var entity = LectivoTotalImputacionEntity.builder()
                .lectivoTotalImputacionId(1L)
                .facultadId(100)
                .lectivoId(200)
                .tipoChequeraId(300)
                .productoId(400)
                .numeroCuenta(new BigDecimal("123.45"))
                .build();

        var domain = mapper.toDomain(entity);

        assertThat(domain.getLectivoTotalImputacionId()).isEqualTo(1L);
        assertThat(domain.getFacultadId()).isEqualTo(100);
        assertThat(domain.getLectivoId()).isEqualTo(200);
        assertThat(domain.getTipoChequeraId()).isEqualTo(300);
        assertThat(domain.getProductoId()).isEqualTo(400);
        assertThat(domain.getNumeroCuenta()).isEqualByComparingTo(new BigDecimal("123.45"));
    }

    @Test
    void toDomain_mapsRelatedEntities() {
        var facultadEntity = new FacultadEntity();
        var lectivoEntity = new LectivoEntity();
        var tipoChequeraEntity = new TipoChequeraEntity();
        var productoEntity = new ProductoEntity();
        var cuentaEntity = new CuentaEntity();

        var entity = LectivoTotalImputacionEntity.builder()
                .lectivoTotalImputacionId(1L)
                .facultad(facultadEntity)
                .lectivo(lectivoEntity)
                .tipoChequera(tipoChequeraEntity)
                .producto(productoEntity)
                .cuenta(cuentaEntity)
                .build();

        var facultad = new Facultad();
        var lectivo = new Lectivo();
        var tipoChequera = new TipoChequera();
        var producto = new Producto();
        var cuenta = new Cuenta();

        when(facultadMapper.toDomain(facultadEntity)).thenReturn(facultad);
        when(lectivoMapper.toDomainModel(lectivoEntity)).thenReturn(lectivo);
        when(tipoChequeraMapper.toDomainModel(tipoChequeraEntity)).thenReturn(tipoChequera);
        when(productoMapper.toDomainModel(productoEntity)).thenReturn(producto);
        when(cuentaMapper.toDomain(cuentaEntity)).thenReturn(cuenta);

        var domain = mapper.toDomain(entity);

        assertThat(domain.getFacultad()).isEqualTo(facultad);
        assertThat(domain.getLectivo()).isEqualTo(lectivo);
        assertThat(domain.getTipoChequera()).isEqualTo(tipoChequera);
        assertThat(domain.getProducto()).isEqualTo(producto);
        assertThat(domain.getCuenta()).isEqualTo(cuenta);
    }

    @Test
    void toDomain_whenRelatedEntitiesAreNull_doesNotThrow() {
        var entity = LectivoTotalImputacionEntity.builder()
                .lectivoTotalImputacionId(1L)
                .facultadId(100)
                .lectivoId(200)
                .build();

        var domain = mapper.toDomain(entity);

        assertThat(domain.getFacultadId()).isEqualTo(100);
        assertThat(domain.getFacultad()).isNull();
    }

    @Test
    void toEntity_whenDomainIsNull_returnsNull() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    void toEntity_mapsAllFields() {
        var domain = LectivoTotalImputacion.builder()
                .lectivoTotalImputacionId(1L)
                .facultadId(100)
                .lectivoId(200)
                .tipoChequeraId(300)
                .productoId(400)
                .numeroCuenta(new BigDecimal("123.45"))
                .build();

        var entity = mapper.toEntity(domain);

        assertThat(entity.getLectivoTotalImputacionId()).isEqualTo(1L);
        assertThat(entity.getFacultadId()).isEqualTo(100);
        assertThat(entity.getLectivoId()).isEqualTo(200);
        assertThat(entity.getTipoChequeraId()).isEqualTo(300);
        assertThat(entity.getProductoId()).isEqualTo(400);
        assertThat(entity.getNumeroCuenta()).isEqualByComparingTo(new BigDecimal("123.45"));
    }

    @Test
    void toEntity_doesNotMapRelatedEntities() {
        var facultad = new Facultad();
        var domain = LectivoTotalImputacion.builder()
                .lectivoTotalImputacionId(1L)
                .facultad(facultad)
                .build();

        var entity = mapper.toEntity(domain);

        assertThat(entity.getFacultad()).isNull();
    }

}
