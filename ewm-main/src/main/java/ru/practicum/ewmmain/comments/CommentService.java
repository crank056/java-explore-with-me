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
import ru.practicum.ewmmain.exceptions.ValidationException;
import ru.practicum.ewmmain.users.UserRepository;
import ru.practicum.ewmmain.users.model.User;

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
        comment.setRate(0L);
        return CommentMapper.toDto(commentRepository.save(comment));
    }

    public CommentDto editCommentByUser(Long userId, Long commentId, NewCommentDto newCommentDto)
        throws AccessException, ValidationException {
        validateUser(userId);
        validateComment(commentId);
        Comment comment = commentRepository.getReferenceById(commentId);
        if (!comment.getCommentator().getId().equals(userId)) {
            throw new AccessException("Вы не можете редактировать чужой комментарий");
        }
        if (comment.getRate() != 0) {
            throw new ValidationException("Нельзя изменить оцененный комментарий");
        }
        comment.setText(newCommentDto.getText());
        return CommentMapper.toDto(commentRepository.save(comment));
    }

    public List<CommentDto> getAllFromUser(Long userId, int from, int size) {
        validateUser(userId);
        Pageable page = PageRequest.of(from / size, size);
        List<Comment> list = commentRepository.findAllByCommentatorIdOrderByRate(userId, page).getContent();
        return list.stream()
            .map(CommentMapper::toDto)
            .collect(Collectors.toList());
    }

    public CommentDto getCommentFromIdUser(Long userId, Long commentId) throws AccessException {
        validateComment(commentId);
        validateUser(userId);
        Comment comment = commentRepository.getReferenceById(commentId);
        if (userRepository.getReferenceById(userId).getIgnoreList().contains(comment.getCommentator())) {
            throw new AccessException("Пользователь в игнорм листе");
        }
        return CommentMapper.toDto(commentRepository.getReferenceById(commentId));
    }

    public CommentDto getCommentFromIdAdmin(Long commentId) {
        validateComment(commentId);
        return CommentMapper.toDto(commentRepository.getReferenceById(commentId));
    }

    public List<CommentDto> getAllEventCommentsUser(Long userId, Long eventId, int from, int size) {
        validateUser(userId);
        validateEvent(eventId);
        List<User> ignoreList = userRepository.getReferenceById(userId).getIgnoreList();
        Pageable page = PageRequest.of(from / size, size);
        List<Comment> list = commentRepository.findAllByEventIdOrderByRate(eventId, page).getContent();
        return list.stream()
            .filter(comment -> !ignoreList.contains(comment.getCommentator()))
            .map(CommentMapper::toDto)
            .collect(Collectors.toList());
    }

    public List<CommentDto> getAllEventCommentsAdmin(Long eventId, int from, int size) {
        Pageable page = PageRequest.of(from / size, size);
        List<Comment> list = commentRepository.findAllByEventIdOrderByRate(eventId, page).getContent();
        return list.stream()
            .map(CommentMapper::toDto)
            .collect(Collectors.toList());
    }

    public boolean deleteByUser(Long userId, Long commentId) throws AccessException {
        validateUser(userId);
        validateComment(commentId);
        if (!commentRepository.getReferenceById(commentId).getCommentator().getId().equals(userId)) {
            throw new AccessException("Вы не можете удалить чужой комментарий");
        }
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
                list = commentRepository.findAllByCreatedBetweenOrderByRate(start, end, page).getContent();
            } else {
                list = commentRepository.findAllByCreatedIsAfterOrderByRate(start, page).getContent();
            }
        }
        if (users != null) {
            List<Long> usersList = Arrays.asList(users);
            list = list.stream()
                .filter(comment -> usersList.contains(comment.getCommentator().getId()))
                .collect(Collectors.toList());
        }
        if (events != null) {
            List<Long> eventList = Arrays.asList(events);
            list = list.stream()
                .filter(comment -> eventList.contains(comment.getEvent().getId()))
                .collect(Collectors.toList());
        }
        if (text != null) {
            list = list.stream()
                .filter(comment -> comment.getText().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
        }
        return list.stream()
            .map(CommentMapper::toDto)
            .collect(Collectors.toList());
    }

    public List<CommentDto> getAllEventCommentsPublic(Long eventId, String text,
                                                      Long[] users, String startDate,
                                                      String endDate, int from, int size) {
        List<Comment> commentList;
        LocalDateTime start;
        LocalDateTime end;
        Pageable page = PageRequest.of(from / size, size);
        if (startDate == null) {
            commentList = commentRepository.findAllByEventIdOrderByRate(eventId, page).getContent();
        } else {
            start = LocalDateTime.parse(startDate, formatter);
            if (endDate != null) {
                end = LocalDateTime.parse(endDate, formatter);
                commentList = commentRepository.findAllByEventIdAndCreatedBetweenOrderByRate(
                    eventId, start, end, page).getContent();
            } else {
                commentList = commentRepository.findAllByEventIdAndCreatedIsAfterOrderByRate(
                    eventId, start, page).getContent();
            }
        }
        if (users != null) {
            List<Long> usersList = Arrays.asList(users);
            commentList = commentList.stream()
                .filter(comment -> usersList.contains(comment.getCommentator().getId()))
                .collect(Collectors.toList());
        }
        if (text != null) {
            commentList = commentList.stream()
                .filter(comment -> comment.getText().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
        }
        return commentList.stream()
            .map(CommentMapper::toDto)
            .collect(Collectors.toList());
    }

    public CommentDto editCommentAdmin(Long commentId, NewCommentDto newCommentDto) throws ValidationException {
        validateComment(commentId);
        if (commentRepository.getReferenceById(commentId).getRate() != 0) {
            throw new ValidationException("Нельзя изменить оцененный комментарий");
        }
        Comment comment = commentRepository.getReferenceById(commentId);
        comment.setText(newCommentDto.getText());
        return CommentMapper.toDto(commentRepository.save(comment));
    }

    public boolean deleteCommentAdmin(Long commentId) {
        validateComment(commentId);
        commentRepository.deleteById(commentId);
        return !commentRepository.existsById(commentId);
    }

    public void addPlusToComment(Long userId, Long commentId) throws ValidationException {
        validateUser(userId);
        validateComment(commentId);
        Comment comment = commentRepository.getReferenceById(commentId);
        User user = userRepository.getReferenceById(userId);
        if (comment.getCommentator().getId().equals(userId)) {
            throw new ValidationException("Чувак, нельзя ставить себе плюсы, ну ты чего?!");
        }
        if (user.getRatingList().contains(comment)) {
            throw new ValidationException("Нельзя поставить больше одной оценки");
        }
        comment.setRate(comment.getRate() + 1);
        commentRepository.save(comment);
        user.getRatingList().add(comment);
        userRepository.save(user);
    }

    public void addMinusToComment(Long userId, Long commentId) throws ValidationException {
        validateUser(userId);
        validateComment(commentId);
        Comment comment = commentRepository.getReferenceById(commentId);
        User user = userRepository.getReferenceById(userId);
        if (comment.getCommentator().getId().equals(userId)) {
            throw new ValidationException("Нельзя поставить минус на свой комментарий");
        }
        if (user.getRatingList().contains(comment)) {
            throw new ValidationException("Нельзя поставить больше одной оценки");
        }
        comment.setRate(comment.getRate() - 1);
        commentRepository.save(comment);
        user.getRatingList().add(comment);
        userRepository.save(user);
    }

    public void addToIgnoreList(Long userId, Long ignoredId) {
        validateUser(userId);
        validateUser(ignoredId);
        User user = userRepository.getReferenceById(userId);
        List<User> ignoredList = user.getIgnoreList();
        ignoredList.add(userRepository.getReferenceById(ignoredId));
        user.setIgnoreList(ignoredList);
        userRepository.save(user);
    }

    public void deleteFromIgnoreList(Long userId, Long ignoredId) {
        validateUser(userId);
        validateUser(ignoredId);
        User user = userRepository.getReferenceById(userId);
        List<User> ignoredList = user.getIgnoreList();
        ignoredList.remove(userRepository.getReferenceById(ignoredId));
        user.setIgnoreList(ignoredList);
        userRepository.save(user);
    }

    @SneakyThrows
    private void validateUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователя не существует");
        }
    }

    @SneakyThrows
    private void validateEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Мероприятия не существует");
        }
    }

    @SneakyThrows
    private void validateComment(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new NotFoundException("Комментария не существует");
        }
    }
}
