package um.tesoreria.core.controller;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import um.tesoreria.core.hexagonal.contable.bancaria.application.exception.BancariaConflictoException;
import um.tesoreria.core.hexagonal.contable.bancaria.application.exception.BancariaConflictoException.Codigo;
import um.tesoreria.core.hexagonal.contable.bancaria.application.exception.BancariaNoEncontradaException;
import um.tesoreria.core.hexagonal.contable.bancaria.application.exception.BancariaSolicitudInvalidaException;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.Bancaria;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.BancariaCambios;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.BancariaUso;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.in.CreateBancariaUseCase;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.in.DeleteBancariaUseCase;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.in.GetBancariaByIdUseCase;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.in.GetBancariaUsoUseCase;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.in.UpdateBancariaUseCase;
import um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.web.mapper.BancariaDtoMapper;
import um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.persistence.entity.CuentaEntity;
import um.tesoreria.core.service.BancariaService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@WebMvcTest(BancariaController.class)
@Import({BancariaDtoMapper.class, BancariaExceptionAdvice.class})
class BancariaControllerTest {
    @Autowired MockMvcTester mvc;
    @MockitoBean BancariaService service;
    @MockitoBean GetBancariaByIdUseCase get;
    @MockitoBean GetBancariaUsoUseCase uso;
    @MockitoBean CreateBancariaUseCase create;
    @MockitoBean UpdateBancariaUseCase update;
    @MockitoBean DeleteBancariaUseCase delete;

    static final String JSON = MediaType.APPLICATION_JSON_VALUE;

    Bancaria cuenta() {
        return Bancaria.builder().bancariaId(8L).banco("Santander").numero("123-4").cbu("0070999000001234567891")
                .titular("Universidad").cuit("30-51859446-6").numeroCuenta(new BigDecimal("11101010001")).proveedorId(12)
                .created(LocalDateTime.parse("2024-01-01T10:00:00")).updated(LocalDateTime.parse("2026-10-01T09:30:15"))
                .uuid("a".repeat(32)).build();
    }

    BancariaCambios capturarPost() {
        ArgumentCaptor<BancariaCambios> captor = ArgumentCaptor.forClass(BancariaCambios.class);
        verify(create).createBancaria(captor.capture());
        return captor.getValue();
    }

    BancariaCambios capturarPut(long id) {
        ArgumentCaptor<BancariaCambios> captor = ArgumentCaptor.forClass(BancariaCambios.class);
        verify(update).updateBancaria(eq(id), captor.capture());
        return captor.getValue();
    }

    // --- Regresión del listado legacy (GET /), que #409 no cambia -------------------------------------------

    @Test
    void elListadoLegacyConservaExactamenteLasClavesYLosValores() {
        um.tesoreria.core.kotlin.model.Bancaria legada = new um.tesoreria.core.kotlin.model.Bancaria();
        legada.setBancariaId(8L);
        legada.setBanco("Santander");
        legada.setNumero("123-4");
        legada.setCbu("0070999000001234567891");
        legada.setTitular("Universidad");
        legada.setCuit("30-51859446-6");
        legada.setNumeroCuenta(new BigDecimal("11101010001"));
        legada.setProveedorId(12);
        legada.setCuenta(CuentaEntity.builder().numeroCuenta(new BigDecimal("11101010001")).nombre("Banco Santander").build());
        legada.setCreated(LocalDateTime.parse("2024-01-01T10:00:00"));
        legada.setUpdated(LocalDateTime.parse("2026-10-01T09:30:15"));
        when(service.findAll()).thenReturn(List.of(legada));

        var json = mvc.get().uri("/bancaria/").assertThat().hasStatus(HttpStatus.OK).bodyJson();
        json.extractingPath("$[0]").asInstanceOf(org.assertj.core.api.InstanceOfAssertFactories.MAP)
                .containsOnlyKeys("bancariaId", "banco", "numero", "cbu", "titular", "cuit", "numeroCuenta", "proveedorId",
                        "cuenta", "created", "updated");
        json.extractingPath("$[0].bancariaId").isEqualTo(8);
        json.extractingPath("$[0].numeroCuenta").isEqualTo(11101010001L);
        json.extractingPath("$[0].cuenta.nombre").isEqualTo("Banco Santander");
        json.extractingPath("$[0].created").isEqualTo("2024-01-01T10:00:00");
        json.extractingPath("$[0].updated").isEqualTo("2026-10-01T09:30:15");
    }

