package um.tesoreria.core.hexagonal.track.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.hexagonal.track.application.exception.TrackException;
import um.tesoreria.core.hexagonal.track.application.service.TrackService;
import um.tesoreria.core.hexagonal.track.domain.model.Track;
import um.tesoreria.core.hexagonal.track.infrastructure.web.dto.TrackRequest;
import um.tesoreria.core.hexagonal.track.infrastructure.web.dto.TrackResponse;
import um.tesoreria.core.hexagonal.track.infrastructure.web.mapper.TrackDtoMapper;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/track", "/api/tesoreria/core/track"})
@RequiredArgsConstructor
public class TrackController {

    private final TrackService trackService;
    private final TrackDtoMapper trackDtoMapper;

    @GetMapping("/")
    public ResponseEntity<List<TrackResponse>> findAll() {
        List<TrackResponse> responses = trackService.findAll().stream()
                .map(trackDtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{trackId}")
    public ResponseEntity<TrackResponse> findByTrackId(@PathVariable Long trackId) {
        try {
            var domain = trackService.findByTrackId(trackId);
            return ResponseEntity.ok(trackDtoMapper.toResponse(domain));
        } catch (TrackException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/")
    public ResponseEntity<TrackResponse> add(@RequestBody TrackRequest trackRequest) {
        Track track = trackDtoMapper.toDomain(trackRequest);
        Track createdTrack = trackService.createTrack(track);
        return new ResponseEntity<>(trackDtoMapper.toResponse(createdTrack), HttpStatus.OK);
    }

    @DeleteMapping("/{trackId}")
    public ResponseEntity<Void> deleteByTrackId(@PathVariable Long trackId) {
        trackService.deleteByTrackId(trackId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
