package com.example.shop.service;


import com.example.shop.dto.CartDetailDTO;
import com.example.shop.dto.CartItemDTO;
import com.example.shop.entity.Cart;
import com.example.shop.entity.CartItem;
import com.example.shop.entity.Item;
import com.example.shop.entity.Members;
import com.example.shop.repository.CartItemRepository;
import com.example.shop.repository.CartRepository;
import com.example.shop.repository.ItemRepository;
import com.example.shop.repository.MembersRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class CartService {

    private final ItemRepository itemRepository;

    private final MembersRepository membersRepository;

    private final CartRepository cartRepository;

    private final CartItemRepository  cartItemRepository;

    public Long addCart(CartItemDTO cartItemDTO,String email){
        //장바구니에 담을때는 기본적으로
        //아이템을 참조하는 장바구니 아이템들을 장바구니에 넣습니다.
        //장바구니는 회원과 1:1 관계입니다.

        //내가사는 아이템 찾기
        Item item =
        itemRepository.findById(cartItemDTO.getItemId())
                .orElseThrow(EntityNotFoundException::new);

        //내가누구
        Members members =
                membersRepository.findByEmail(email);
        //내장바구니
        Cart cart =
        cartRepository.findByMembersEmail(email);
        log.info(cart + "cart아이템 검사 하기 전에 들어 오니");

        if (cart == null){
            Cart saveCart = new Cart();
            saveCart.setMembers(members);

            cart =
            cartRepository.save(saveCart);  //장바구니가 없다면 만들어주자

        }

        //장바구니에 담겨진 아이템 찾기
        CartItem cartItem =
        cartItemRepository.findByCartIdAndItemId(cart.getId(),item.getId());

        if (cartItem == null){
            //장바구니에 아이템이 담겨 있지 않다면
            CartItem saveCartItem = new CartItem();
            saveCartItem.setCart(cart); //장바구니
            saveCartItem.setItem(item); //장바구니에 담길 아이템
            saveCartItem.setCount(cartItemDTO.getCount());  //수량

            cartItem =
            cartItemRepository.save(saveCartItem);
            //아이템 저장

            return cartItem.getId();
        }else {
            //장바구니에 동일한 아이템이 있다면
            //장바구니아이템의 수량을 기존수량 + 입력받은 수량으로 수정해준다.
            cartItem.setCount(
                    cartItem.getCount() + cartItem.getCount()
            );

            return cartItem.getId();

        }




    }

    public List<CartDetailDTO> getCartList(String email){



        List<CartDetailDTO> cartDetailDTOList =
                cartItemRepository.findByCartDetailDTOList(email);

        return cartDetailDTOList;
    }

    //카트의 주인 확인

    public boolean validateCartItem(Long cartItemId,String email){


        Members members = membersRepository.findByEmail(email);

        //현재 컨트롤러로 부터 넘겨받은 카트아이템의 아이딜르 통해서
        //카트 아이템을 찾는다면,
        Optional<CartItem> optionalCartItem=
        cartItemRepository.findById(cartItemId);

        CartItem cartItem = optionalCartItem.orElseThrow(EntityNotFoundException::new);

        //카트아이템이 담긴 카트를 찾을수 있고
        Cart cart =
                cartItem.getCart();

        //카트가 참조하는 멤버를 찾을수 잇다.
        Members cartMember = cart.getMembers();

        //현재초그인한 회원과 , 카트가 참조 하는 회원의 pk번고하가 일치하느가 ?
        //혹은 유일한값인 email이 일치하는가 ?
        //select* from cartItem
            //join cart on cartItem.cart_id = cart.cart_id
            //join member on cart.member_id = member.member_id
        //where member.email = 현재 넘겨받은 email
        //and cartItem.cartItem_id == 넘겨받은 cartItemId
        if(members.getNum() == cartMember.getNum()){

            return true;
        }else {
            return false;
        }
    }



    public void updateCartItemCount(Long cartItemId,int count){


            //select * from cartItem where cart.cart_id
            CartItem cartItem =
                cartItemRepository.findById(cartItemId)
                        .orElseThrow(EntityNotFoundException::new);

        cartItem.setCount(count);

        cartItemRepository.save(cartItem);


    }

    //삭제

    public void cartItemDel (Long cartItemId) {
        log.info("서비스로 들어온 pk:" + cartItemId);


        //들어온 pk를 가지고 데이터 삭제
        //delete from cartItem where cartItem_id = 파라미터로 들어온간

        CartItem cartItem =
        cartItemRepository.findById(cartItemId)

                .orElseThrow(EntityNotFoundException::new);

        cartItemRepository.delete(cartItem); //삭제



    }}







