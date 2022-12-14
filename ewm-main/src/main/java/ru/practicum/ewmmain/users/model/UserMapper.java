package ru.practicum.ewmmain.users.model;

import java.util.ArrayList;

public class UserMapper {

    public static UserDto toUserDto(User user) {
        return new UserDto(
            user.getId(),
            user.getName(),
            user.getEmail()
        );
    }

    public static User toUser(UserDto userDto) {
        return new User(
            userDto.getId(),
            userDto.getName(),
            userDto.getEmail(),
            new ArrayList<>(),
            new ArrayList<>()
        );
    }

    public static UserShortDto toUserShortDto(User user) {
        return new UserShortDto(
            user.getId(),
            user.getName()
        );
    }
}


