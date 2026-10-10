package um.tesoreria.core.hexagonal.compras.articulo.infrastructure.persistence.adapter;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.articulo.application.exception.ArticuloException;
import um.tesoreria.core.hexagonal.compras.articulo.domain.model.Articulo;
import um.tesoreria.core.hexagonal.compras.articulo.domain.ports.out.ArticuloRepository;
import um.tesoreria.core.hexagonal.compras.articulo.infrastructure.persistence.entity.ArticuloEntity;
import um.tesoreria.core.hexagonal.compras.articulo.infrastructure.persistence.mapper.ArticuloMapper;
import um.tesoreria.core.hexagonal.compras.articulo.infrastructure.persistence.repository.JpaArticuloRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import um.tesoreria.core.hexagonal.compras.articulo.domain.model.ArticuloSearch;
import um.tesoreria.core.hexagonal.compras.articulo.infrastructure.persistence.repository.ArticuloKeyRepository;
import um.tesoreria.core.model.PaginatedResponse;


@Component
@RequiredArgsConstructor
public class JpaArticuloRepositoryAdapter implements ArticuloRepository {

    private final JpaArticuloRepository jpaArticuloRepository;
    private final ArticuloMapper articuloMapper;
    private final ArticuloKeyRepository articuloKeyRepository;
    private final EntityManager entityManager;

    @Override
    public Articulo create(Articulo articulo) {
        ArticuloEntity entity = articuloMapper.toEntity(articulo);
        try {
            // persist inserta siempre (save haría merge y pisaría un id existente); el flush trae el error acá
            entityManager.persist(entity);
            entityManager.flush();
        } catch (RuntimeException ex) {
            throw ArticuloRestricciones.traducir(ex, articulo.getArticuloId(), "alta");
        }
        return articuloMapper.toDomainModel(entity);
    }

    @Override
    public Optional<Articulo> findById(Long id) {
        return jpaArticuloRepository.findById(id).map(articuloMapper::toDomainModel);
    }

    @Override
    public Optional<Articulo> findByIdForUpdate(Long id) {
        // Existencia sin cargar la entidad: un SELECT ... FOR UPDATE de un id inexistente tomaría un bloqueo de brecha
        if (!jpaArticuloRepository.existsById(id)) {
            return Optional.empty();
        }
        // El estado sale de la lectura con bloqueo: en REPEATABLE READ es la única que ve lo último confirmado
        // (una lectura común, como un refresh, devuelve la foto del primer SELECT de la transacción).
        // Nativo porque con PESSIMISTIC_WRITE Hibernate 7 genera "FOR UPDATE OF", que MySQL 5.7 no acepta.
        // Debe ser la primera carga del artículo en la transacción: si ya estuviera en el contexto, Hibernate
        // devolvería la instancia cargada y no la fila leída.
        List<?> filas;
        try {
            filas = entityManager.createNativeQuery("SELECT * FROM articulos WHERE Art_ID = :id FOR UPDATE", ArticuloEntity.class)
                    .setParameter("id", id)
                    .getResultList();
        } catch (RuntimeException ex) {
            throw ArticuloRestricciones.traducir(ex, id, "bloqueo");
        }
        return filas.stream().map(ArticuloEntity.class::cast).findFirst().map(articuloMapper::toDomainModel);
    }

    @Override
    public List<Articulo> findAll() {
        return jpaArticuloRepository.findAll().stream()
                .map(articuloMapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public PaginatedResponse<Articulo> findAllPaginatedByTipo(String tipo, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("nombre").ascending());
        Page<ArticuloEntity> entityPage = jpaArticuloRepository.findAllByTipo(tipo, pageRequest);
        List<Articulo> domainList = entityPage.getContent().stream()
                .map(articuloMapper::toDomainModel)
                .collect(Collectors.toList());
        return new PaginatedResponse<>(domainList, entityPage.getTotalElements(), entityPage.getTotalPages(), entityPage.getNumber(), entityPage.getSize());
    }

    @Override
    public List<ArticuloSearch> findAllByStrings(List<String> conditions) {
        return articuloKeyRepository.findAllByStrings(conditions).stream()
                .map(articuloMapper::toSearchDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Articulo update(Articulo articulo) {
        Long id = articulo.getArticuloId();
        ArticuloEntity entity = jpaArticuloRepository.findById(id).orElseThrow(() -> new ArticuloException(id));
        articuloMapper.copyBusinessFields(articulo, entity);
        try {
            entityManager.flush();
        } catch (RuntimeException ex) {
            throw ArticuloRestricciones.traducir(ex, id, "edición");
        }
        return articuloMapper.toDomainModel(entity);
    }

    @Override
    public void deleteById(Long id) {
        ArticuloEntity entity = jpaArticuloRepository.findById(id).orElseThrow(() -> new ArticuloException(id));
        try {
            entityManager.remove(entity);
            entityManager.flush();
        } catch (RuntimeException ex) {
            throw ArticuloRestricciones.traducir(ex, id, "baja");
        }
    }

    @Override
    public Optional<Articulo> findLast() {
        return jpaArticuloRepository.findTopByOrderByArticuloIdDesc().map(articuloMapper::toDomainModel);
    }
}
