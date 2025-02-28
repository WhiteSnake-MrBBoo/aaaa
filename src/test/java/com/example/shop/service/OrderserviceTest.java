package com.example.shop.service;

import com.example.shop.dto.OrderHistDTO;
import com.example.shop.dto.RequestPageDTO;
import com.example.shop.dto.ResponesPageDTO;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Log4j2
class OrderserviceTest {

    @Autowired
    Orderservice orderservice;

    @Test
    @Transactional
    public void List(){

        RequestPageDTO requestPageDTO = new RequestPageDTO();

        ResponesPageDTO<OrderHistDTO> responesPageDTO=
        orderservice.getOrderList("1212@1212",requestPageDTO);


        responesPageDTO.getDtoList().forEach(orderHistDTO -> log.info(orderHistDTO));

    }


}