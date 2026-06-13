package com.example.product_api.service;

import com.example.product_api.dto.SalesReportResponseDTO;
import com.example.product_api.dto.TopProductResponseDTO;
import java.util.List;

public interface ReportService {
    SalesReportResponseDTO getSalesReport();
    List<TopProductResponseDTO> getTopSellingProducts();
}
