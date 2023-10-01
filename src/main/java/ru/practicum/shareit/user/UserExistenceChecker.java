package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;

@RequiredArgsConstructor
@Component
public class UserExistenceChecker {
    private final UserRepository userRepository;

    public boolean isUserExist(Long userId) {
        try {
            userRepository.getReferenceById(userId).getName();
            return true;
        } catch (EntityNotFoundException e) {
            return false;
        }
    }
}