    // --- Lecturas nuevas ------------------------------------------------------------------------------------

    @Test
    void getPorIdDevuelveLasMismasClavesPlanasDelListadoSinCuentaAnidadaNiStatus() {
        when(get.getBancariaById(8L)).thenReturn(cuenta());
        var json = mvc.get().uri("/bancaria/8").assertThat().hasStatus(HttpStatus.OK).bodyJson();
        json.extractingPath("$").asInstanceOf(org.assertj.core.api.InstanceOfAssertFactories.MAP)
                .containsOnlyKeys("bancariaId", "banco", "numero", "cbu", "titular", "cuit", "numeroCuenta", "proveedorId",
                        "created", "updated")
                .doesNotContainKeys("status", "cuenta", "uuid");
        json.extractingPath("$.updated").isEqualTo("2026-10-01T09:30:15");
        json.extractingPath("$.numeroCuenta").isEqualTo(11101010001L);
    }

    @Test
    void getDeInexistenteEs404ConCuerpoYClaveStatus() {
        when(get.getBancariaById(99L)).thenThrow(new BancariaNoEncontradaException(99L));
        var json = mvc.get().uri("/bancaria/99").assertThat().hasStatus(HttpStatus.NOT_FOUND).bodyJson();
        json.extractingPath("$.status").isEqualTo(404);
        json.extractingPath("$.codigo").isEqualTo("NO_ENCONTRADA");
    }

    @Test
    void usoDevuelveConteosYEliminable() {
        when(uso.getBancariaUso(8L)).thenReturn(new BancariaUso(2, 69000));
        var json = mvc.get().uri("/bancaria/8/uso").assertThat().hasStatus(HttpStatus.OK).bodyJson();
        json.extractingPath("$.valores").isEqualTo(2);
        json.extractingPath("$.libroBanco").isEqualTo(69000);
        json.extractingPath("$.eliminable").isEqualTo(false);
    }

    @Test
    void unIdQueNoEsNumeroEs400ConCuerpo() {
        var json = mvc.get().uri("/bancaria/abc").assertThat().hasStatus(HttpStatus.BAD_REQUEST).bodyJson();
        json.extractingPath("$.status").isEqualTo(400);
        json.extractingPath("$.codigo").isEqualTo("SOLICITUD_INVALIDA");
    }

    // --- Escrituras -----------------------------------------------------------------------------------------

    @Test
    void postDelegaAlAltaYResponde200SinClaveStatus() {
        when(create.createBancaria(any())).thenReturn(cuenta());
        var json = mvc.post().uri("/bancaria/").contentType(JSON).content("""
                {"bancariaId":0,"banco":"Santander","numero":"123-4","numeroCuenta":11101010001,"cuenta":{"x":1},"created":"2020-01-01T00:00:00"}""")
                .assertThat().hasStatus(HttpStatus.OK).bodyJson();
        json.extractingPath("$.bancariaId").isEqualTo(8);
        json.extractingPath("$").asInstanceOf(org.assertj.core.api.InstanceOfAssertFactories.MAP).doesNotContainKey("status");
        BancariaCambios pedido = capturarPost();
        assertThat(pedido.getBancariaId()).isZero();
        assertThat(pedido.getBanco().valor()).isEqualTo("Santander");
        assertThat(pedido.getNumeroCuenta().valor()).isEqualByComparingTo("11101010001");
        assertThat(pedido.getTitular().presente()).isFalse();
    }

