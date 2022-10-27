package ru.practicum.ewmmain.comments.controllers;

import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.comments.CommentService;
import ru.practicum.ewmmain.comments.model.CommentDto;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class PublicController {

    private final CommentService commentService;

    public PublicController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/{eventId}")
    public List<CommentDto> getAllComments(@PathVariable Long eventId,
                                           @RequestParam(required = false) String text,
                                           @RequestParam(required = false) Long[] users,
                                           @RequestParam(required = false) String start,
                                           @RequestParam(required = false) String end,
                                           @RequestParam(defaultValue = "0") int from,
                                           @RequestParam(defaultValue = "10") int size) {
        return commentService.getAllEventCommentsPublic(eventId, text, users, start, end, from, size);
    }
}
