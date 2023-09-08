package ru.practicum.user.service;

import java.util.ArrayList;
import java.util.List;

import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private UserMapper userMapper;

    @Override
    public void deleteUser(Long userId) {
        User result = getUserById(userId);
        userRepository.deleteById(result.getId());
        log.info("Пользователь с id " + userId + "удалён");
    }

    @Override
    public List<UserDto> getUsersInfo(List<Long> ids, int from, int size) {
        PageRequest pagination = PageRequest.of(from / size,
                size);
        List<User> all = new ArrayList<>();
        if (ids == null) {
            all.addAll(userRepository.findAll(pagination).getContent());
        } else {
            all.addAll(userRepository.findAllByIdIn(ids, pagination));
        }
        log.info("Получены все пользователи " + all);
        return userMapper.userListToUserDtoList(all);
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = userMapper.userDtoToUser(userDto);
        try {
            user = userRepository.save(user);
        } catch (Exception e) {
            catchSqlException(e);
        }
        log.info("Пользователь= " + user + " создан.");
        return userMapper.userToUserDto(user);
    }

    private void catchSqlException(Exception e) {
        StringBuilder stringBuilder = new StringBuilder(e.getCause().getCause().getMessage());
        int indexEqualsSign = stringBuilder.indexOf("=");
        stringBuilder.delete(0, indexEqualsSign + 1);
        throw new ConflictException(stringBuilder.toString().replace("\"", "").trim());
    }

    public User getUserById(Long userId) {
        User result = userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId));

        log.info("User {} is found.", result.getId());
        return result;
    }
}