    @Test
    void putDistingueAusenteDeNull() {
        when(update.updateBancaria(eq(8L), any())).thenReturn(cuenta());
        mvc.put().uri("/bancaria/8").contentType(JSON).content("{\"titular\":null,\"proveedorId\":null,\"cbu\":\"\"}")
                .assertThat().hasStatus(HttpStatus.OK);
        BancariaCambios cambios = capturarPut(8L);
        assertThat(cambios.getTitular().presente()).isTrue();
        assertThat(cambios.getTitular().valor()).isNull();
        assertThat(cambios.getProveedorId().presente()).isTrue();
        assertThat(cambios.getProveedorId().valor()).isNull();
        assertThat(cambios.getCbu().presente()).isTrue();
        assertThat(cambios.getCbu().valor()).isEmpty();
        assertThat(cambios.getBanco().presente()).isFalse();
        assertThat(cambios.getCuit().presente()).isFalse();
        assertThat(cambios.getNumeroCuenta().presente()).isFalse();
        assertThat(cambios.getUpdated()).isNull();
    }

    @Test
    void putConPayloadCompletoDeVb6TomaElIdYLaPrecondicionOpcional() {
        when(update.updateBancaria(eq(8L), any())).thenReturn(cuenta());
        mvc.put().uri("/bancaria/8").contentType(JSON).content("""
                {"bancariaId":8,"banco":"Santander","numero":"123-4","cbu":"","titular":"U","cuit":"30518594466",
                 "numeroCuenta":11101010001,"proveedorId":12,"updated":"2026-10-01T09:30:15"}""")
                .assertThat().hasStatus(HttpStatus.OK);
        BancariaCambios cambios = capturarPut(8L);
        assertThat(cambios.getBancariaId()).isEqualTo(8L);
        assertThat(cambios.getUpdated()).isEqualTo(LocalDateTime.parse("2026-10-01T09:30:15"));
        assertThat(cambios.getProveedorId().valor()).isEqualTo(12);
    }

    @Test
    void putAceptaUpdatedConZona() {
        when(update.updateBancaria(eq(8L), any())).thenReturn(cuenta());
        mvc.put().uri("/bancaria/8").contentType(JSON).content("{\"updated\":\"2026-10-01T09:30:15Z\"}")
                .assertThat().hasStatus(HttpStatus.OK);
        assertThat(capturarPut(8L).getUpdated()).isEqualTo(LocalDateTime.parse("2026-10-01T09:30:15"));
    }

    @Test
    void numerosConDecimalesOFueraDeRangoSon400NumeroInvalidoSinLlegarAlCasoDeUso() {
        for (String cuerpo : new String[]{
                "{\"proveedorId\":12.7}", "{\"proveedorId\":0}", "{\"proveedorId\":32768}", "{\"proveedorId\":-1}",
                "{\"numeroCuenta\":1.5}", "{\"numeroCuenta\":0}", "{\"numeroCuenta\":-5}", "{\"numeroCuenta\":123456789012}",
                "{\"bancariaId\":3.2}"}) {
            var json = mvc.put().uri("/bancaria/8").contentType(JSON).content(cuerpo)
                    .assertThat().hasStatus(HttpStatus.BAD_REQUEST).bodyJson();
            json.extractingPath("$.codigo").as(cuerpo).isEqualTo("NUMERO_INVALIDO");
            json.extractingPath("$.status").isEqualTo(400);
        }
        verifyNoInteractions(update);
    }

    @Test
    void numerosEnterosValidosDeLosLimitesPasan() {
        when(update.updateBancaria(eq(8L), any())).thenReturn(cuenta());
        mvc.put().uri("/bancaria/8").contentType(JSON).content("{\"proveedorId\":32767,\"numeroCuenta\":99999999999,\"bancariaId\":8.0}")
                .assertThat().hasStatus(HttpStatus.OK);
        BancariaCambios cambios = capturarPut(8L);
        assertThat(cambios.getProveedorId().valor()).isEqualTo(32767);
        assertThat(cambios.getNumeroCuenta().valor()).isEqualByComparingTo("99999999999");
    }

