package ru.practicum.ewmmain.requests.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewmmain.events.model.EventShortDto;
import ru.practicum.ewmmain.users.model.UserShortDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestDto {
        private Long id;
        private LocalDateTime created;
        private EventShortDto event;
        private UserShortDto requester;
        private Boolean status;
    }

