package com.example.shop.controller;


import com.example.shop.dto.OrderDTO;
import com.example.shop.exception.OutofStockException;
import com.example.shop.service.Orderservice;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.Type;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
public class OrderController {

    private final Orderservice orderservice;

    @PostMapping("/order")
    public /*@ResponseBody*/ ResponseEntity order(@RequestBody @Valid OrderDTO orderDTO,
                                                  BindingResult bindingResult, Principal principal)
    {
        log.info("주문하기 post진입 ");
        log.info("들어온값 체크 " + orderDTO);

        if (bindingResult.hasErrors()){
            log.info("유효성 count가 없다면");

            StringBuilder sb = new StringBuilder();
            //"홍깅동" + "홍길동" append를 통해서 추가적으로 저장 가능

            log.info(bindingResult.getAllErrors());

            List<FieldError> fieldErrors =
            bindingResult.getFieldErrors();

            for (FieldError fieldError:fieldErrors){
                sb.append(fieldError.getDefaultMessage());

            }

            log.info(sb.toString());
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        if (principal == null){
            log.info("로그인 안되어 있음");

            return new ResponseEntity<String>("",HttpStatus.UNAUTHORIZED);

        }
        //주문을 하려면 부모인 주문 엔티티필요, 주문 엔티티는 회원과 1:1, 이메일로 주무 찾아오기
        String email = principal.getName();


        Long orderId = null;

        try{

            orderId = orderservice.order(orderDTO,email);

        }catch (OutofStockException e){
            return new ResponseEntity<String>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }






        //주문아이템의 부모인 아이템 > 입력받은 itemid로 해결

           return new ResponseEntity<Long>(orderId,HttpStatus.OK);
    }

}
