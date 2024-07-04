package com.programingish.order_service.service;


import com.programingish.order_service.dto.OrderRequest;
import com.programingish.order_service.dto.OrderlinesItemsDto;
import com.programingish.order_service.model.Order;
import com.programingish.order_service.model.OrderLineItems;
import com.programingish.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    
    public void placeOrder(OrderRequest orderRequest){

        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> orderLineItems = orderRequest.getOrderlinesItemsDtoList()
                .stream()
                .map(this::mapToDto)
               .toList();
        order.setOrderLineItemsList(orderLineItems);
        orderRepository.save(order);

    }

    private OrderLineItems mapToDto(OrderlinesItemsDto orderlinesItemsDto) {

        OrderLineItems items = new OrderLineItems();
        items.setPrice(orderlinesItemsDto.getPrice());
        items.setQuantity(orderlinesItemsDto.getQuantity());
        items.setSkuCode(orderlinesItemsDto.getSkuCode());

        return items;
    }
}
