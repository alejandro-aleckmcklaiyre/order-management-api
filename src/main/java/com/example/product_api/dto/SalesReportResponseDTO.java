package com.example.product_api.dto;

public class SalesReportResponseDTO {
    private Long totalOrders;
    private Double totalRevenue;

    public SalesReportResponseDTO() {
    }

    public SalesReportResponseDTO(Long totalOrders, Double totalRevenue) {
        this.totalOrders = totalOrders;
        this.totalRevenue = totalRevenue;
    }

    public Long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}
