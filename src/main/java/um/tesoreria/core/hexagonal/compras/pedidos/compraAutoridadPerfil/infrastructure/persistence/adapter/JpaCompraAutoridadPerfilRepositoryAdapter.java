package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.application.exception.CompraAutoridadPerfilException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.domain.model.CompraAutoridadPerfil;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.domain.ports.out.CompraAutoridadPerfilRepository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.infrastructure.persistence.entity.CompraAutoridadPerfilEntity;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.infrastructure.persistence.mapper.CompraAutoridadPerfilMapper;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.infrastructure.persistence.repository.JpaCompraAutoridadPerfilRepository;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaCompraAutoridadPerfilRepositoryAdapter implements CompraAutoridadPerfilRepository {

    private final JpaCompraAutoridadPerfilRepository repository;
    private final CompraAutoridadPerfilMapper mapper;

    @Override
    public Optional<CompraAutoridadPerfil> findByAutoridadPerfilId(Long autoridadPerfilId) {
        return repository.findByAutoridadPerfilId(autoridadPerfilId).map(mapper::toDomain);
    }

    @Override
    public Optional<CompraAutoridadPerfil> findByNombre(String nombre) {
        return repository.findByNombre(nombre).map(mapper::toDomain);
    }

    @Override
    public List<CompraAutoridadPerfil> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public CompraAutoridadPerfil save(CompraAutoridadPerfil perfil) {
        CompraAutoridadPerfilEntity saved;
        if (perfil.getAutoridadPerfilId() != null) {
            CompraAutoridadPerfilEntity managed = repository.findByAutoridadPerfilId(perfil.getAutoridadPerfilId())
                    .orElseThrow(() -> new CompraAutoridadPerfilException(perfil.getAutoridadPerfilId()));
            mapper.updateEntity(perfil, managed);
            saved = repository.save(managed);
        } else {
            saved = repository.save(mapper.toEntity(perfil));
        }
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional
    public void deleteByAutoridadPerfilId(Long autoridadPerfilId) {
        try {
            repository.deleteByAutoridadPerfilId(autoridadPerfilId);
            // Fuerza el DELETE dentro de la transacción para capturar aquí el FK de
            // compra_autoridad_usuario si el perfil todavía tiene usuarios asignados.
            repository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new CompraAutoridadPerfilException(
                    "No se puede eliminar el perfil de autoridad: tiene usuarios asignados.");
        }
    }

    @Override
    public boolean existsByAutoridadPerfilId(Long autoridadPerfilId) {
        return repository.existsByAutoridadPerfilId(autoridadPerfilId);
    }

}
