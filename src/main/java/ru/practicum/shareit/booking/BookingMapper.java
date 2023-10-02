package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForResponse;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;


@Component
@RequiredArgsConstructor
public class BookingMapper {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public Booking fromBookingDto(BookingDto bookingDto, Long userId, Status status) {
        return new Booking(bookingDto.getId(), bookingDto.getStart(), bookingDto.getEnd(),
                itemRepository.getReferenceById(bookingDto.getItemId()),
                userRepository.getReferenceById(userId), status);
    }

    public BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getId(), booking.getStart(), booking.getEnd(),
                booking.getItem().getId(), booking.getBooker().getId(), booking.getStatus());
    }

    public BookingDtoForResponse toBookingDtoForResponse(Booking booking) {
        return new BookingDtoForResponse(booking.getId(), booking.getStart(), booking.getEnd(),
                ItemMapper.toItemDto(booking.getItem()), UserMapper.toUserDto(booking.getBooker()),
                booking.getStatus());
    }
}
