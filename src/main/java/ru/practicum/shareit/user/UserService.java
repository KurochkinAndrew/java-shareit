package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public User getUserById(Long id) {
        User user = userStorage.getUserById(id);
        if (id == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        if (user != null) return user;
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    public User addUser(User user) {
        if (userStorage.getHashMapOfEmails().containsValue(user.getEmail()))
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        return userStorage.addUser(user);
    }

    public User refreshUser(Long id, User user) {
        User user1 = userStorage.getUserById(id);
        if (userStorage.getHashMapOfEmails().containsValue(user.getEmail()) &&
                !user1.getEmail().equals(user.getEmail()))
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        if (id == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        if (user1 == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        String userName = user.getName();
        String userEmail = user.getEmail();
        if (userName != null) user1.setName(userName);
        if (userEmail != null) {
            user1.setEmail(userEmail);
            userStorage.updateHashMapOfEmails(id, userEmail);
        }
        return userStorage.refreshUser(id, user1);
    }

    public void deleteUser(Long id) {
        userStorage.deleteUser(id);
    }

    public List<User> getAll() {
        return userStorage.getAll();
    }
}
