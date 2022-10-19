package ru.practicum.ewmmain.events.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmmain.events.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    public Page<Event> findAllByEventDateIsAfter(LocalDateTime start, Pageable page);

    public Page<Event> findAllByEventDateIsAfterAndEventDateIsBefore(
        LocalDateTime start, LocalDateTime end, Pageable page);
}
