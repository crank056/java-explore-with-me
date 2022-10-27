package ru.practicum.ewmmain.comments.controllers;

import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.comments.CommentService;
import ru.practicum.ewmmain.comments.model.CommentDto;
import ru.practicum.ewmmain.comments.model.NewCommentDto;

import java.util.List;

@RestController
@RequestMapping("/admin/comments")
public class AdminController {

    private final CommentService commentService;

    public AdminController(CommentService commentService) {
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

    @GetMapping("/{eventId}")
    public CommentDto getAllEventComments(@PathVariable Long eventId) {
        return commentService.getAllEventCommentsAdmin(eventId);
    }

    @PatchMapping("/{commentId}")
    public CommentDto editCommentAdmin(@PathVariable Long commentId, @RequestBody NewCommentDto newCommentDto) {
        return commentService.editCommentAdmin(eventId, newCommentDto);
    }

    @DeleteMapping("/{commentId}")
    public boolean deleteCommentAdmin(@PathVariable Long commentId) {
        return commentService.deleteCommentAdmin(commentId);
    }
}
