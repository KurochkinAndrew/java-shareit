package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemStorage {
    private long newId = 1;
    private final HashMap<Long, Item> items;
    private final HashMap<Long, List<Item>> itemsOfUsers;

    public Item addItem(ItemDto itemDto, Long ownerId) {
        Item item = new Item(
                newId,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                ownerId,
                null);
        items.put(newId, item);
        if (itemsOfUsers.get(item.getOwnerId()) == null) {
            itemsOfUsers.put(item.getOwnerId(), List.of(item));
        } else {
            itemsOfUsers.get(item.getOwnerId()).add(item);
        }
        newId++;
        return item;
    }

    public Item getItemById(Long id) {
        return items.get(id);
    }

    public void refreshItem(Long id, Item item) {
        items.put(id, item);
    }

    public List<Item> getItemsOfUser(Long userId) {
        return itemsOfUsers.get(userId);
    }

    public List<ItemDto> searchItemByNameOrDescription(String searchString) {
        List<ItemDto> foundItems = new ArrayList<>();
        for (Item item : items.values()) {
            if ((StringUtils.containsIgnoreCase(item.getName(), searchString)
                    || StringUtils.containsIgnoreCase(item.getDescription(), searchString)) &&
                    !searchString.isBlank() && item.isAvailable())
                foundItems.add(ItemMapper.toItemDto(item));
        }
        return foundItems;
    }
}
