package com.camping.projet.service.impl;

import com.camping.projet.dto.request.ExerciceEvacuationRequest;
import com.camping.projet.dto.response.ExerciceEvacuationResponse;
import com.camping.projet.entity.ExerciceEvacuation;
import com.camping.projet.mapper.ExerciceEvacuationMapper;
import com.camping.projet.repository.ExerciceEvacuationRepository;
import com.camping.projet.service.IExerciceEvacuationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExerciceEvacuationServiceImpl implements IExerciceEvacuationService {

    private final ExerciceEvacuationRepository exerciceRepository;
    private final ExerciceEvacuationMapper exerciceMapper;

    @Override
    @Transactional
    public ExerciceEvacuationResponse scheduleExercise(ExerciceEvacuationRequest request) {
        ExerciceEvacuation exercice = exerciceMapper.toEntity(request);
        return exerciceMapper.toResponse(exerciceRepository.save(exercice));
    }

    @Override
    @Transactional
    public ExerciceEvacuationResponse recordExerciseResults(Long id, Integer dureeReelle, String pointsPositifs,
            String pointsAmelioration) {
        ExerciceEvacuation exercice = exerciceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exercise not found"));

        exercice.setDureeMinutes(dureeReelle);
        exercice.setPointsPositifs(pointsPositifs);
        exercice.setPointsAmelioration(pointsAmelioration);
        exercice.setReussi(dureeReelle <= exercice.getDureeObjectifMinutes());

        return exerciceMapper.toResponse(exerciceRepository.save(exercice));
    }

    @Override
    public ExerciceEvacuationResponse getExerciseById(Long id) {
        return exerciceRepository.findById(id)
                .map(exerciceMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Exercise not found"));
    }

    @Override
    public List<ExerciceEvacuationResponse> getAllExercises() {
        return exerciceRepository.findAll().stream()
                .map(exerciceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExerciceEvacuationResponse> getUpcomingExercises() {
        return exerciceRepository.findUpcomingExercises(LocalDateTime.now()).stream()
                .map(exerciceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExerciceEvacuationResponse> getExercisesByResponsable(Long userId) {
        return exerciceRepository.findByResponsable(userId).stream()
                .map(exerciceMapper::toResponse)
                .collect(Collectors.toList());
    }
}
