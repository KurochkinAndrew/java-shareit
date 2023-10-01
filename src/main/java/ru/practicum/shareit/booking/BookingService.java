package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForResponse;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.item.ItemExistenceChecker;
import ru.practicum.shareit.user.UserExistenceChecker;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserExistenceChecker userExistenceChecker;
    private final ItemExistenceChecker itemExistenceChecker;
    private final BookingMapper bookingMapper;

    public BookingDtoForResponse addBooking(BookingDto bookingDto, Long userId) {
        if (!userExistenceChecker.isUserExist(userId)) throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Пользователя с id = " + userId + "не существует!");
        if (!itemExistenceChecker.isItemExist(bookingDto.getItemId()))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Вещи с id = " + bookingDto.getItemId() + "не существует!");
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())
                || bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Время начала бронирования не может быть позже" +
                    " или равно времени конца бронирования!");
        }
        Booking booking = bookingMapper.fromBookingDto(bookingDto, userId, Status.WAITING);
        if (booking.getItem().getOwnerId().equals(userId) ) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        if (!booking.getItem().isAvailable()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Нельзя забронировать недоступную вещь!");
        return bookingMapper.toBookingDtoForResponse(bookingRepository.save(booking));
    }

    public BookingDtoForResponse getBookingById(Long bookingId, Long userId) {
        Booking booking;
        try {
            booking = bookingRepository.getReferenceById(bookingId);
            booking.getItem();
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Бронирования с id = " + bookingId +
                    " не существует!");
        }
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwnerId().equals(userId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return bookingMapper.toBookingDtoForResponse(booking);
    }

    public BookingDtoForResponse approveOrRejectBooking(Long bookingId, Long userId, Boolean approved) {
        Booking booking;
        try {
            booking = bookingRepository.getReferenceById(bookingId);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Бронирования с id = " + bookingId +
                    " не существует!");
        }
        if (!booking.getItem().getOwnerId().equals(userId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        if (!booking.getStatus().equals(Status.WAITING)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return bookingMapper.toBookingDtoForResponse(bookingRepository.save(booking));
    }

    public List<Booking> getBookings(Long userId, String state) {
        if (!userExistenceChecker.isUserExist(userId)) throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Пользователя с id = " + userId + "не существует!");
        switch (state) {
            case "ALL":
                return bookingRepository.getBookingsOfUser(userId);
            case "CURRENT":
                return bookingRepository.getBookingsOfUserWithStartBeforeAndEndAfter(userId, LocalDateTime.now());
            case "PAST":
                return bookingRepository.getBookingsOfUserWithEndBefore(userId, LocalDateTime.now());
            case "FUTURE":
                return bookingRepository.getBookingsOfUserWithStartAfter(userId, LocalDateTime.now());
            case "WAITING":
                return bookingRepository.getBookingsOfUserWithWaitingStatus(userId);
            case "REJECTED":
                return bookingRepository.getBookingsOfUserWithRejectedStatus(userId);
            default:
                throw new UnsupportedStatusException("Unknown state: " + state);
        }
    }

    public List<Booking> getBookingsOfOwnerItems(Long userId, String state) {
        if (!userExistenceChecker.isUserExist(userId)) throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Пользователя с id = " + userId + "не существует!");
        switch (state) {
            case "ALL":
                return bookingRepository.getBookingsOfOwnerItems(userId);
            case "CURRENT":
                return bookingRepository.getBookingsOfOwnerItemsWithStartBeforeAndEndAfter(userId, LocalDateTime.now());
            case "PAST":
                return bookingRepository.getBookingsOfOwnerItemsWithEndBefore(userId, LocalDateTime.now());
            case "FUTURE":
                return bookingRepository.getBookingsOfOwnerItemsWithStartAfter(userId, LocalDateTime.now());
            case "WAITING":
                return bookingRepository.getBookingsOfOwnerItemsWithWaitingStatus(userId);
            case "REJECTED":
                return bookingRepository.getBookingsOfOwnerItemsWithRejectedStatus(userId);
            default:
                throw new UnsupportedStatusException("Unknown state: " + state);
        }
    }
}
