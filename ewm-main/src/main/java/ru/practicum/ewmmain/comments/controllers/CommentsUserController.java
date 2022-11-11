package ru.practicum.ewmmain.comments.controllers;

import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.comments.CommentService;
import ru.practicum.ewmmain.comments.model.CommentDto;
import ru.practicum.ewmmain.comments.model.NewCommentDto;
import ru.practicum.ewmmain.exceptions.AccessException;
import ru.practicum.ewmmain.exceptions.ValidationException;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/comments")
public class CommentsUserController {

    private final CommentService commentService;

    public CommentsUserController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/{eventId}")
    public CommentDto createComment(@PathVariable Long userId, @PathVariable Long eventId,
                                    @RequestBody NewCommentDto newCommentDto) {
        return commentService.createComment(userId, eventId, newCommentDto);
    }

    @PatchMapping("/{commentId}")
    public CommentDto editComment(@PathVariable Long userId, @PathVariable Long commentId,
                                  @RequestBody NewCommentDto newCommentDto)
        throws AccessException, ValidationException {
        return commentService.editCommentByUser(userId, commentId, newCommentDto);
    }

    @GetMapping
    public List<CommentDto> getAllComments(@PathVariable Long userId, @RequestParam int from,
                                           @RequestParam int size) {
        return commentService.getAllFromUser(userId, from, size);
    }

    @GetMapping("/event/{eventId}")
    public List<CommentDto> getAllEventComments(@PathVariable Long userId, @PathVariable Long eventId,
                                                @RequestParam int from, @RequestParam int size) {
        return commentService.getAllEventCommentsUser(userId, eventId, from, size);
    }

    @GetMapping("/{commentId}")
    public CommentDto getFromId(@PathVariable Long userId, @PathVariable Long commentId) throws AccessException {
        return commentService.getCommentFromIdUser(userId, commentId);
    }

    @DeleteMapping("/{commentId}")
    public boolean deleteFromId(@PathVariable Long userId, @PathVariable Long commentId) throws AccessException {
        return commentService.deleteByUser(userId, commentId);
    }

    @PatchMapping("/{commentId}/plus")
    public void addPlusToComment(@PathVariable Long userId, @PathVariable Long commentId)
        throws ValidationException {
        commentService.addPlusToComment(userId, commentId);
    }

    @PatchMapping("/{commentId}/minus")
    public void addMinusToComment(@PathVariable Long userId, @PathVariable Long commentId)
        throws ValidationException {
        commentService.addMinusToComment(userId, commentId);
    }

    @PatchMapping("/ignore/{ignoredId}")
    public void addToIgnoreList(@PathVariable Long userId, @PathVariable Long ignoredId) {
        commentService.addToIgnoreList(userId, ignoredId);
    }

    @PatchMapping("/unIgnore/{ignoredId}")
    public void deleteFromIgnoreList(@PathVariable Long userId, @PathVariable Long ignoredId) {
        commentService.deleteFromIgnoreList(userId, ignoredId);
    }
}
