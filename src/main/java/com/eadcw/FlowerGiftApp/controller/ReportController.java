package com.eadcw.FlowerGiftApp.controller;

import com.eadcw.FlowerGiftApp.dto.SalesReportDTO;
import com.eadcw.FlowerGiftApp.service.SalesReportService;
import com.eadcw.FlowerGiftApp.service.SalesReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    @Autowired
    private SalesReportService reportService;

    @GetMapping("/sales")
    public ResponseEntity<?> getSalesReport(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        try {
            if (startDate.isAfter(endDate)) {
                return ResponseEntity.badRequest()
                        .body(createErrorMap("Start date must be before end date"));
            }

            SalesReportDTO report = reportService.generateSalesReport(startDate, endDate);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorMap("Failed to generate report: " + e.getMessage()));
        }
    }

    @PostMapping("/download")
    public ResponseEntity<?> downloadReport(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate,
            @RequestParam(defaultValue = "CSV") String format) {
        try {
            if (startDate.isAfter(endDate)) {
                return ResponseEntity.badRequest()
                        .body(createErrorMap("Start date must be before end date"));
            }

            // For now, return the same report data
            // Frontend can handle CSV/PDF conversion
            SalesReportDTO report = reportService.generateSalesReport(startDate, endDate);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorMap("Failed to download report: " + e.getMessage()));
        }
    }

    private Map<String, String> createErrorMap(String error) {
        Map<String, String> map = new HashMap<>();
        map.put("error", error);
        return map;
    }
}