package com.camping.projet.service;

import com.camping.projet.dto.request.ExerciceEvacuationRequest;
import com.camping.projet.dto.response.ExerciceEvacuationResponse;
import java.util.List;

public interface IExerciceEvacuationService {
    ExerciceEvacuationResponse scheduleExercise(ExerciceEvacuationRequest request);

    ExerciceEvacuationResponse recordExerciseResults(Long id, Integer dureeReelle, String pointsPositifs,
            String pointsAmelioration);

    ExerciceEvacuationResponse getExerciseById(Long id);

    List<ExerciceEvacuationResponse> getAllExercises();

    List<ExerciceEvacuationResponse> getUpcomingExercises();

    List<ExerciceEvacuationResponse> getExercisesByResponsable(Long userId);
}
