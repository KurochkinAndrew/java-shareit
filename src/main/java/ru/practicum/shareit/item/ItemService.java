package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserExistenceChecker;
import ru.practicum.shareit.user.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemStorage;
    private final CommentRepository commentRepository;
    private final UserExistenceChecker userChecker;
    private final ItemExistenceChecker itemChecker;
    private final ItemMapper itemMapper;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public ItemDto addItem(ItemDto itemDto, Long userId) {
        if (userId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        if (userChecker.isUserExist(userId)) {
            return ItemMapper.toItemDto(itemStorage.save(ItemMapper.toItem(itemDto, userId)));
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователя с id = " + userId + " не существует!");
    }

    public ItemDtoWithBooking getItemById(Long itemId, Long userId) {
        try {
            ItemDtoWithBooking itemDto = itemMapper.toItemDtoWithBooking(itemStorage.getReferenceById(itemId), userId);
            itemDto.setComments(getCommentsByItemId(itemId));
            return itemDto;
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Вещи с id = " + itemId + " не существует!");
        }
    }

    public ItemDto refreshItem(Long itemId, ItemDto itemDto, Long userId) {
        Item item;
        try {
            item = itemStorage.getReferenceById(itemId);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Вещи с id = " + itemId + " не существует!");
        }
        if (!userId.equals(item.getOwnerId())) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        String itemDtoName = itemDto.getName();
        String itemDtoDescription = itemDto.getDescription();
        Boolean itemDtoAvailable = itemDto.getAvailable();
        if (itemDtoName != null && !itemDtoName.isBlank()) item.setName(itemDtoName);
        if (itemDtoDescription != null && !itemDtoDescription.isBlank()) item.setDescription(itemDtoDescription);
        if (itemDtoAvailable != null) item.setAvailable(itemDtoAvailable);
        return ItemMapper.toItemDto(itemStorage.save(item));
    }

    public List<ItemDtoWithBooking> getItemsOfUser(Long userId) {
        if (!userChecker.isUserExist(userId)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        ArrayList<Item> items = new ArrayList<>(itemStorage.getItemsOfUser(userId));
        ArrayList<ItemDtoWithBooking> itemsDto = new ArrayList<>();
        for (Item item : items) {
            ItemDtoWithBooking itemDtoWithBooking = itemMapper.toItemDtoWithBooking(item, userId);
            itemDtoWithBooking.setComments(getCommentsByItemId(item.getId()));
            itemsDto.add(itemDtoWithBooking);
        }
        return itemsDto;
    }

    public List<Item> searchItemByNameOrDescription(String searchString) {
        if (!searchString.isBlank()) return itemStorage.searchItemByNameOrDescription(searchString);
        return Collections.EMPTY_LIST;
    }

    public CommentDto addComment(Comment comment, Long itemId, Long authorId) {
        if (!userChecker.isUserExist(authorId) || !itemChecker.isItemExist(itemId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        if (bookingRepository.getBookingsOfUserWithEndBefore(authorId, LocalDateTime.now()).size() == 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        comment.setItemId(itemId);
        comment.setAuthor(userRepository.getReferenceById(authorId));
        comment.setCreated(LocalDateTime.now());
        return itemMapper.toCommentDto(commentRepository.save(comment));
    }

    private List<CommentDto> getCommentsByItemId(Long itemId) {
        ArrayList<CommentDto> commentsDto = new ArrayList<>();
        ArrayList<Comment> comments = new ArrayList<>(itemStorage.getCommentsByItemId(itemId));
        for (Comment comment : comments) {
            commentsDto.add(itemMapper.toCommentDto(comment));
        }
        return commentsDto;
    }
}
