package ru.practicum.ewmmain.comments.model;

import lombok.*;
import ru.practicum.ewmmain.events.model.Event;
import ru.practicum.ewmmain.users.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "COMMENTS")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private LocalDateTime created;
    @Column(nullable = false)
    private String text;
    @ManyToOne
    @JoinColumn(name = "commentator_id", referencedColumnName = "id")
    private User commentator;
    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    private Event event;
    @Column
    private Long rate;
}
