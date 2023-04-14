package com.ms.productservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ms.productservice.dto.ProductRequest;
import com.ms.productservice.dto.ProductResponse;
import com.ms.productservice.model.Product;
import com.ms.productservice.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
	
	private final ProductRepository productRepository;
	
	/*public ProductService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}
	INSTEAD OF MANUALLY INJECTING INTO THE CONSTRUCTOR, WE CAN USE REQIREDARGSCONSTRUCTOR 
	FROM LOMBOK. THIS WILL CREATE CONSTRUCTOR AT THE COMPILE TIME WITH ALL THE REQUIRED ARGS.
	*/


	public void createProduct(ProductRequest productRequest) {
		Product product = Product.builder()
				.name(productRequest.getName())
				.description(productRequest.getDescription())
				.price(productRequest.getPrice())
				.build();
		productRepository.save(product);
		log.info("Product {} is saved", product.getId());
	}
	
	public List<ProductResponse> getAllProducts(){
		
		List<Product> products = productRepository.findAll();
		//return products.stream().map(product -> mapToProductResponse(product)).toList();
		return products.stream().map(this::mapToProductResponse).toList();
		
	}
	
	private ProductResponse mapToProductResponse(Product product) {
		return ProductResponse.builder()
				.id(product.getId())
				.name(product.getName())
				.description(product.getDescription())
				.price(product.getPrice())
				.build();
	}
	
	
}
