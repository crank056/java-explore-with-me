package ru.practicum.ewmmain.comments;

import lombok.SneakyThrows;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewmmain.comments.model.Comment;
import ru.practicum.ewmmain.comments.model.CommentDto;
import ru.practicum.ewmmain.comments.model.CommentMapper;
import ru.practicum.ewmmain.comments.model.NewCommentDto;
import ru.practicum.ewmmain.events.repositories.EventRepository;
import ru.practicum.ewmmain.exceptions.AccessException;
import ru.practicum.ewmmain.exceptions.NotFoundException;
import ru.practicum.ewmmain.users.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public CommentService(CommentRepository commentRepository, UserRepository userRepository,
                          EventRepository eventRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    public CommentDto createComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        validateUser(userId);
        validateEvent(eventId);
        Comment comment = CommentMapper.toEntity(newCommentDto);
        comment.setCreated(LocalDateTime.now());
        comment.setCommentator(userRepository.getReferenceById(userId));
        comment.setEvent(eventRepository.getReferenceById(eventId));
        return CommentMapper.toDto(commentRepository.save(comment));
    }

    public CommentDto editCommentByUser(Long userId, Long commentId, NewCommentDto newCommentDto)
        throws AccessException {
        validateUser(userId);
        validateComment(commentId);
        Comment comment = commentRepository.getReferenceById(commentId);
        if (comment.getCommentator().getId() != userId)
            throw new AccessException("Вы не можете редактировать чужой комментарий");
        comment.setDescription(newCommentDto.getDescription());
        return CommentMapper.toDto(commentRepository.save(comment));
    }

    public List<CommentDto> getAllFromUser(Long userId, int from, int size) {
        validateUser(userId);
        Pageable page = PageRequest.of(from / size, size);
        List<Comment> list = commentRepository.findAllByCommentatorId(userId, page).getContent();
        return list.stream().map(CommentMapper::toDto).collect(Collectors.toList());
    }

    public CommentDto getCommentFromId(Long commentId) {
        validateComment(commentId);
        return CommentMapper.toDto(commentRepository.getReferenceById(commentId));
    }

    public List<CommentDto> getAllEventComments(Long eventId, int from, int size) {
        Pageable page = PageRequest.of(from / size, size);
        List<Comment> list = commentRepository.findAllByEventId(eventId, page).getContent();
        return list.stream().map(CommentMapper::toDto).collect(Collectors.toList());
    }

    public boolean deleteByUser(Long userId, Long commentId) throws AccessException {
        validateUser(userId);
        validateComment(commentId);
        if (!commentRepository.getReferenceById(commentId).getCommentator().getId().equals(userId))
            throw new AccessException("Вы не можете удалить чужой комментарий");
        commentRepository.deleteById(commentId);
        return !commentRepository.existsById(commentId);
    }

    public List<CommentDto> getAllCommentsAdmin(
        String text, Long[] users, Long[] events, String startDate, String endDate, int from, int size) {
        List<Comment> list;
        LocalDateTime start;
        LocalDateTime end;
        Pageable page = PageRequest.of(from / size, size);
        if (startDate == null) {
            list = commentRepository.findAll();
        } else {
            start = LocalDateTime.parse(startDate, formatter);
            if (endDate != null) {
                end = LocalDateTime.parse(endDate, formatter);
                list = commentRepository.findAllByCreatedIsAfterAndEventDateIsBefore(start, end, page).getContent();
            } else list = commentRepository.findAllByCreatedIsAfter(start, page).getContent();
        }
        if (users != null) {
            List<Long> usersList = Arrays.asList(users);
            list = list.stream().filter(comment -> usersList.contains(comment.getCommentator().getId()))
                .collect(Collectors.toList());
        }
        if (events != null) {
            List<Long> eventList = Arrays.asList(events);
            list = list.stream().filter(comment -> eventList.contains(comment.getEvent().getId()))
                .collect(Collectors.toList());
        }
        if (text != null) {
            list = list.stream().filter(comment -> comment.getDescription().toLowerCase()
                .contains(text.toLowerCase())).collect(Collectors.toList());
        }
        return list.stream().map(CommentMapper::toDto).collect(Collectors.toList());
    }

    public CommentDto editCommentAdmin(Long commentId, NewCommentDto newCommentDto) {
        validateComment(commentId);
        Comment comment = commentRepository.getReferenceById(commentId);
        comment.setDescription(newCommentDto.getDescription());
        return CommentMapper.toDto(commentRepository.save(comment));
    }

    public boolean deleteCommentAdmin(Long commentId) {
        validateComment(commentId);
        commentRepository.deleteById(commentId);
        return !commentRepository.existsById(commentId);
    }

    @SneakyThrows
    private void validateUser(Long userId) {
        if (!userRepository.existsById(userId)) throw new NotFoundException("Пользователя не существует");
    }

    @SneakyThrows
    private void validateEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) throw new NotFoundException("Мероприятия не существует");
    }

    @SneakyThrows
    private void validateComment(Long commentId) {
        if (!commentRepository.existsById(commentId)) throw new NotFoundException("Комментария не существует");
    }
}
