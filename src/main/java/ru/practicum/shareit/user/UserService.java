package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.dto.UserDto;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userStorage;

    public UserDto getUserById(Long id) {
        log.info("Получение пользователя: id={}", id);
        if (id == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        try {
            return UserMapper.toUserDto(userStorage.getReferenceById(id));
        } catch (EntityNotFoundException e) {
            log.info("Пользователь не получен, пользователь не найден: id={}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователя с id = " + id + " не существует!");
        }
    }

    public User addUser(User user) {
        log.info("Создание пользователя: email={}", user.getEmail());
        try {
            return userStorage.save(user);
        } catch (DataIntegrityViolationException e) {
            log.info("Пользователь не создан, пользователь уже существует: email={}", user.getEmail());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Пользователь с E-mail " + user.getEmail() +
                    " уже существует!");
        }
    }

    public User refreshUser(Long id, UserDto user) {
        log.info("Обновление пользователя: id={}", id);
        if (id == null || user == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        if (user.getId() == null) {
            user.setId(id);
        }
        User userFromDb;
        try {
            userFromDb = userStorage.getReferenceById(id);
            if ((user.getEmail() != null) && (user.getEmail() != userFromDb.getEmail()))
                userFromDb.setEmail(user.getEmail());
            if (user.getName() != null) userFromDb.setName(user.getName());
        } catch (EntityNotFoundException e) {
            log.info("Обновление пользователя, пользователь не найден: id={}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователя с id = " + id + " не существует!");
        }
        try {
            return userStorage.save(userFromDb);
        } catch (DataIntegrityViolationException e) {
            log.info("Обновление пользователя, пользователь c таким e-mail уже существует: id={}, e-mail={}",
                    userFromDb.getEmail());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Пользователь с E-mail " + user.getEmail() +
                    " уже существует!");
        }
    }

    public void deleteUser(Long id) {
        log.info("Удаление пользователя: id={}", id);
        try {
            userStorage.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            log.info("Удаление пользователя, пользователь не найден: id={}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователя с id = " + id + " не существует!");
        }
    }

    public List<User> getAll() {
        return userStorage.findAll();
    }
}
