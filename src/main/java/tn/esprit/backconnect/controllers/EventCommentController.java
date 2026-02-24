package tn.esprit.backconnect.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.backconnect.entities.EventComment;
import tn.esprit.backconnect.services.IEventCommentService;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Tag(name = "Comments", description = "Event comment management APIs")
public class EventCommentController {

    private final IEventCommentService eventCommentService;

    @GetMapping
    @Operation(summary = "Get all comments")
    public ResponseEntity<List<EventComment>> getAllComments() {
        return ResponseEntity.ok(eventCommentService.getAllComments());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get comment by ID")
    public ResponseEntity<EventComment> getCommentById(@PathVariable Long id) {
        return ResponseEntity.ok(eventCommentService.getCommentById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new comment")
    public ResponseEntity<EventComment> createComment(@RequestBody EventComment comment) {
        return new ResponseEntity<>(eventCommentService.createComment(comment), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a comment")
    public ResponseEntity<EventComment> updateComment(@PathVariable Long id, @RequestBody EventComment comment) {
        return ResponseEntity.ok(eventCommentService.updateComment(id, comment));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a comment")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        eventCommentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/event/{eventId}")
    @Operation(summary = "Get comments by event")
    public ResponseEntity<List<EventComment>> getCommentsByEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventCommentService.getCommentsByEvent(eventId));
    }

    @GetMapping("/participant/{participantId}")
    @Operation(summary = "Get comments by participant")
    public ResponseEntity<List<EventComment>> getCommentsByParticipant(@PathVariable Long participantId) {
        return ResponseEntity.ok(eventCommentService.getCommentsByParticipant(participantId));
    }
}
