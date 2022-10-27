package ru.practicum.ewmmain.comments;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmmain.comments.model.Comment;

import java.time.LocalDateTime;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findAllByCommentatorId(Long commentatorId, Pageable page);

    Page<Comment> findAllByCreatedIsAfterAndEventDateIsBefore(LocalDateTime start, LocalDateTime end, Pageable page);

    Page<Comment> findAllByCreatedIsAfter(LocalDateTime start, Pageable page);

    Page<Comment> findAllByEventId(Long eventId, Pageable page);
}
