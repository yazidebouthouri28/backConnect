package com.camping.projet.controller;

import com.camping.projet.dto.request.ExerciceEvacuationRequest;
import com.camping.projet.dto.response.ExerciceEvacuationResponse;
import com.camping.projet.service.IExerciceEvacuationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
public class ExerciceEvacuationController {

    private final IExerciceEvacuationService exerciceService;

    @PostMapping("/schedule")
    public ResponseEntity<ExerciceEvacuationResponse> schedule(@Valid @RequestBody ExerciceEvacuationRequest request) {
        return new ResponseEntity<>(exerciceService.scheduleExercise(request), HttpStatus.CREATED);
    }

    @PostMapping("/{id}/results")
    public ResponseEntity<ExerciceEvacuationResponse> recordResults(
            @PathVariable Long id,
            @RequestParam Integer duree,
            @RequestParam String positif,
            @RequestParam String amelio) {
        return ResponseEntity.ok(exerciceService.recordExerciseResults(id, duree, positif, amelio));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExerciceEvacuationResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(exerciceService.getExerciseById(id));
    }

    @GetMapping
    public ResponseEntity<List<ExerciceEvacuationResponse>> getAll() {
        return ResponseEntity.ok(exerciceService.getAllExercises());
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<ExerciceEvacuationResponse>> getUpcoming() {
        return ResponseEntity.ok(exerciceService.getUpcomingExercises());
    }

    @GetMapping("/responsable/{userId}")
    public ResponseEntity<List<ExerciceEvacuationResponse>> getByResponsable(@PathVariable Long userId) {
        return ResponseEntity.ok(exerciceService.getExercisesByResponsable(userId));
    }
}
