package ru.practicum.ewmmain.requests;

import org.springframework.stereotype.Service;
import ru.practicum.ewmmain.events.model.Event;
import ru.practicum.ewmmain.events.model.State;
import ru.practicum.ewmmain.events.repositories.EventRepository;
import ru.practicum.ewmmain.exceptions.AccessException;
import ru.practicum.ewmmain.exceptions.NotFoundException;
import ru.practicum.ewmmain.exceptions.ValidationException;
import ru.practicum.ewmmain.requests.model.*;
import ru.practicum.ewmmain.users.UserRepository;
import ru.practicum.ewmmain.users.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RequestService {

    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public RequestService(RequestRepository requestRepository, EventRepository eventRepository,
                          UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    public List<ParticipationRequestDto> getAllRequestFromUserEvent(Long userId, Long eventId)
            throws AccessException, NotFoundException {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователя не существует");
        }
        if (eventRepository.getReferenceById(eventId).getInitiator().getId() != userId) {
            throw new AccessException("Вы не создатель мероприятия");
        }
        List<Request> requestList = requestRepository.findAllByEventId(eventId);
        return requestList.stream()
                .map(RequestMapper::toPRDto)
                .collect(Collectors.toList());
    }

    public ParticipationRequestDto confirmRequest(Long userId, Long eventId, Long reqId)
            throws NotFoundException, AccessException, ValidationException {
        if (!requestRepository.existsById(reqId)) {
            throw new NotFoundException("Запроса не существует");
        }
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Мероприятия не существует");
        }
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователя не существует");
        }
        if (eventRepository.getReferenceById(eventId).getInitiator().getId() != userId) {
            throw new AccessException("Вы не создатель мероприятия");
        }
        Event event = eventRepository.getReferenceById(eventId);
        Request request = requestRepository.getReferenceById(reqId);
        if (event.getParticipantLimit() == 0 || event.getRequestModeration().equals(false)) {
            throw new ValidationException("Мероприятие не требует подтверждения заявки");
        }
        if (event.getParticipantLimit() >= event.getConfirmedRequests()) {
            request.setStatus(Status.CANCELED);
        }
        request.setStatus(Status.CONFIRMED);
        event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        eventRepository.save(event);
        return RequestMapper.toPRDto(requestRepository.save(request));
    }

    public ParticipationRequestDto rejectRequest(Long userId, Long eventId, Long reqId)
            throws AccessException, NotFoundException {
        if (!requestRepository.existsById(reqId)) {
            throw new NotFoundException("Запроса не существует");
        }
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Мероприятия не существует");
        }
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователя не существует");
        }
        if (eventRepository.getReferenceById(eventId).getInitiator().getId() != userId) {
            throw new AccessException("Вы не создатель мероприятия");
        }
        Request request = requestRepository.getReferenceById(reqId);
        request.setStatus(Status.REJECTED);
        return RequestMapper.toPRDto(requestRepository.save(request));
    }

    public List<ParticipationRequestDto> getAllUserRequest(Long userId) throws NotFoundException {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователя не существует");
        }
        List<Request> requestList = requestRepository.findAllByRequesterId(userId);
        return requestList.stream()
                .map(RequestMapper::toPRDto)
                .collect(Collectors.toList());
    }

    public ParticipationRequestDto createRequest(Long userId, Long eventId) throws AccessException, NotFoundException {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователя не существует");
        }
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Мероприятия не существует");
        }
        if (requestRepository.findByRequesterIdAndEventId(userId, eventId) != null) {
            throw new AccessException(
                    "Нельзя дважды добавить запрос");
        }
        Event event = eventRepository.getReferenceById(eventId);
        User user = userRepository.getReferenceById(userId);
        if (event.getInitiator().getId().equals(userId)) {
            throw new AccessException(
                    "Нельзя добавить запрос на участие в своем мероприятии");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new AccessException("Мероприятие не опубликовано");
        }
        if (event.getParticipantLimit() <= event.getConfirmedRequests()) {
            throw new AccessException(
                    "Достигнут лимит участников");
        }
        Request request = new Request();
        if (!event.getRequestModeration()) {
            request.setStatus(Status.CONFIRMED);
        } else {
            request.setStatus(Status.PENDING);
        }
        request.setCreated(LocalDateTime.now());
        request.setRequester(user);
        request.setEvent(event);
        return RequestMapper.toPRDto(requestRepository.save(request));
    }

    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) throws AccessException,
            NotFoundException {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователя не существует");
        }
        if (!requestRepository.existsById(requestId)) {
            throw new NotFoundException("Запроса не существует");
        }
        Request request = requestRepository.getReferenceById(requestId);
        Event event = eventRepository.getReferenceById(request.getEvent().getId());
        if (request.getRequester().getId() != userId) {
            throw new AccessException("Запрос не принадлежит пользователю");
        }
        request.setStatus(Status.CANCELED);
        event.setConfirmedRequests(event.getConfirmedRequests() - 1);
        eventRepository.save(event);
        return RequestMapper.toPRDto(requestRepository.save(request));
    }
}
