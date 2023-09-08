package ru.practicum.user.service;

import ru.practicum.user.dto.UserDto;

import java.util.List;

public interface UserService {

    void deleteUser(Long userId);

    List<UserDto> getUsersInfo(List<Long> ids, int from, int size);

    UserDto createUser(UserDto userDto);
}
