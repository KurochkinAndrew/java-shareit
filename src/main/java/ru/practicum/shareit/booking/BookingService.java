package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserExistenceChecker userExistenceChecker;
    private final ItemExistenceChecker itemExistenceChecker;
    private final BookingMapper bookingMapper;

    public BookingDtoForResponse addBooking(BookingDto bookingDto, Long userId) {
        log.info("Создание бронирования: itemId={}, userId={}", bookingDto.getItemId(), userId);
        if (!userExistenceChecker.isUserExist(userId)) throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Пользователя с id = " + userId + " не существует!");
        if (!itemExistenceChecker.isItemExist(bookingDto.getItemId()))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Вещи с id = " + bookingDto.getItemId() + " не существует!");
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())
                || bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Время начала бронирования не может быть позже" +
                    " или равно времени конца бронирования!");
        }
        Booking booking = bookingMapper.fromBookingDto(bookingDto, userId, Status.WAITING);
        if (booking.getItem().getOwnerId().equals(userId)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        if (!booking.getItem().isAvailable()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Нельзя забронировать недоступную вещь!");
        log.info("Создание бронирования завершено: itemId={}, userId={}", bookingDto.getItemId(), userId);
        return bookingMapper.toBookingDtoForResponse(bookingRepository.save(booking));
    }

    public BookingDtoForResponse getBookingById(Long bookingId, Long userId) {
        log.info("Получение бронирования: bookingId={}, userId={}", bookingId, userId);
        Booking booking;
        try {
            booking = bookingRepository.getReferenceById(bookingId);
            booking.getItem();
        } catch (EntityNotFoundException e) {
            log.info("Бронирование не найдено: bookingId={}, userId={}", bookingId, userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Бронирования с id = " + bookingId +
                    " не существует!");
        }
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwnerId().equals(userId)) {
            log.info("Пользователь не является создателем бронирования или владельцем вещи:" +
                    " bookingId={}, userId={}", bookingId, userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        log.info("Получение бронирования завершено: bookingId={}, userId={}", bookingId, userId);
        return bookingMapper.toBookingDtoForResponse(booking);
    }

    public BookingDtoForResponse approveOrRejectBooking(Long bookingId, Long userId, Boolean approved) {
        log.info("Изменение статуса бронирования: bookingId={}, userId={}, approved={}", bookingId, userId, approved);
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
            log.info("Пользователь подтвердил бронирование: bookingId={}, userId={}", bookingId, userId);
        } else {
            booking.setStatus(Status.REJECTED);
            log.info("Пользователь отклонил бронирование: bookingId={}, userId={}", bookingId, userId);
        }
        return bookingMapper.toBookingDtoForResponse(bookingRepository.save(booking));
    }

    public List<Booking> getBookings(Long userId, String state) {
        if (!userExistenceChecker.isUserExist(userId)) throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Пользователя с id = " + userId + " не существует!");
        State enumState = parseState(state);
        switch (enumState) {
            case ALL:
                return bookingRepository.getBookingsOfUser(userId);
            case CURRENT:
                return bookingRepository.getBookingsOfUserWithStartBeforeAndEndAfter(userId, LocalDateTime.now());
            case PAST:
                return bookingRepository.getBookingsOfUserWithEndBefore(userId, LocalDateTime.now());
            case FUTURE:
                return bookingRepository.getBookingsOfUserWithStartAfter(userId, LocalDateTime.now());
            case WAITING:
                return bookingRepository.getBookingsOfUserWithWaitingStatus(userId);
            case REJECTED:
                return bookingRepository.getBookingsOfUserWithRejectedStatus(userId);
            default:
                throw new UnsupportedStatusException("Unknown state: " + state);
        }
    }

    public List<Booking> getBookingsOfOwnerItems(Long userId, String state) {
        if (!userExistenceChecker.isUserExist(userId)) throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Пользователя с id = " + userId + " не существует!");
        State enumState = parseState(state);
        switch (enumState) {
            case ALL:
                return bookingRepository.getBookingsOfOwnerItems(userId);
            case CURRENT:
                return bookingRepository.getBookingsOfOwnerItemsWithStartBeforeAndEndAfter(userId, LocalDateTime.now());
            case PAST:
                return bookingRepository.getBookingsOfOwnerItemsWithEndBefore(userId, LocalDateTime.now());
            case FUTURE:
                return bookingRepository.getBookingsOfOwnerItemsWithStartAfter(userId, LocalDateTime.now());
            case WAITING:
                return bookingRepository.getBookingsOfOwnerItemsWithWaitingStatus(userId);
            case REJECTED:
                return bookingRepository.getBookingsOfOwnerItemsWithRejectedStatus(userId);
            default:
                throw new UnsupportedStatusException("Unknown state: " + state);
        }
    }

    private State parseState(String state) {
        State status;
        try {
            status = State.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStatusException("Unknown state: " + state);
        }
        return status;
    }
}
