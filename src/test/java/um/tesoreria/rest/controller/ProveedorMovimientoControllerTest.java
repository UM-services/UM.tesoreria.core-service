package um.tesoreria.rest.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import um.tesoreria.rest.repository.IProveedorMovimientoRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProveedorMovimientoControllerTest {

    @Autowired
    private ProveedorMovimientoController controller;

    @Autowired
    private IProveedorMovimientoRepository repository;

    @BeforeEach
    void setUp() {
    }

    @Test
    void findAllEliminables() {
    }

    @Test
    void findAllAsignables() {
    }

    @Test
    void findByProveedorMovimientoId() {
    }

    @Test
    void findByOrdenPago() {
    }

    @Test
    void findLastOrdenPago() {
    }

    @Test
    void add() {
    }
}