package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;

    public ItemDto addItem(ItemDto itemDto, Long userId) {
        userService.getUserById(userId);
        return ItemMapper.toItemDto(itemStorage.addItem(itemDto, userId));
    }

    public ItemDto getItemDtoById(Long itemId) {
        if (itemStorage.getItemById(itemId) != null) return ItemMapper.toItemDto(itemStorage.getItemById(itemId));
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    public ItemDto refreshItem(Long itemId, ItemDto itemDto, Long userId) {
        Item item = itemStorage.getItemById(itemId);
        if (item == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        if (!userId.equals(item.getOwnerId())) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        String itemDtoName = itemDto.getName();
        String itemDtoDescription = itemDto.getDescription();
        Boolean itemDtoAvailable = itemDto.getAvailable();
        if (itemDtoName != null) item.setName(itemDtoName);
        if (itemDtoDescription != null) item.setDescription(itemDtoDescription);
        if (itemDtoAvailable != null) item.setAvailable(itemDtoAvailable);
        itemStorage.refreshItem(itemId, item);
        return ItemMapper.toItemDto(itemStorage.getItemById(itemId));
    }

    public List<Item> getItemsOfUser(Long userId) {
        if (userId != null) return itemStorage.getItemsOfUser(userId);
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    public List<ItemDto> searchItemByNameOrDescription(String searchString) {
        return itemStorage.searchItemByNameOrDescription(searchString);
    }
}
