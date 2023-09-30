package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.addItem(itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemDtoById(@PathVariable("itemId") Long itemId) {
        return itemService.getItemDtoById(itemId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto refreshItem(@PathVariable("itemId") Long itemId, @RequestBody ItemDto itemDto,
                               @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.refreshItem(itemId, itemDto, userId);
    }

    @GetMapping
    public List<Item> getItemsOfUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItemsOfUser(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemByNameOrDescription(@RequestParam(value = "text") String searchString) {
        return itemService.searchItemByNameOrDescription(searchString);
    }
}
