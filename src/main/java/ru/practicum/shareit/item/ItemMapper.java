package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class ItemMapper {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getRequestId() != null ? item.getRequestId() : null,
                new ArrayList<>(),
                null,
                null
        );
    }

    public static Item toItem(ItemDto itemDto, Long ownerId) {
        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(),
                ownerId, itemDto.getRequestId());
    }

    public ItemDtoWithBooking toItemDtoWithBooking(Item item, Long userId) {
        ArrayList<Booking> lastBookings = new ArrayList<>(bookingRepository.getLastBooking(item.getOwnerId(),
                item.getId(), LocalDateTime.now()));
        ArrayList<Booking> nextBookings =
                new ArrayList<>(bookingRepository.getNextBooking(item.getOwnerId(), item.getId(),
                        LocalDateTime.now()));
        BookingDto lastBooking = null;
        BookingDto nearestBooking = null;
        if (!lastBookings.isEmpty() && item.getOwnerId().equals(userId)) lastBooking =
                bookingMapper.toBookingDto(lastBookings.get(0));
        if (!nextBookings.isEmpty() && item.getOwnerId().equals(userId))
            nearestBooking = bookingMapper.toBookingDto(nextBookings.get(0));
        return new ItemDtoWithBooking(item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getRequestId() != null ? item.getRequestId() : null,
                lastBooking,
                nearestBooking,
                new ArrayList<>());
    }

    public CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText(), comment.getItemId(),
                comment.getAuthor().getName(), comment.getCreated());
    }
}
