package ru.practicum.ewmmain.comments.controllers;

import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.comments.CommentService;
import ru.practicum.ewmmain.comments.model.CommentDto;
import ru.practicum.ewmmain.comments.model.NewCommentDto;
import ru.practicum.ewmmain.exceptions.ValidationException;

import java.util.List;

@RestController
@RequestMapping("/admin/comments")
public class CommentsAdminController {

    private final CommentService commentService;

    public CommentsAdminController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public List<CommentDto> getAllComments(@RequestParam(required = false) String text,
                                           @RequestParam(required = false) Long[] users,
                                           @RequestParam(required = false) Long[] events,
                                           @RequestParam(required = false) String start,
                                           @RequestParam(required = false) String end,
                                           @RequestParam(defaultValue = "0") int from,
                                           @RequestParam(defaultValue = "10") int size) {
        return commentService.getAllCommentsAdmin(text, users, events, start, end, from, size);
    }

    @GetMapping("/event/{eventId}")
    public List<CommentDto> getAllEventComments(@PathVariable Long eventId,
                                                @RequestParam(defaultValue = "0") int from,
                                                @RequestParam(defaultValue = "10") int size) {
        return commentService.getAllEventCommentsAdmin(eventId, from, size);
    }

    @GetMapping("/{commentId}")
    public CommentDto getFromId(@PathVariable Long commentId) {
        return commentService.getCommentFromIdAdmin(commentId);
    }

    @PatchMapping("/{commentId}")
    public CommentDto editCommentAdmin(@PathVariable Long commentId, @RequestBody NewCommentDto newCommentDto)
        throws ValidationException {
        return commentService.editCommentAdmin(commentId, newCommentDto);
    }

    @DeleteMapping("/{commentId}")
    public boolean deleteCommentAdmin(@PathVariable Long commentId) {
        return commentService.deleteCommentAdmin(commentId);
    }
}
