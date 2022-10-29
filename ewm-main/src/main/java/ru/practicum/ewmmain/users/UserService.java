package ru.practicum.ewmmain.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewmmain.exceptions.ValidationException;
import ru.practicum.ewmmain.users.model.User;
import ru.practicum.ewmmain.users.model.UserDto;
import ru.practicum.ewmmain.users.model.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto createUser(UserDto userDto) {
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
    }

    public List<UserDto> getUsers(List<Long> ids, int from, int size) throws ValidationException {
        validatePageSize(from, size);
        Pageable page = PageRequest.of(from / size, size, Sort.by("id").descending());
        List<User> users;
        if (ids.size() > 0) {
            users = userRepository.findAllById(ids);
        } else {
            users = userRepository.findAll(page).getContent();
        }
        return users.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public boolean deleteUser(Long userId) {
        userRepository.deleteById(userId);
        return !userRepository.existsById(userId);
    }

    private void validatePageSize(int from, int size) throws ValidationException {
        if (from < 0 || size < 1) {
            throw new ValidationException("Неверные значения формата");
        }
    }
}
