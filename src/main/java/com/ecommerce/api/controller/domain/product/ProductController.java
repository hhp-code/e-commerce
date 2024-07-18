package com.ecommerce.api.controller.domain.product;

import com.ecommerce.api.controller.domain.order.dto.OrderDto;
import com.ecommerce.api.controller.domain.product.dto.ProductDto;
import com.ecommerce.api.controller.domain.product.dto.ProductMapper;
import com.ecommerce.api.controller.usecase.PopularProductCase;
import com.ecommerce.domain.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Product", description = "상품 관련 API")
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final PopularProductCase popularProductCase;

    public ProductController(ProductService productService, PopularProductCase popularProductCase) {
        this.productService = productService;
        this.popularProductCase = popularProductCase;
    }

    @GetMapping
    @Operation(summary = "상품 목록 조회", description = "전체 상품 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 상품 목록을 조회함",
                    content = @Content(schema = @Schema(implementation = ProductDto.ProductListResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ProductDto.ProductListResponse getProducts() {
        return ProductMapper.toProductListResponse(productService.getProducts());
    }

    @GetMapping("/{productId}")
    @Operation(summary = "상품 상세 조회", description = "특정 상품의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 상품을 조회함",
                    content = @Content(schema = @Schema(implementation = ProductDto.ProductResponse.class))),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ProductDto.ProductResponse getProduct(
            @Parameter(description = "조회할 상품의 ID", required = true) @PathVariable Long productId) {
        return ProductMapper.toProductResponse(productService.getProduct(productId));
    }

    @GetMapping("/popular")
    @Operation(summary = "인기 상품 조회", description = "인기 있는 상품들의 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 인기 상품 목록을 조회함",
                    content = @Content(schema = @Schema(implementation = ProductDto.ProductListResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public OrderDto.PopularListResponse getPopularProducts() {
        return ProductMapper.toPopulartListResponse(popularProductCase.getPopularProducts());
    }
}