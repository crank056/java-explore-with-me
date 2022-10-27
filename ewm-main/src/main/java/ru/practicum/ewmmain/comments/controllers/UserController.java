package ru.practicum.ewmmain.comments.controllers;

import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.comments.CommentService;
import ru.practicum.ewmmain.comments.model.CommentDto;
import ru.practicum.ewmmain.comments.model.NewCommentDto;
import ru.practicum.ewmmain.exceptions.AccessException;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/comments")
public class UserController {

    private final CommentService commentService;

    public UserController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/{eventId}")
    public CommentDto createComment(@PathVariable Long userId, @PathVariable Long eventId,
                                    @RequestBody NewCommentDto newCommentDto) {
        return commentService.createComment(userId, eventId, newCommentDto);
    }

    @PatchMapping("/{commentId}")
    public CommentDto editComment(@PathVariable Long userId, @PathVariable Long commentId,
                                  @RequestBody NewCommentDto newCommentDto) throws AccessException {
        return commentService.editCommentByUser(userId, commentId, newCommentDto);
    }

    @GetMapping
    public List<CommentDto> getAllComments(@PathVariable Long userId, @RequestParam int from,
                                           @RequestParam int size) {
        return commentService.getAllFromUser(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public List<CommentDto> getAllEventComments(@PathVariable Long eventId, @RequestParam int from,
                                                @RequestParam int size) {
        return commentService.getAllEventComments(eventId, from, size);
    }

    @GetMapping("/{commentId}")
    public CommentDto getFromId(@PathVariable Long commentId) {
        return commentService.getCommentFromId(commentId);
    }

    @DeleteMapping("/{commentId}")
    public boolean deleteFromId(@PathVariable Long userId, @PathVariable Long commentId) throws AccessException {
        return commentService.deleteByUser(userId, commentId);
    }
}
