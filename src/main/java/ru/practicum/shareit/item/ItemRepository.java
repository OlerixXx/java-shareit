package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;


public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(String name, String description, Pageable page);

    List<Item> findAllByOwner(User owner, Pageable page);

    List<Item> findAllByOwner(User owner);

    Optional<Item> findByIdAndOwnerIdIsNot(Long itemId, Long ownerId);

    List<Item> findAllByRequestId(Long requestId);
}
