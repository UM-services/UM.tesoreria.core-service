package um.tesoreria.core.hexagonal.chequera.emision.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.BorradorChequera;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.BorradorSede;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.EstadoBorrador;
import um.tesoreria.core.hexagonal.chequera.emision.domain.ports.out.BorradorChequeraRepository;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto.FacultadSedeBorradorResponse;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.application.service.TipoChequeraService;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera;
import um.tesoreria.core.hexagonal.dependencias.facultad.application.service.FacultadService;
import um.tesoreria.core.hexagonal.dependencias.facultad.domain.model.Facultad;
import um.tesoreria.core.hexagonal.dependencias.geografica.application.service.GeograficaService;
import um.tesoreria.core.hexagonal.dependencias.geografica.domain.model.Geografica;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BorradorConsultaServiceTest {

    @Mock
    private BorradorChequeraRepository borradorRepository;
    @Mock
    private BorradorEmisionService borradorEmisionService;
    @Mock
    private TipoChequeraService tipoChequeraService;
    @Mock
    private FacultadService facultadService;
    @Mock
    private GeograficaService geograficaService;
    @InjectMocks
    private BorradorConsultaService service;

    private void catalogos() {
        when(tipoChequeraService.findAll()).thenReturn(List.of(
                TipoChequera.builder().tipoChequeraId(1).geograficaId(1).build(),
                TipoChequera.builder().tipoChequeraId(2).geograficaId(2).build(),
                TipoChequera.builder().tipoChequeraId(3).geograficaId(null).build()));
        when(facultadService.findAll()).thenReturn(List.of(
                Facultad.builder().facultadId(14).nombre("Facultad de Ciencias Médicas").build(),
                Facultad.builder().facultadId(15).nombre("Escuela Técnica").build()));
        when(geograficaService.findAll()).thenReturn(List.of(
                new Geografica(1, "Mendoza", (byte) 0),
                new Geografica(2, "San Rafael", (byte) 0)));
    }

    @Test
    void resumenAgrupaPorFacultadYSedeDerivadaDelTipoDeChequera() {
        catalogos();
        when(borradorRepository.findAllSedesByLectivoIdAndEstado(38, EstadoBorrador.ABIERTO)).thenReturn(List.of(
                new BorradorSede(14, 1),
                new BorradorSede(14, 1),
                new BorradorSede(14, 2),
                new BorradorSede(15, 1)));

        List<FacultadSedeBorradorResponse> resumen = service.resumenLectivo(38);

        assertThat(resumen).containsExactly(
                new FacultadSedeBorradorResponse(14, "Facultad de Ciencias Médicas", 1, "Mendoza", 2L),
                new FacultadSedeBorradorResponse(14, "Facultad de Ciencias Médicas", 2, "San Rafael", 1L),
                new FacultadSedeBorradorResponse(15, "Escuela Técnica", 1, "Mendoza", 1L));
    }

    @Test
    void resumenDescartaBorradoresConTipoSinSede() {
        when(tipoChequeraService.findAll()).thenReturn(List.of(
                TipoChequera.builder().tipoChequeraId(3).geograficaId(null).build()));
        when(borradorRepository.findAllSedesByLectivoIdAndEstado(38, EstadoBorrador.ABIERTO))
                .thenReturn(List.of(new BorradorSede(14, 3)));

        assertThat(service.resumenLectivo(38)).isEmpty();
    }

    @Test
    void resumenSinBorradoresDevuelveListaVaciaSinCargarCatalogosDeNombres() {
        when(tipoChequeraService.findAll()).thenReturn(List.of());
        when(borradorRepository.findAllSedesByLectivoIdAndEstado(38, EstadoBorrador.ABIERTO)).thenReturn(List.of());

        assertThat(service.resumenLectivo(38)).isEmpty();
    }

    @Test
    void resumenConLectivoNuloDevuelveListaVacia() {
        assertThat(service.resumenLectivo(null)).isEmpty();
    }

    @Test
    void listarPorSedeFiltraLaSedeDerivadaYOrdenaPorApellidoYNombre() {
        when(tipoChequeraService.findAll()).thenReturn(List.of(
                TipoChequera.builder().tipoChequeraId(1).geograficaId(1).build(),
                TipoChequera.builder().tipoChequeraId(2).geograficaId(2).build()));
        BorradorChequera perez = BorradorChequera.builder().borradorId(2L).facultadId(14)
                .tipoChequeraId(1).personaApellido("Pérez").personaNombre("Ana").build();
        BorradorChequera alvarez = BorradorChequera.builder().borradorId(1L).facultadId(14)
                .tipoChequeraId(1).personaApellido("Álvarez").personaNombre("Beto").build();
        BorradorChequera otraSede = BorradorChequera.builder().borradorId(3L).facultadId(14)
                .tipoChequeraId(2).personaApellido("Aaa").personaNombre("Zzz").build();
        when(borradorRepository.findAllByLectivoIdAndFacultadIdAndEstado(38, 14, EstadoBorrador.ABIERTO))
                .thenReturn(List.of(perez, otraSede, alvarez));
        when(borradorEmisionService.enriquecerParaConsulta(any())).thenAnswer(invocation -> invocation.getArgument(0));

        List<BorradorChequera> resultado = service.listarPorSede(14, 38, 1);

        assertThat(resultado).extracting(BorradorChequera::getBorradorId).containsExactly(1L, 2L);
    }

    @Test
    void listarPorSedeConParametrosNulosDevuelveListaVacia() {
        assertThat(service.listarPorSede(null, 38, 1)).isEmpty();
        assertThat(service.listarPorSede(14, null, 1)).isEmpty();
        assertThat(service.listarPorSede(14, 38, null)).isEmpty();
    }
}
