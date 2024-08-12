package com.programingish.order_service.service;


import com.programingish.order_service.dto.InventoryResponse;
import com.programingish.order_service.dto.OrderRequest;
import com.programingish.order_service.dto.OrderlinesItemsDto;
import com.programingish.order_service.model.Order;
import com.programingish.order_service.model.OrderLineItems;
import com.programingish.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient webClient;
    
    public void placeOrder(OrderRequest orderRequest){

        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> orderLineItems = orderRequest.getOrderlinesItemsDtoList()
                .stream()
                .map(this::mapToDto)
               .toList();
        order.setOrderLineItemsList(orderLineItems);
        //Call Inventory service, and place order if product is in stock
        
        List<String> skuCodes = order.getOrderLineItemsList().stream()
                .map(OrderLineItems::getSkuCode).toList();
        
        InventoryResponse [] inventoryResponses = webClient.get().uri("http://localhost:8082/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode",skuCodes).build())
                        .retrieve().bodyToMono(InventoryResponse[].class).block();

        Boolean result = null;
        if (inventoryResponses != null) {
            result = Arrays.stream(inventoryResponses).allMatch(InventoryResponse::isInStock);
        }

        if (Boolean.TRUE.equals(result)) orderRepository.save(order);
        else throw new IllegalArgumentException("Product is not in stock, please try again later");
        
    }

    private OrderLineItems mapToDto(OrderlinesItemsDto orderlinesItemsDto) {

        OrderLineItems items = new OrderLineItems();
        items.setPrice(orderlinesItemsDto.getPrice());
        items.setQuantity(orderlinesItemsDto.getQuantity());
        items.setSkuCode(orderlinesItemsDto.getSkuCode());

        return items;
    }
}
