package tn.esprit.backconnect.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.backconnect.entities.EventComment;
import tn.esprit.backconnect.repositories.EventCommentRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventCommentServiceImpl implements IEventCommentService {

    private final EventCommentRepository eventCommentRepository;

    @Override
    public List<EventComment> getAllComments() {
        return eventCommentRepository.findAll();
    }

    @Override
    public EventComment getCommentById(Long id) {
        return eventCommentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + id));
    }

    @Override
    public EventComment createComment(EventComment comment) {
        comment.setPublicationDate(LocalDateTime.now());
        return eventCommentRepository.save(comment);
    }

    @Override
    public EventComment updateComment(Long id, EventComment comment) {
        EventComment existing = getCommentById(id);
        existing.setContent(comment.getContent());
        existing.setRating(comment.getRating());
        return eventCommentRepository.save(existing);
    }

    @Override
    public void deleteComment(Long id) {
        eventCommentRepository.deleteById(id);
    }

    @Override
    public List<EventComment> getCommentsByEvent(Long eventId) {
        return eventCommentRepository.findByEventId(eventId);
    }

    @Override
    public List<EventComment> getCommentsByParticipant(Long participantId) {
        return eventCommentRepository.findByParticipantUserId(participantId);
    }
}
