package ru.practicum.ewmmain.comments;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmmain.comments.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
