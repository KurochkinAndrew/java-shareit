package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;

@Component
@RequiredArgsConstructor
public class ItemExistenceChecker {
    private final ItemRepository itemRepository;

    public boolean isItemExist(Long itemId) {
        try {
            itemRepository.getReferenceById(itemId).getName();
            return true;
        } catch (EntityNotFoundException e) {
            return false;
        }
    }
}
