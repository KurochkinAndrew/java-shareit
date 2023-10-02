package ru.practicum.shareit.booking;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND b.start < ?2 AND b.end > ?2 ORDER BY b.start DESC")
    List<Booking> getBookingsOfUserWithStartBeforeAndEndAfter(Long userId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND b.start > ?2 ORDER BY b.start DESC")
    List<Booking> getBookingsOfUserWithStartAfter(Long userId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND b.end < ?2 ORDER BY b.start DESC")
    List<Booking> getBookingsOfUserWithEndBefore(Long userId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 ORDER BY b.start DESC")
    List<Booking> getBookingsOfUser(Long userId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND b.status = 'WAITING' ORDER BY b.start DESC")
    List<Booking> getBookingsOfUserWithWaitingStatus(Long userId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND b.status = 'REJECTED' ORDER BY b.start DESC")
    List<Booking> getBookingsOfUserWithRejectedStatus(Long userId);

    @Query("SELECT b FROM Booking b WHERE b.item.ownerId = ?1 AND b.start < ?2 AND b.end > ?2 ORDER BY b.start DESC")
    List<Booking> getBookingsOfOwnerItemsWithStartBeforeAndEndAfter(Long userId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.ownerId = ?1 AND b.start > ?2 ORDER BY b.start DESC")
    List<Booking> getBookingsOfOwnerItemsWithStartAfter(Long userId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.ownerId = ?1 AND b.end < ?2 ORDER BY b.start DESC")
    List<Booking> getBookingsOfOwnerItemsWithEndBefore(Long userId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.ownerId = ?1 ORDER BY b.start DESC")
    List<Booking> getBookingsOfOwnerItems(Long userId);

    @Query("SELECT b FROM Booking b WHERE b.item.ownerId = ?1 AND b.status = 'WAITING' ORDER BY b.start DESC")
    List<Booking> getBookingsOfOwnerItemsWithWaitingStatus(Long userId);

    @Query("SELECT b FROM Booking b WHERE b.item.ownerId = ?1 AND b.status = 'REJECTED' ORDER BY b.start DESC")
    List<Booking> getBookingsOfOwnerItemsWithRejectedStatus(Long userId);

    @Query("SELECT b FROM Booking b WHERE b.item.ownerId = ?1 AND b.status = 'APPROVED'" +
            " AND b.item.id = ?2 AND b.start < ?3 ORDER BY b.start DESC")
    List<Booking> getLastBooking(Long userId, Long itemId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.ownerId = ?1 AND b.status = 'APPROVED'" +
            " AND b.item.id = ?2 AND b.start > ?3 ORDER BY b.start ASC")
    List<Booking> getNextBooking(Long userId, Long itemId, LocalDateTime now);
}
