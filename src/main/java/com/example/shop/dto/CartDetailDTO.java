package com.example.shop.dto;


import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CartDetailDTO {

    private Long cartItemId;    //장바구니 아이디

    private String itemNm;  //상품명

    private int price; //상품금액

    private int count;  //상품수량

    private String imgName; //상품이름 경로는 /imges/로 대체


}
