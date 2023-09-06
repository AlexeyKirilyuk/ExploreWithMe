package ru.practicum.user;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDto> getAll(List<Long> ids, Integer from, Integer size) {
        List<UserDto> result = userMapper.toUserDto(getUsersByParams(ids, getPage(from, size)));
        log.info("Найдено {} пользователей.", result.size());
        return result;
    }

    public UserDto create(UserDto userDto) {
        UserDto result = Optional.of(userRepository.save(userMapper.toUser(userDto)))
                .map(userMapper::toUserDto)
                .orElseThrow();
        log.info("Пользователь {} {} создан.", result.getId(), result.getName());
        return result;
    }

    public void deleteById(Long userId) {
        User user = getUserById(userId);
        userRepository.deleteById(user.getId());
        log.info("Пользователь {} удалён.", user.getName());
    }

    private List<User> getUsersByParams(List<Long> ids, Pageable pageable) {
        if (ids == null) {
           return userRepository.findAll(pageable).toList();
        } else {
            return userRepository.findAllByIdIn(ids, pageable);
        }
    }

    public User getUserById(Long userId) {
        User user = userRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException("Пользователь", userId));
        log.info("Пользователь {} найден.", user.getId());
        return user;
    }

    private PageRequest getPage(Integer from, Integer size) {
        if (size <= 0 || from < 0) {
            throw new IllegalArgumentException("Размер страницы не должен быть меньше единицы.");
        }
        return PageRequest.of(from / size, size, Sort.by("id").ascending());
    }
}
