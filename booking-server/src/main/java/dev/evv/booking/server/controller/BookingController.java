package dev.evv.booking.server.controller;

import dev.evv.booking.client.model.Room;
import dev.evv.booking.client.vo.RoomStatus;
import dev.evv.booking.server.service.BookingService;
import dev.evv.booking.server.service.OrderService;
import dev.evv.order.client.model.Order;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
@EnableFeignClients
@RequestMapping(path = "/v1/room")
public class BookingController {
    public static Logger logger = Logger.getLogger(BookingController.class.getName());
    private static final String BACKEND_BOOKING = "backend_booking";
    private final BookingService bookingService;
    private final OrderService orderService;

    private int retryAttemps = 1;

    private Map<Long, Room> rooms = new HashMap<>(){
        {
            put(101L, new Room(101L, null, "1a", 1000.0, RoomStatus.FREE));
            put(201L, new Room(201L, null, "2a", 1050.0, RoomStatus.FREE));
            put(301L, new Room(301L, null, "3a", 1100.0, RoomStatus.FREE));
            put(401L, new Room(401L, null, "4a", 2000.0, RoomStatus.FREE));
            put(501L, new Room(501L, null, "5a", 2100.0, RoomStatus.FREE));
        }
    };

    public BookingController(BookingService bookingService, OrderService orderService) {
        this.bookingService = bookingService;
        this.orderService = orderService;
    }

    @GetMapping("/all")
    @RateLimiter(name = BACKEND_BOOKING, fallbackMethod = "getAllReteLimFallback")
    public ResponseEntity<List<Room>> getRooms(){
        return ResponseEntity.ok(rooms.values().stream().toList());
    }

    @PostMapping()
    public ResponseEntity<Room> addRoom(@RequestBody Room room){
        rooms.put(room.getId(), room);
        return ResponseEntity.ok(room);
    }

    @DeleteMapping("/{roomId}")
    @CircuitBreaker(name = BACKEND_BOOKING, fallbackMethod = "deleteByIdFallback")
    @Retry(name = BACKEND_BOOKING, fallbackMethod = "deleteByIdRetryFallback")
    public ResponseEntity<String> deleteById(@PathVariable Long roomId){
//        List<Order> roomOrders = bookingService.findOrdersByRoomId(roomId);
        logger.info("attempt " + retryAttemps++);
        List<Order> allOrders = orderService.findAll().getBody();
        List<Order> roomOrders = allOrders.stream()
                .filter(o -> o.getRoomId().equals(roomId))
                .collect(Collectors.toList());

        if(roomOrders.size() > 0){
            return ResponseEntity.ok(
                    String.format("We can't delete room (id=%d), because it has %d orders, ",
                            roomId,
                            roomOrders.size()));
        }
        Optional<Room> deleted = Optional.ofNullable(rooms.remove(roomId));
        return ResponseEntity.ok(
                String.format("room number %s (id=%d) was deleted",
                deleted.orElse(new Room()).getNumber(),
                deleted.orElse(new Room()).getId())
        );
    }

    public ResponseEntity<String> deleteByIdFallback(Exception e){
        return ResponseEntity.ok("(circuit breaker) fallback from BookingService because OrderService is down");
    }

    public ResponseEntity<String> deleteByIdRetryFallback(Exception e){
        return ResponseEntity.ok("(retry) fallback from BookingService because OrderService is down");
    }

    public ResponseEntity<String> getAllReteLimFallback(Exception e){
        return ResponseEntity.ok("(rateLimiter) fallback from BookingService because many request in some period of time");
    }

}
