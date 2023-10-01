package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.dto.UserDto;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userStorage;

    public UserDto getUserById(Long id) {
        if (id == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        try {
            return UserMapper.toUserDto(userStorage.getReferenceById(id));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователя с id = " + id + " не существует!");
        }
    }

    public User addUser(User user) {
        try {
            return userStorage.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Пользователь с E-mail " + user.getEmail() +
                    " уже существует!");
        }
    }

    public User refreshUser(Long id, UserDto user) {
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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователя с id = " + id + " не существует!");
        }
        try {
            return userStorage.save(userFromDb);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Пользователь с E-mail " + user.getEmail() +
                    " уже существует!");
        }
    }

    public void deleteUser(Long id) {
        try {
            userStorage.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователя с id = " + id + " не существует!");
        }
    }

    public List<User> getAll() {
        return userStorage.findAll();
    }
}
