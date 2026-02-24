package tn.esprit.backconnect.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tn.esprit.backconnect.entities.*;
import tn.esprit.backconnect.repositories.*;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final OrganizerRepository organizerRepository;
    private final ParticipantRepository participantRepository;
    private final EventRepository eventRepository;

    @Override
    public void run(String... args) throws Exception {
        if (organizerRepository.count() == 0) {
            log.info("Initializing sample data...");

            // Create Sample Organizer
            Organizer organizer = new Organizer();
            organizer.setUsername("org1");
            organizer.setEmail("org@test.tn");
            organizer.setPassword("pass");
            organizer.setRole(Role.ORGANIZER);
            organizer = organizerRepository.save(organizer);

            // Create Sample Event
            Event event = new Event();
            event.setTitle("Camp Connect 2026");
            event.setDescription("Annual camping event");
            event.setLocation("Zaghouan");
            event.setStartDate(LocalDateTime.now().plusDays(7));
            event.setEndDate(LocalDateTime.now().plusDays(10));
            event.setStatus(EventStatus.PUBLISHED);
            event.setOrganizer(organizer);
            eventRepository.save(event);

            // Create Sample Participant
            Participant participant = new Participant();
            participant.setUsername("user1");
            participant.setEmail("user@test.tn");
            participant.setPassword("pass");
            participant.setRole(Role.CAMPER);
            participant.setXpPoints(100);
            participantRepository.save(participant);

            log.info("Sample data initialized:");
            log.info(" - Organizer created with userId: {}", organizer.getUserId());
            log.info(" - Event created with id: {}", event.getId());
            log.info(" - Participant created with userId: {}", participant.getUserId());
        } else {
            log.info("Data already exists. IDs found:");
            organizerRepository.findAll().stream().findFirst()
                    .ifPresent(o -> log.info(" - Organizer userId: {}", o.getUserId()));
            eventRepository.findAll().stream().findFirst().ifPresent(e -> log.info(" - Event id: {}", e.getId()));
            participantRepository.findAll().stream().findFirst()
                    .ifPresent(p -> log.info(" - Participant userId: {}", p.getUserId()));
        }
    }
}
