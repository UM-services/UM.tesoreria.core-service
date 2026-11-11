package um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import um.tesoreria.core.hexagonal.usuarios.permiso.application.service.PermisoService;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.model.Permiso;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.model.SyncPermisosResultado;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.ports.in.SyncPermisosUseCase;
import um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.manifest.PermisoManifestLoader;
import um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.web.dto.PermisoResponse;
import um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.web.dto.SyncPermisosResponse;
import um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.web.mapper.PermisoDtoMapper;
import um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.web.mapper.SyncPermisosDtoMapper;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebMvcTest(PermisoController.class)
class PermisoControllerTest {

    @Autowired
    private MockMvcTester mockMvc;

    @MockitoBean
    private PermisoService service;

    @MockitoBean
    private PermisoDtoMapper dtoMapper;

    @MockitoBean
    private PermisoManifestLoader manifestLoader;

    @MockitoBean
    private SyncPermisosUseCase syncPermisosUseCase;

    @MockitoBean
    private SyncPermisosDtoMapper syncDtoMapper;

    @Test
    void findAll_returnsList() {
        var domain = Permiso.builder().permisoId(1L).clave("chequeras.eliminar").build();
        var response = PermisoResponse.builder().permisoId(1L).clave("chequeras.eliminar").build();
        when(service.findAll()).thenReturn(List.of(domain));
        when(dtoMapper.toResponse(domain)).thenReturn(response);

        mockMvc.get().uri("/api/tesoreria/core/permiso/")
                .assertThat().hasStatusOk()
                .bodyJson().extractingPath("$[0].clave").isEqualTo("chequeras.eliminar");
    }

    @Test
    void findById_whenMissing_returns404() {
        when(service.findById(99L)).thenReturn(Optional.empty());

        mockMvc.get().uri("/api/tesoreria/core/permiso/99")
                .assertThat().hasStatus(404);
    }

    @Test
    void sync_dryRun_returnsTheDiff() {
        var resultado = SyncPermisosResultado.builder()
                .dryRun(true)
                .totalManifiesto(2)
                .agregados(1)
                .clavesAgregadas(List.of("TESORERIA::compras.gastos"))
                .build();
        var response = SyncPermisosResponse.builder()
                .dryRun(true)
                .totalManifiesto(2)
                .agregados(1)
                .clavesAgregadas(List.of("TESORERIA::compras.gastos"))
                .build();
        when(manifestLoader.load()).thenReturn(List.of(
                Permiso.builder().clave("compras.gastos").modulo("compras")
                        .descripcion("Gastos").build()));
        when(syncPermisosUseCase.sync(anyList(), eq(true))).thenReturn(resultado);
        when(syncDtoMapper.toResponse(resultado)).thenReturn(response);

        mockMvc.post().uri("/api/tesoreria/core/permiso/sync")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"dryRun\":true}")
                .assertThat().hasStatusOk()
                .bodyJson().extractingPath("$.agregados").isEqualTo(1);
    }
}