    @Test
    void updatedConFormatoInvalidoEs400() {
        mvc.put().uri("/bancaria/8").contentType(JSON).content("{\"updated\":\"ayer\"}")
                .assertThat().hasStatus(HttpStatus.BAD_REQUEST).bodyJson().extractingPath("$.codigo").isEqualTo("SOLICITUD_INVALIDA");
    }

    @Test
    void cuerpoMalformadoEs400ConCuerpo() {
        var json = mvc.post().uri("/bancaria/").contentType(JSON).content("{no es json")
                .assertThat().hasStatus(HttpStatus.BAD_REQUEST).bodyJson();
        json.extractingPath("$.status").isEqualTo(400);
        json.extractingPath("$.codigo").isEqualTo("SOLICITUD_INVALIDA");
    }

    @Test
    void cadaRechazoDeReglaTieneSuStatusYCodigo() {
        when(create.createBancaria(any())).thenThrow(new BancariaSolicitudInvalidaException(
                BancariaSolicitudInvalidaException.Codigo.CUIT_INVALIDO, "cuit inválido"));
        var alta = mvc.post().uri("/bancaria/").contentType(JSON).content("{}").assertThat().hasStatus(HttpStatus.BAD_REQUEST).bodyJson();
        alta.extractingPath("$.codigo").isEqualTo("CUIT_INVALIDO");
        alta.extractingPath("$.message").isEqualTo("cuit inválido");

        when(update.updateBancaria(any(), any())).thenThrow(new BancariaConflictoException(Codigo.CAMBIO_CONCURRENTE, "recargar"));
        var edicion = mvc.put().uri("/bancaria/8").contentType(JSON).content("{}").assertThat().hasStatus(HttpStatus.CONFLICT).bodyJson();
        edicion.extractingPath("$.codigo").isEqualTo("CAMBIO_CONCURRENTE");
        edicion.extractingPath("$.status").isEqualTo(409);

        doThrow(new BancariaConflictoException(Codigo.EN_USO, "en uso")).when(delete).deleteBancaria(8L);
        mvc.delete().uri("/bancaria/8").assertThat().hasStatus(HttpStatus.CONFLICT).bodyJson().extractingPath("$.codigo").isEqualTo("EN_USO");
    }

    @Test
    void putYDeleteDeInexistenteSon404ConCuerpo() {
        when(update.updateBancaria(eq(99L), any())).thenThrow(new BancariaNoEncontradaException(99L));
        doThrow(new BancariaNoEncontradaException(99L)).when(delete).deleteBancaria(99L);
        mvc.put().uri("/bancaria/99").contentType(JSON).content("{}").assertThat().hasStatus(HttpStatus.NOT_FOUND)
                .bodyJson().extractingPath("$.status").isEqualTo(404);
        mvc.delete().uri("/bancaria/99").assertThat().hasStatus(HttpStatus.NOT_FOUND)
                .bodyJson().extractingPath("$.status").isEqualTo(404);
    }

    @Test
    void unFalloDeBloqueoDeLaBaseEs409Conflicto() {
        when(update.updateBancaria(any(), any())).thenThrow(new org.springframework.dao.CannotAcquireLockException("timeout"));
        var json = mvc.put().uri("/bancaria/8").contentType(JSON).content("{}").assertThat().hasStatus(HttpStatus.CONFLICT).bodyJson();
        json.extractingPath("$.codigo").isEqualTo("CONFLICTO");
        json.extractingPath("$.status").isEqualTo(409);
    }

    @Test
    void bajaExitosaDevuelveCuerpoVacio() {
        mvc.delete().uri("/bancaria/15").assertThat().hasStatus(HttpStatus.NO_CONTENT).body().isEmpty();
        verify(delete).deleteBancaria(15L);
    }
}
