package tn.esprit.projetPi.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import tn.esprit.projetPi.entities.Reservation;
import tn.esprit.projetPi.entities.ReservationStatus;
import tn.esprit.projetPi.entities.ReservationType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends MongoRepository<Reservation, String> {
    
    List<Reservation> findByUserId(String userId);
    
    List<Reservation> findByUserIdAndType(String userId, ReservationType type);
    
    List<Reservation> findByCampsiteId(String campsiteId);
    
    List<Reservation> findByEventId(String eventId);
    
    List<Reservation> findByStatus(ReservationStatus status);
    
    List<Reservation> findByUserIdAndStatus(String userId, ReservationStatus status);
    
    Optional<Reservation> findByConfirmationCode(String confirmationCode);
    
    @Query("{'campsiteId': ?0, 'status': {$in: ['PENDING', 'CONFIRMED']}, '$or': [{'checkInDate': {$lte: ?2, $gte: ?1}}, {'checkOutDate': {$lte: ?2, $gte: ?1}}, {'$and': [{'checkInDate': {$lte: ?1}}, {'checkOutDate': {$gte: ?2}}]}]}")
    List<Reservation> findConflictingReservations(String campsiteId, LocalDate checkIn, LocalDate checkOut);
    
    @Query("{'eventId': ?0, 'status': {$in: ['PENDING', 'CONFIRMED']}}")
    List<Reservation> findActiveEventReservations(String eventId);
    
    @Query(value = "{'eventId': ?0, 'status': 'CONFIRMED'}", count = true)
    Long countConfirmedEventReservations(String eventId);
    
    List<Reservation> findByCheckInDate(LocalDate date);
    
    List<Reservation> findByCheckOutDate(LocalDate date);
    
    @Query("{'userId': ?0, 'type': 'EVENT', 'eventId': ?1, 'status': {$in: ['PENDING', 'CONFIRMED']}}")
    Optional<Reservation> findUserEventRegistration(String userId, String eventId);
}
