package ru.practicum.ewmmain.events.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmmain.events.model.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
}
