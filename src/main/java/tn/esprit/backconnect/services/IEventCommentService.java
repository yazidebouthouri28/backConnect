package tn.esprit.backconnect.services;

import tn.esprit.backconnect.entities.EventComment;

import java.util.List;

public interface IEventCommentService {
    List<EventComment> getAllComments();

    EventComment getCommentById(Long id);

    EventComment createComment(EventComment comment);

    EventComment updateComment(Long id, EventComment comment);

    void deleteComment(Long id);

    List<EventComment> getCommentsByEvent(Long eventId);

    List<EventComment> getCommentsByParticipant(Long participantId);
}
