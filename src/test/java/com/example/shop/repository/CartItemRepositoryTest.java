package com.example.shop.repository;

import com.example.shop.dto.CartDetailDTO;
import com.example.shop.entity.CartItem;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Log4j2
class CartItemRepositoryTest {

    @Autowired
    CartItemRepository cartItemRepository;

    @Test
    @Transactional
    public void test2(){

        List<CartDetailDTO> cartDetailDTOS =
        cartItemRepository.findByCartDetailDTOList("1212@1212");

        cartDetailDTOS.forEach(cartDetailDTO -> log.info(cartDetailDTO));

    }


//    @Test
//    @Transactional
//    public void test(){
//
//        List<CartItem> cartItems =
//        cartItemRepository.findByCartDetailDTOList("1212@1212");
//
//        cartItems.forEach(cartItem -> log.info(cartItem));
//    }



}