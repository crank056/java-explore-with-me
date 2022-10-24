package ru.practicum.ewmmain.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.exceptions.ValidationException;
import ru.practicum.ewmmain.users.model.UserDto;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }

    @GetMapping
    public List<UserDto> getUsers(
        @RequestBody List<Long> ids,
        @RequestParam(required = false, defaultValue = "0") int from,
        @RequestParam(required = false, defaultValue = "10") int size) throws ValidationException {
        return userService.getUsers(ids, from, size);
    }

    @DeleteMapping("/{userId}")
    public boolean deleteUser(@PathVariable Long userId) {
        return userService.deleteUser(userId);
    }
}
