package ru.practicum.ewmmain.comments.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private long id;
    private String created;
    private String description;
    private long commentatorId;
    private long eventId;
}
