package com.ms.orderservice.service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.ms.orderservice.dto.InventoryResponse;
import com.ms.orderservice.dto.OrderLineItemsDto;
import com.ms.orderservice.dto.OrderRequest;
import com.ms.orderservice.model.Order;
import com.ms.orderservice.model.OrderLineItems;
import com.ms.orderservice.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
	
	private final OrderRepository orderRepository;
	private final WebClient.Builder webClientBuilder;
	
	public String placeOrder(OrderRequest orderRequest) {
		Order order = new Order();
		order.setOrderNumber(UUID.randomUUID().toString());
		
		List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
			.stream()
			.map(orderLineItemsDto -> mapToDto(orderLineItemsDto)).toList();
		order.setOrderLineItems(orderLineItems);
		
		List<String> skuCodes = order.getOrderLineItems().stream()
			.map(orderLineItem -> orderLineItem.getSkuCode())
			.toList();
		// Call inventory service, and place order if product is in stock
		InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
			.uri("http://inventory-service/api/inventory", 
					uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
			.retrieve()
			.bodyToMono(InventoryResponse[].class)
			.block();
		// bodyToMono => will tell the type of response we are expecting from the
		// end point and, block => this means webclient will make synchronous request to the 
		// uri mentioned, by default the requesting is Asynchronous.
		boolean allProductsInStock = Arrays.stream(inventoryResponseArray).allMatch(inventoryResponse -> inventoryResponse.isInStock());
		
		if(allProductsInStock) {
			orderRepository.save(order);
			return "Order Placed Successfully";
		}else {
			throw new IllegalArgumentException("Product is not in Stock, please try again later.");
		}
		
	}
	
	private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
		OrderLineItems orderLineItems = new OrderLineItems();
		orderLineItems.setPrice(orderLineItemsDto.getPrice());
		orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
		orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
		return orderLineItems;
	}
	
}
