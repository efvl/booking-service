package dev.evv.booking.server.service;

import dev.evv.order.client.model.Order;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "order-server", path = "/api/v1/order")
public interface OrderService {

    @GetMapping("/all")
    ResponseEntity<List<Order>> findAll();

}
