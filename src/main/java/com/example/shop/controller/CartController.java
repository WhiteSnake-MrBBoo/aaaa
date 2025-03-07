package com.example.shop.controller;


import com.example.shop.dto.CartDetailDTO;
import com.example.shop.dto.CartItemDTO;
import com.example.shop.exception.OutofStockException;
import com.example.shop.service.CartService;
import com.example.shop.service.Orderservice;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@Controller

@RequiredArgsConstructor
@Log4j2
public class CartController {

    private final CartService cartService;
    private final Orderservice orderservice;

    @PostMapping("/cart")
    public ResponseEntity order(@Valid CartItemDTO cartItemDTO,
                                BindingResult bindingResult,
                                Principal principal
    )
    {
        //데이터 들어오는지 확인
        log.info(cartItemDTO);
        log.info(cartItemDTO);
        log.info(cartItemDTO);


        if (bindingResult.hasErrors()){

            log.info("장바구니 유효성검사 에러");
            log.info(bindingResult.getAllErrors());

            List<FieldError> fieldErrorList = bindingResult.getFieldErrors();

            StringBuilder stringBuilder = new StringBuilder();

            for (FieldError error : fieldErrorList){


                stringBuilder.append(error.getDefaultMessage());

            }

            //입력된 에러를 다시 보여주기 위해 반환값으로 에러내용을 반환한다.
            return  new ResponseEntity<String>(stringBuilder.toString(), HttpStatus.BAD_REQUEST);

        }

        if (principal == null){

            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        String email = principal.getName();
        Long cartItemId = null;

        try {

            cartItemId =
            cartService.addCart(cartItemDTO, email);
        }catch (EntityNotFoundException e) {

            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);

        }

        return new ResponseEntity<Long>
                (cartItemId, HttpStatus.OK);



    }

    @GetMapping("/cart")
    public String carthist(Principal principal, Model model){

//        List<CartDetailDTO> cartDetailDTOList
//                = cartService.getCartList(principal.getName());
//
//        model.addAttribute("cartDetailDTOList", cartDetailDTOList);

        model.addAttribute("cartDetailDTOList",
                cartService.getCartList(principal.getName()));

        return "cart/cartlist";

    }

    @PatchMapping("/cartItem/{cartItemId}/{count}")
    public ResponseEntity updateCount(
            @PathVariable("cartItemId")Long cartItemid,
            @PathVariable("count")int count, Principal principal)
    {

        log.info("장바구니 아이템 번호:" + cartItemid );
        log.info("수량:" + count );

        log.info("장바구니 아이템 번호:" + cartItemid );
        log.info("수량:" + count );

        if (count <= 0){
            return new ResponseEntity<String>("최소1개이상 담아주에쇼",
            HttpStatus.BAD_REQUEST);

        }else {

        }
        if (principal == null){
            return new ResponseEntity( HttpStatus.UNAUTHORIZED);

        }

        //현재 카트가 내꺼니?
        if (!cartService.validateCartItem(cartItemid,principal.getName())){
            //일치하지 않는다면 false값이기 때문에 !붙여주고
            //니꺼 아니니까 다시 페이지이동
            return new ResponseEntity( HttpStatus.FORBIDDEN);


        }


        //카트아이템의 수량변경

        cartService.updateCartItemCount(cartItemid,count);

        //카트 상품에 대한 접근 제한 할것
        // 접근이 가능하다면 db에 장바구니 아이템에 저장 하기

        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/cartItemdel/{cartItemId}")
    public ResponseEntity delcartItem(){

        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/cartItemdel")
    public ResponseEntity delcartItem(Long cartItemId,Principal principal){


        if (principal == null){
            return new ResponseEntity( HttpStatus.UNAUTHORIZED);

        }

        //현재 카트가 내꺼니?
        if (!cartService.validateCartItem(cartItemId,principal.getName())){
            //일치하지 않는다면 false값이기 때문에 !붙여주고
            //니꺼 아니니까 다시 페이지이동
            return new ResponseEntity( HttpStatus.FORBIDDEN);


        }

        //삭제
        try {
            cartService.cartItemDel(cartItemId);

        }catch (EntityNotFoundException e){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);

        }


        log.info("컨트롤러로 들어온다. " + cartItemId);


        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/cart/orders")
    public ResponseEntity aaaa(@RequestParam(value = "cartItemIdList",required = false)List<Long> cartItemIdList,Principal principal){


        if (cartItemIdList == null){
            return new ResponseEntity<String>("2." ,HttpStatus.BAD_REQUEST);


        }

        if (principal == null){
            return new ResponseEntity( HttpStatus.UNAUTHORIZED);

        }

        //현재 카트가 내꺼니?
        for (Long cartItemId : cartItemIdList){


            if (!cartService.validateCartItem(cartItemId,principal.getName())){
                //일치하지 않는다면 false값이기 때문에 !붙여주고
                //니꺼 아니니까 다시 페이지이동

                return new ResponseEntity( HttpStatus.FORBIDDEN);

            }

        }
        //저장 주문
        try {
            orderservice.orders(cartItemIdList,principal.getName());


        }catch (OutofStockException e){

            return new ResponseEntity<String>("재고가부족합니다." ,HttpStatus.BAD_REQUEST);
        }



        return new ResponseEntity(HttpStatus.OK);

    }


}
