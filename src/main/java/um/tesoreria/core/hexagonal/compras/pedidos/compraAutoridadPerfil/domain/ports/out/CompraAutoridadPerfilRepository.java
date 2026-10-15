package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.domain.ports.out;

import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.domain.model.CompraAutoridadPerfil;

import java.util.List;
import java.util.Optional;

public interface CompraAutoridadPerfilRepository {

    Optional<CompraAutoridadPerfil> findByAutoridadPerfilId(Long autoridadPerfilId);

    Optional<CompraAutoridadPerfil> findByNombre(String nombre);

    List<CompraAutoridadPerfil> findAll();

    CompraAutoridadPerfil save(CompraAutoridadPerfil perfil);

    void deleteByAutoridadPerfilId(Long autoridadPerfilId);

    boolean existsByAutoridadPerfilId(Long autoridadPerfilId);

}
