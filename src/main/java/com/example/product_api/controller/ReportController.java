package com.example.product_api.controller;

import com.example.product_api.dto.SalesReportResponseDTO;
import com.example.product_api.dto.TopProductResponseDTO;
import com.example.product_api.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/sales")
    public ResponseEntity<SalesReportResponseDTO> getSalesReport() {
        SalesReportResponseDTO report = reportService.getSalesReport();
        return ResponseEntity.ok(report);
    }

    @GetMapping("/top-products")
    public ResponseEntity<List<TopProductResponseDTO>> getTopSellingProducts() {
        List<TopProductResponseDTO> topProducts = reportService.getTopSellingProducts();
        return ResponseEntity.ok(topProducts);
    }
}
