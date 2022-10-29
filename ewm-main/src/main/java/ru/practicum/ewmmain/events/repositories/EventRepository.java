package ru.practicum.ewmmain.events.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmmain.events.model.Event;
import ru.practicum.ewmmain.events.model.State;

import java.time.LocalDateTime;

public interface EventRepository extends JpaRepository<Event, Long> {

    Page<Event> findAllByEventDateIsAfter(LocalDateTime start, Pageable page);

    Page<Event> findAllByEventDateIsAfterAndEventDateIsBefore(
        LocalDateTime start, LocalDateTime end, Pageable page);

    Page<Event> findAllByInitiatorId(Long initiatorId, Pageable pageable);

    Page<Event> findAllByEventDateBetweenAndState(
        LocalDateTime start, LocalDateTime end, State state, Pageable page);

    Page<Event> findAllByEventDateIsAfterAndState(LocalDateTime start, State state, Pageable page);
}
