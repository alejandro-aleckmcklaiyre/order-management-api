package com.example.product_api.service.impl;

import com.example.product_api.dto.SalesReportResponseDTO;
import com.example.product_api.dto.TopProductResponseDTO;
import com.example.product_api.repository.OrderRepository;
import com.example.product_api.service.ReportService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    private final OrderRepository orderRepository;

    public ReportServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public SalesReportResponseDTO getSalesReport() {
        Long totalOrders = orderRepository.countNonCancelledOrders();
        Double totalRevenue = orderRepository.sumNonCancelledRevenue();
        return new SalesReportResponseDTO(totalOrders, totalRevenue);
    }

    @Override
    public List<TopProductResponseDTO> getTopSellingProducts() {
        return orderRepository.findTopSellingProducts();
    }
}
