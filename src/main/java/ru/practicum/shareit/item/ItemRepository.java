package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("SELECT i FROM Item i WHERE i.ownerId = ?1 ORDER BY i.id")
    List<Item> getItemsOfUser(Long userId);

    @Query("SELECT i FROM Item i WHERE i.available = true AND" +
            " (UPPER (i.description) LIKE UPPER (concat('%', ?1, '%'))" +
            " OR UPPER(i.name) LIKE UPPER (concat('%', ?1, '%')))")
    List<Item> searchItemByNameOrDescription(String searchString);

    @Query("SELECT c FROM Comment c WHERE c.itemId = ?1")
    List<Comment> getCommentsByItemId(Long itemId);
}
