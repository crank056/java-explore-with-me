package ru.practicum.ewmmain.comments.controllers;

import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.comments.CommentService;
import ru.practicum.ewmmain.comments.model.CommentDto;
import ru.practicum.ewmmain.comments.model.NewCommentDto;

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
                                  @RequestBody NewCommentDto newCommentDto) {
        return commentService.editCommentByUser(userId, commentId, newCommentDto);
    }

    @GetMapping
    public List<CommentDto> getAllComments(@PathVariable Long userId, @RequestParam int from,
                                           @RequestParam int size) {
        return commentService.getAllFromUser(userId, from, size);
    }

    @GetMapping("/{commentId}")
    public CommentDto getFromId(@PathVariable Long userId, @PathVariable Long commentId) {
        return commentService.getFromIdByUser(userId, commentId);
    }

    @DeleteMapping("/{commentId}")
    public boolean deleteFromId(@PathVariable Long userId, @PathVariable Long commentId) {
        return commentService.deleteByUser(userId, commentId);
    }
}
