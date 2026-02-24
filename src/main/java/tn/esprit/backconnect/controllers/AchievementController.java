package tn.esprit.backconnect.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.backconnect.entities.Achievement;
import tn.esprit.backconnect.entities.BadgeLevel;
import tn.esprit.backconnect.services.IAchievementService;

import java.util.List;

@RestController
@RequestMapping("/api/achievements")
@RequiredArgsConstructor
@Tag(name = "Achievements", description = "Gamification achievement management APIs")
public class AchievementController {

    private final IAchievementService achievementService;

    @GetMapping
    @Operation(summary = "Get all achievements")
    public ResponseEntity<List<Achievement>> getAllAchievements() {
        return ResponseEntity.ok(achievementService.getAllAchievements());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get achievement by ID")
    public ResponseEntity<Achievement> getAchievementById(@PathVariable Long id) {
        return ResponseEntity.ok(achievementService.getAchievementById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new achievement")
    public ResponseEntity<Achievement> createAchievement(@RequestBody Achievement achievement) {
        return new ResponseEntity<>(achievementService.createAchievement(achievement), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an achievement")
    public ResponseEntity<Achievement> updateAchievement(@PathVariable Long id, @RequestBody Achievement achievement) {
        return ResponseEntity.ok(achievementService.updateAchievement(id, achievement));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an achievement")
    public ResponseEntity<Void> deleteAchievement(@PathVariable Long id) {
        achievementService.deleteAchievement(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/level/{badgeLevel}")
    @Operation(summary = "Get achievements by badge level")
    public ResponseEntity<List<Achievement>> getAchievementsByBadgeLevel(@PathVariable BadgeLevel badgeLevel) {
        return ResponseEntity.ok(achievementService.getAchievementsByBadgeLevel(badgeLevel));
    }
}
