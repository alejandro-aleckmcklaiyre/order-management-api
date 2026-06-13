package com.example.product_api.dto;

public class TopProductResponseDTO {
    private Long productId;
    private String productName;
    private Long totalQuantitySold;
    private Double totalRevenue;

    public TopProductResponseDTO() {
    }

    public TopProductResponseDTO(Long productId, String productName, Long totalQuantitySold, Double totalRevenue) {
        this.productId = productId;
        this.productName = productName;
        this.totalQuantitySold = totalQuantitySold;
        this.totalRevenue = totalRevenue;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Long getTotalQuantitySold() {
        return totalQuantitySold;
    }

    public void setTotalQuantitySold(Long totalQuantitySold) {
        this.totalQuantitySold = totalQuantitySold;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}
