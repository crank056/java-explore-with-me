package ru.practicum.ewmmain.events.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmmain.events.model.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
