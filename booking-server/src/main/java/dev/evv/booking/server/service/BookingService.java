package dev.evv.booking.server.service;

import dev.evv.order.client.model.Order;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

//    private RestTemplate restTemplate;
//
//    public BookingService(RestTemplate restTemplate) {
//        this.restTemplate = restTemplate;
//    }

    public List<Order> findOrdersByRoomId(Long roomId){
        RestTemplate restTemplate = new RestTemplate();
        String resourceUrl = "http://order-server/api/v1/order/all";
        ResponseEntity<List<Order>> responseEntity =
                restTemplate.exchange(
                        resourceUrl,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<Order>>() {}
                );
        List<Order> allOrders = responseEntity.getBody();

        return allOrders.stream()
                .filter(o -> o.getRoomId().equals(roomId))
                .collect(Collectors.toList());
    }

    public List<Order> getFallBackFindOrdersByRoomId(Long roomId){
        return Arrays.asList(new Order());
    }

}
