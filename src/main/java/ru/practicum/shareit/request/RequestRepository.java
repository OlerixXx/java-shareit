package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByRequestorIdOrderByCreatedDesc(Long requestor);

    @Query(value = "SELECT * FROM requests WHERE requestor_id != ?1", nativeQuery = true)
    List<Request> findAllExcludingOwner(Long requestor, Pageable pageable);
}
