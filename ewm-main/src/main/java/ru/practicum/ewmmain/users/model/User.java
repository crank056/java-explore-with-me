package ru.practicum.ewmmain.users.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewmmain.comments.model.Comment;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String email;
    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(name = "COMMENT_IGNORE_LIST",
        joinColumns = {@JoinColumn(name = "user_id")},
        inverseJoinColumns = {@JoinColumn(name = "speechless_id")})
    private List<User> ignoreList;
    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(name = "RATING_LIST",
        joinColumns = {@JoinColumn(name = "user_id")},
        inverseJoinColumns = {@JoinColumn(name = "comment_id")})
    private List<Comment> ratingList;
}
