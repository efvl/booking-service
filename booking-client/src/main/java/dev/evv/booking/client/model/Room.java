package dev.evv.booking.client.model;

import dev.evv.booking.client.vo.RoomStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    private Long id;
    private String order;
    private String number;
    private Double price;
    private RoomStatus roomStatus;

}
