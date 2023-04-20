package dev.evv.booking.client.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Location {

    private Long id;
    private String name;
    private String country;
    private String city;
    private String street;
    private String house;

}
