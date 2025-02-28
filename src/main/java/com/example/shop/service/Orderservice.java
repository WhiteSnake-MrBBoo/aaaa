package com.example.shop.service;


import com.example.shop.constant.OrderStatus;
import com.example.shop.dto.*;
import com.example.shop.entity.*;
import com.example.shop.exception.OutofStockException;
import com.example.shop.repository.ItemRepository;
import com.example.shop.repository.MembersRepository;
import com.example.shop.repository.OrdersRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class Orderservice {

    private final OrdersRepository ordersRepository;

    private final MembersRepository membersRepository;

    private final ItemRepository itemRepository;

    public Long order(OrderDTO orderDTO,String email){
        //참조될 아이템 찾기

        Item item = itemRepository.findById(orderDTO.getItemId())
                .orElseThrow(EntityExistsException::new);

        //참조될 회원
        Members members = membersRepository.findByEmail(email); //멤버에 Entity안에 Email값만 가져온다.




        //부모인 order set
        Orders orders = new Orders();

        orders.setMembers(members); //누구 주문
        orders.setOrderStatus(OrderStatus.ORDER);   //주문상태


        //담을 아이템
        List<OrderItem> orderItemList = new ArrayList<>();

        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);    //입력받은 아이템
        orderItem.setCount(orderDTO.getCount());    //입력받은 주문수량
        orderItem.setOrderPrice(item.getPrice());

        orderItem.setOrders(orders);
        orderItemList.add(orderItem);

        if (item.getStockNumber() - orderDTO.getCount() <= 0 ){
            throw new OutofStockException("상품 재고가 부족합낟. (현재 수량 :" + item.getStockNumber()+")" );

        }

        //주문수량만큼 아이템 수량 변경
        item.setStockNumber(item.getStockNumber() - orderDTO.getCount() );


        orders.setOrderItems(orderItemList);

        Orders ordersA =
        ordersRepository.save(orders);


        return ordersA.getId();

    }

    //상품 주문내역
    public ResponesPageDTO getOrderList(String email, RequestPageDTO requestPageDTO){


        //주문목록 - 페이지 처리 (0부터 시작 10페이지씩 정리)
        Page<Orders> ordersPage =
                ordersRepository.findOrders(email, requestPageDTO.getPageable("id"));

        //주문목록 list - 주문자 DB 값 리스트 배열로 정의
        List<Orders> ordersList =
                ordersPage.getContent();

        //주문목록 dto 변환 - 주문자의 주문목록 보여주는 객체
        List<OrderHistDTO> orderHistDTOList = new ArrayList<>();

        for(Orders orders  : ordersList) {  //주문자 Entity롤 반환
            OrderHistDTO orderHistDTO = new OrderHistDTO(orders);   //뷰페이지로 가는 객체 dtoList
            //주문 목록 보여주는 곳에 주문자 반환

            List<OrderItem> orderItemList = orders.getOrderItems(); //orederItem 배열로 정리
            for (OrderItem entity :  orderItemList){
                //주문아이템의 아이템에 달려 있는 이미지들을 가져와서
                List<ImgEntity> imgEntityList =
                        entity.getItem().getImgEntityList();


                for(ImgEntity imgEntity : imgEntityList){
                    //대표이미지 라면
                    if(imgEntity.getRepimgYn() !=null && imgEntity.getRepimgYn().equals("Y")){
                        OrderItemDTO orderItemDTO
                                = new OrderItemDTO(entity , imgEntity.getImgName());

                        orderHistDTO.addOrderItemDTO(orderItemDTO);

                    }
                }

            }
            orderHistDTOList.add(orderHistDTO);


        }
        return new ResponesPageDTO(requestPageDTO,orderHistDTOList,(int) ordersPage.getTotalElements());

    }

    //주문취소
    public void cancleOrder(Long orderId){

        //주문취소하려는 주문을 Pk로 불러와서
        Orders orders =
        ordersRepository.findById(orderId).orElseThrow(EntityExistsException::new);

        //주문의 주문상태를 취소상태로 변경

        if (orders.getOrderStatus() == OrderStatus.ORDER){
            orders.setOrderStatus( OrderStatus.CANCEL);

            //주문의 주문 아이템들의 수량만큰 재고를 더해준다.
            List<OrderItem> orderItemList = orders.getOrderItems();

            for ( OrderItem orderItem: orderItemList){

//            orderItem.getCount() ;  //주문수량
//            orderItem.getItem().getStockNumber();   //재고수량

                orderItem.getItem().setStockNumber(
                        orderItem.getItem().getStockNumber()
                                +orderItem.getCount()
                );
            }

        }



    }



    //자신이 주문한 내역인지 확인 하는 메소드
    public boolean validateOrder(Long orderId,String email){

        Members members =
        membersRepository.findByEmail(email);   //select * from members where email = :email

        Orders orders =
        ordersRepository.findById(orderId)
                .orElseThrow(EntityExistsException::new); //주문목을을 찾아온다.
                                //select * from orders where order_ie = :orderid

        Members savemember =
        orders.getMembers();

        //현재 로그인 사용자와 현재 주문의 참조하는회원이 같지 않다면
        if ( !members.getEmail().equals(savemember.getEmail())){
            return false;

        }
        return true;

    }


}
