package com.programingish.order_service.service;

import com.programingish.order_service.dto.InventoryResponse;
import com.programingish.order_service.dto.OrderRequest;
import com.programingish.order_service.dto.OrderlinesItemsDto;
import com.programingish.order_service.model.Order;
import com.programingish.order_service.model.OrderLineItems;
import com.programingish.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final WebClient.Builder webClientBuilder;

    public ResponseEntity<String> placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> orderLineItems = orderRequest.getOrderlinesItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();
        order.setOrderLineItemsList(orderLineItems);

        // Call Inventory service and place order if products are in stock
        List<String> skuCodes = order.getOrderLineItemsList().stream()
                .map(OrderLineItems::getSkuCode)
                .toList();

        InventoryResponse[] inventoryResponses = webClientBuilder.build()
                .get()
                .uri("http://inventory-service/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        boolean allInStock = inventoryResponses != null && inventoryResponses.length > 0 &&
                Arrays.stream(inventoryResponses).allMatch(InventoryResponse::isInStock);

        if (allInStock) {
            orderRepository.save(order);
            return ResponseEntity.ok("Order placed successfully");
        } else {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Product is not in stock, please try again later");
        }
    }

    private OrderLineItems mapToDto(OrderlinesItemsDto orderlinesItemsDto) {
        OrderLineItems items = new OrderLineItems();
        items.setPrice(orderlinesItemsDto.getPrice());
        items.setQuantity(orderlinesItemsDto.getQuantity());
        items.setSkuCode(orderlinesItemsDto.getSkuCode());
        return items;
    }
}
