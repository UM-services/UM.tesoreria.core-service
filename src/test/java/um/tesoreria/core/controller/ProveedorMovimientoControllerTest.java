package um.tesoreria.core.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import um.tesoreria.core.kotlin.model.Comprobante;
import um.tesoreria.core.kotlin.model.Modulo;
import um.tesoreria.core.kotlin.model.ProveedorMovimiento;
import um.tesoreria.core.kotlin.repository.ModuloRepository;
import um.tesoreria.core.repository.ComprobanteRepository;
import um.tesoreria.core.repository.ProveedorMovimientoRepository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProveedorMovimientoControllerTest {

    @Autowired
    private ProveedorMovimientoRepository repository;

    @Autowired
    private ComprobanteRepository comprobanteRepository;

    @Autowired
    private ModuloRepository moduloRepository;

    @Autowired
    private ProveedorMovimientoController controller;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void add() {
        moduloRepository.deleteAll();
        moduloRepository.save(new Modulo(4, "Orden Pago"));
        comprobanteRepository.deleteAll();
        comprobanteRepository.save(new Comprobante(6, "Orden Pago", 4, (byte) 1, (byte) 1, (byte) 1, (byte) 1, 0L, (byte) 0, null, null, null, null));
        repository.deleteAll();
        OffsetDateTime fechaPago = OffsetDateTime.now();
        ProveedorMovimiento proveedorMovimiento = new ProveedorMovimiento(null, null, "Personal Administrativo y Docente", 6, fechaPago, fechaPago, fechaPago.getYear() - 1989, 1L, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, null, null, "Sueldos mes " + fechaPago.getMonthValue() + "/" + fechaPago.getYear(), null, (byte) 0, (byte) 0, null, null, null, null, null);
        ResponseEntity<ProveedorMovimiento> response = controller.add(proveedorMovimiento);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        repository.deleteAll();
        comprobanteRepository.deleteAll();
        moduloRepository.deleteAll();
    }
}