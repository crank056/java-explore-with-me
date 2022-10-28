package ru.practicum.ewmmain.comments;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmmain.comments.model.Comment;

import java.time.LocalDateTime;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findAllByCommentatorIdOrderByRate(Long commentatorId, Pageable page);

    Page<Comment> findAllByCreatedBetweenOrderByRate(LocalDateTime start, LocalDateTime end, Pageable page);

    Page<Comment> findAllByCreatedIsAfterOrderByRate(LocalDateTime start, Pageable page);

    Page<Comment> findAllByEventIdOrderByRate(Long eventId, Pageable page);

    Page<Comment> findAllByEventIdAndCreatedBetweenOrderByRate(Long eventId, LocalDateTime start, LocalDateTime end, Pageable page);

    Page<Comment> findAllByEventIdAndCreatedIsAfterOrderByRate(Long eventId, LocalDateTime start, Pageable page);
}
