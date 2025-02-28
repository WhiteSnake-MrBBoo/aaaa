package com.example.shop.dto;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString


public class CartItemDTO {

    private Long itemId;

    @Min(value = 1, message ="최소값은 1입니다." )
    private int count;



}
