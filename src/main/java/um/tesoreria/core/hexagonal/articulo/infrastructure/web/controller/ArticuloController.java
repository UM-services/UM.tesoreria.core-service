package um.tesoreria.core.hexagonal.articulo.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import um.tesoreria.core.hexagonal.articulo.application.service.ArticuloService;
import um.tesoreria.core.hexagonal.articulo.domain.model.Articulo;
import um.tesoreria.core.hexagonal.articulo.infrastructure.web.dto.ArticuloRequest;
import um.tesoreria.core.hexagonal.articulo.infrastructure.web.dto.ArticuloSearchResponse;
import um.tesoreria.core.hexagonal.articulo.infrastructure.web.dto.ArticuloResponse;
import um.tesoreria.core.hexagonal.articulo.infrastructure.web.mapper.ArticuloDtoMapper;

import java.util.List;
import java.util.stream.Collectors;
import um.tesoreria.core.model.PaginatedResponse;


@RestController
@RequestMapping({"/articulo", "/api/tesoreria/core/articulo"})
@RequiredArgsConstructor
public class ArticuloController {

    private final ArticuloService articuloService;
    private final ArticuloDtoMapper articuloDtoMapper;
    

    @PostMapping("/")
    public ResponseEntity<ArticuloResponse> createArticulo(@RequestBody ArticuloRequest articuloRequest) {
        Articulo articulo = articuloDtoMapper.toDomain(articuloRequest);
        Articulo createdArticulo = articuloService.createArticulo(articulo);
        return new ResponseEntity<>(articuloDtoMapper.toResponse(createdArticulo), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticuloResponse> getArticuloById(@PathVariable Long id) {
        return articuloService.getArticuloById(id)
                .map(articulo -> new ResponseEntity<>(articuloDtoMapper.toResponse(articulo), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/search")
    public ResponseEntity<List<ArticuloSearchResponse>> findByStrings(@RequestBody List<String> conditions) {
        List<ArticuloSearchResponse> responses = articuloService.searchArticulos(conditions).stream()
                .map(articuloDtoMapper::toSearchResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping("/")
    public ResponseEntity<List<ArticuloResponse>> getAllArticulos() {
        List<ArticuloResponse> responses = articuloService.getAllArticulos().stream()
                .map(articuloDtoMapper::toResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping("/tipo/{tipo}/page")
    public ResponseEntity<PaginatedResponse<ArticuloResponse>> getPaginatedByTipo(
            @PathVariable String tipo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
            
        PaginatedResponse<Articulo> domainPage = articuloService.getPaginatedArticulosByTipo(tipo, page, size);
        
        List<ArticuloResponse> responseList = domainPage.getData().stream()
                .map(articuloDtoMapper::toResponse)
                .collect(Collectors.toList());
                
        PaginatedResponse<ArticuloResponse> paginatedResponse = new PaginatedResponse<>(
                responseList,
                domainPage.getTotalElements(),
                domainPage.getTotalPages(),
                domainPage.getCurrentPage(),
                domainPage.getPageSize()
        );
        
        return new ResponseEntity<>(paginatedResponse, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticuloResponse> updateArticulo(@PathVariable Long id, @RequestBody ArticuloRequest articuloRequest) {
        Articulo articulo = articuloDtoMapper.toDomain(articuloRequest);
        return articuloService.updateArticulo(id, articulo)
                .map(updatedArticulo -> new ResponseEntity<>(articuloDtoMapper.toResponse(updatedArticulo), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticulo(@PathVariable Long id) {
        boolean deleted = articuloService.deleteArticulo(id);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/new")
    public ResponseEntity<ArticuloResponse> getNewArticulo() {
        return new ResponseEntity<>(articuloDtoMapper.toResponse(articuloService.getNewArticulo()), HttpStatus.OK);
    }
}
