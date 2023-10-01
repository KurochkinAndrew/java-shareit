package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForResponse;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoForResponse addBooking(@RequestBody @Valid BookingDto bookingDto,
                                            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.addBooking(bookingDto, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoForResponse getBookingById(@PathVariable Long bookingId,
                                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoForResponse approveOrRejectBooking(@PathVariable Long bookingId,
                                                        @RequestHeader("X-Sharer-User-Id") Long userId,
                                                        @RequestParam Boolean approved) {
        return bookingService.approveOrRejectBooking(bookingId, userId, approved);
    }

    @GetMapping
    public List<Booking> getBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<Booking> getBookingsOfOwnerItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getBookingsOfOwnerItems(userId, state);
    }
}
