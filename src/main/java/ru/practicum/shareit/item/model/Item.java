package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
public class Item {
    private Long id;
    private String name;
    private String Description;
    private boolean available;
    private Long ownerId;
    private ItemRequest request;
}