package ru.practicum.ewmmain.requests;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmmain.requests.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByEventId(Long eventId);

    List<Request> findAllByRequesterId(Long requesterId);

    Request findByRequesterIdAndEventId(Long requesterId, Long eventId);
}
