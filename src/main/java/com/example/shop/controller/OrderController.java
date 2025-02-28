package com.example.shop.controller;


import com.example.shop.dto.OrderDTO;
import com.example.shop.dto.OrderHistDTO;
import com.example.shop.dto.RequestPageDTO;
import com.example.shop.dto.ResponesPageDTO;
import com.example.shop.exception.OutofStockException;
import com.example.shop.service.Orderservice;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

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
            log.info("이전페이지 주소" );

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

//    @RequestMapping(name = " ", value = , method = {RequestMethod.GET })

    //www.naver.com/orders/{123}
    @GetMapping({"/orders","/orders/{page}"})
    public String orderHist(@PathVariable ("page")Optional<Integer>page
            , Principal principal , Model model, RequestPageDTO requestPageDTO)
    {
        //페이지를 url롤 받은 값이 없다면 1, 있다면 받는값 할당
        requestPageDTO.setPage(page.isPresent() ? page.get() : 1 );

        log.info("현재 페이지는 ?" + (page.isPresent() ? page.get() : 1 ));

        //파라미터가 Long이라면
//        if(page != null){
//            requestPageDTO.setPage(page.intValue());    //Long를 int롤 변환
//        }

        //만약에 로그인이 안되어있다면 리다이렉트 하던가 페이지 자체가 로그인 되어야 한다.
        if(principal == null){

            return "redirect:/";
        }

        ResponesPageDTO<OrderHistDTO> responesPageDTO =
        orderservice.getOrderList(principal.getName(),requestPageDTO);

        //가져온값 로그로 찍어오기
        responesPageDTO.getDtoList().forEach(orderHistDTO -> log.info(orderHistDTO));

        model.addAttribute("responesPageDTO" , responesPageDTO);



        return  "order/orderHist";

    }

    @PostMapping("/order/{orderId}/cancel")
    public ResponseEntity cancelOrder(@PathVariable("orderId")Long orderId,
                                      Principal principal)
    {

        if ( orderservice.validateOrder(orderId,principal.getName())){

            return new ResponseEntity<String>("주문취소 권한이 없습니다.",HttpStatus.FORBIDDEN);
        }
        log.info("orderId 받은 주문 번호" + orderId);
        log.info("orderId 받은 주문 번호" + orderId);
        log.info("orderId 받은 주문 번호" + orderId);

        orderservice.cancleOrder(orderId);


        return new ResponseEntity<Long>(orderId,HttpStatus.OK);
    }



}
