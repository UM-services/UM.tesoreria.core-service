package um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.infrastructure.persistence.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.infrastructure.persistence.entity.LectivoTotalImputacionEntity;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaLectivoTotalImputacionRepositoryTest.TestAuditingConfig.class)
class JpaLectivoTotalImputacionRepositoryTest {

    @EnableJpaAuditing
    static class TestAuditingConfig {}

    @Autowired
    private JpaLectivoTotalImputacionRepository repository;

    @Test
    void saveAndFindById() {
        var saved = repository.save(LectivoTotalImputacionEntity.builder()
                .facultadId(1).lectivoId(2).tipoChequeraId(3).productoId(4)
                .numeroCuenta(new BigDecimal("100.50")).build());

        assertThat(saved.getLectivoTotalImputacionId()).isNotNull();

        var found = repository.findById(saved.getLectivoTotalImputacionId());
        assertThat(found).isPresent();
        assertThat(found.get().getFacultadId()).isEqualTo(1);
    }

    @Test
    void findAllByFacultadIdAndLectivoIdAndTipoChequeraId() {
        repository.save(LectivoTotalImputacionEntity.builder()
                .facultadId(1).lectivoId(2).tipoChequeraId(3).productoId(4)
                .numeroCuenta(new BigDecimal("10.00")).build());
        repository.save(LectivoTotalImputacionEntity.builder()
                .facultadId(1).lectivoId(2).tipoChequeraId(3).productoId(5)
                .numeroCuenta(new BigDecimal("20.00")).build());
        repository.save(LectivoTotalImputacionEntity.builder()
                .facultadId(1).lectivoId(2).tipoChequeraId(4).productoId(6)
                .numeroCuenta(new BigDecimal("30.00")).build());

        var results = repository.findAllByFacultadIdAndLectivoIdAndTipoChequeraId(1, 2, 3);

        assertThat(results).hasSize(2);
    }

    @Test
    void findAllByLectivoId() {
        repository.save(LectivoTotalImputacionEntity.builder()
                .facultadId(1).lectivoId(2).tipoChequeraId(3).productoId(4).build());
        repository.save(LectivoTotalImputacionEntity.builder()
                .facultadId(1).lectivoId(2).tipoChequeraId(4).productoId(5).build());
        repository.save(LectivoTotalImputacionEntity.builder()
                .facultadId(1).lectivoId(3).tipoChequeraId(3).productoId(6).build());

        var results = repository.findAllByLectivoId(2);

        assertThat(results).hasSize(2);
    }

    @Test
    void findByFacultadIdAndLectivoIdAndTipoChequeraIdAndProductoId() {
        repository.save(LectivoTotalImputacionEntity.builder()
                .facultadId(1).lectivoId(2).tipoChequeraId(3).productoId(4)
                .numeroCuenta(new BigDecimal("50.00")).build());
        repository.save(LectivoTotalImputacionEntity.builder()
                .facultadId(1).lectivoId(2).tipoChequeraId(3).productoId(5)
                .numeroCuenta(new BigDecimal("75.00")).build());

        var result = repository.findByFacultadIdAndLectivoIdAndTipoChequeraIdAndProductoId(1, 2, 3, 4);

        assertThat(result).isPresent();
        assertThat(result.get().getNumeroCuenta()).isEqualByComparingTo(new BigDecimal("50.00"));
    }

    @Test
    void findByFacultadIdAndLectivoIdAndTipoChequeraIdAndProductoId_whenNotFound() {
        var result = repository.findByFacultadIdAndLectivoIdAndTipoChequeraIdAndProductoId(999, 999, 999, 999);

        assertThat(result).isEmpty();
    }

}
