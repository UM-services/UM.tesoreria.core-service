package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.domain.ports.out;

import java.util.List;

public interface CompraAutoridadUsuarioRepository {

    List<Long> findPerfilIdsByUsuarioId(Integer usuarioId);

    void asignar(Integer usuarioId, Long autoridadPerfilId);

    void quitar(Integer usuarioId, Long autoridadPerfilId);

    boolean existe(Integer usuarioId, Long autoridadPerfilId);

}
