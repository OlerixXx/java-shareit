package ru.practicum.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.item.ItemRepository;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.dto.RequestWithItemsDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.user.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final RequestRepository requestRepository;

    @Transactional
    public Request create(Long userId, RequestDto requestDto) {
        if (!userRepository.userExists(userId)) {
            throw new NoSuchElementException();
        }
        return requestRepository.save(RequestMapper.toRequest(
                requestDto,
                userRepository.findById(userId).orElseThrow(NoSuchElementException::new)
        ));
    }

    public List<RequestWithItemsDto> getAllRequests(Long userId) {
        if (!userRepository.userExists(userId)) {
            throw new NoSuchElementException();
        }
        return requestListToDto(requestRepository.findAllByRequestorIdOrderByCreatedDesc(userId));

    }

    public List<RequestWithItemsDto> getAllRequestsItems(Long userId, Pageable page) {
        return requestListToDto(requestRepository.findAllExcludingOwner(
                userId, PageRequest.of(page.getPageNumber(), page.getPageSize(), Sort.by("created").descending()))
        );
    }

    public RequestWithItemsDto getRequest(Long userId, Long requestId) {
        if (!userRepository.userExists(userId)) {
            throw new NoSuchElementException();
        }
        return RequestMapper.toRequestWithItems(requestRepository.findById(requestId).orElseThrow(NoSuchElementException::new),
                itemRepository.findAllByRequestId(requestId)
        );
    }

    private List<RequestWithItemsDto> requestListToDto(List<Request> requests) {
        if (requests.isEmpty()) {
            return List.of();
        } else {
            return requests.stream()
                    .map(request -> RequestMapper.toRequestWithItems(request, itemRepository.findAllByRequestId(request.getId())))
                    .collect(Collectors.toList());
        }
    }
}
