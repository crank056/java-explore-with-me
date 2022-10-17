package ru.practicum.ewmmain.events.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewmmain.categories.model.Category;
import ru.practicum.ewmmain.locations.model.Location;
import ru.practicum.ewmmain.users.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "EVENTS")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String tittle;
    @Column(nullable = false)
    private String annotations;
    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private LocalDateTime created;
    @Column(nullable = false)
    private LocalDateTime event_date;
    private LocalDateTime published;
    @ManyToOne
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    private Location location_id;
    @Column(nullable = false)
    private Boolean paid;
    @Column(nullable = false)
    private Integer participant_limit;
    @Column(nullable = false)
    private Boolean request_moderation;
    private Integer confirmed_requests;
    @ManyToOne
    @JoinColumn(name = "initiator_id", referencedColumnName = "id")
    private User initiator;
    private String state;
    private Long views;
}

