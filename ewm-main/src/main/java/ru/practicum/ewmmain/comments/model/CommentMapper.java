package ru.practicum.ewmmain.comments.model;

import java.time.format.DateTimeFormatter;

public class CommentMapper {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static CommentDto toDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getCreated().format(formatter),
                comment.getText(),
                comment.getCommentator().getId(),
                comment.getEvent().getId(),
                comment.getRate());
    }

    public static Comment toEntity(NewCommentDto newCommentDto) {
        Comment comment = new Comment();
        comment.setText(newCommentDto.getText());
        return comment;
    }
}
