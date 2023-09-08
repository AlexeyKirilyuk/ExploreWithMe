package ru.practicum.user.mapper;

import java.util.List;

import ru.practicum.user.model.User;
import ru.practicum.user.dto.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto userToUserDto(User user);

    User userDtoToUser(UserDto userDto);

    List<UserDto> userListToUserDtoList(List<User> all);
}
