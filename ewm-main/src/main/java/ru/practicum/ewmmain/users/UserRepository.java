package ru.practicum.ewmmain.users;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmmain.users.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
