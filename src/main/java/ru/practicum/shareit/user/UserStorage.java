package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserStorage {
    private long newId = 1;
    private final HashMap<Long, User> users;
    private final HashMap<Long, String> emails;

    public User getUserById(Long id) {
        return users.get(id);
    }

    public User addUser(User user) {
        user.setId(newId);
        users.put(newId, user);
        emails.put(user.getId(), user.getEmail());
        newId++;
        return user;
    }

    public User refreshUser(Long id, User user) {
        user.setId(id);
        users.put(id, user);
        return user;
    }

    public HashMap<Long, String> getHashMapOfEmails() {
        return emails;
    }

    public void updateHashMapOfEmails(Long id, String email) {
        emails.put(id, email);
    }

    public void deleteUser(Long id) {
        users.remove(id);
        emails.remove(id);
    }

    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }
}
