package com.example.teachermanagement.controller;

import com.example.teachermanagement.model.ApiResponse;
import com.example.teachermanagement.model.ManagementRecord;
import com.example.teachermanagement.service.ManagementService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ManagementController {
    private final ManagementService managementService;

    public ManagementController(ManagementService managementService) {
        this.managementService = managementService;
    }

    @GetMapping("/dashboard")
    public ApiResponse<Map<String, Integer>> dashboard() {
        return ApiResponse.ok(managementService.statistics());
    }

    @GetMapping("/{module}")
    public ApiResponse<List<ManagementRecord>> list(@PathVariable String module) {
        return ApiResponse.ok(managementService.list(module));
    }

    @PostMapping("/{module}")
    public ApiResponse<ManagementRecord> create(@PathVariable String module, @RequestBody Map<String, Object> fields) {
        return ApiResponse.created(managementService.create(module, fields));
    }

    @PutMapping("/{module}/{id}")
    public ApiResponse<ManagementRecord> update(
            @PathVariable String module,
            @PathVariable Long id,
            @RequestBody Map<String, Object> fields
    ) {
        return ApiResponse.ok(managementService.update(module, id, fields));
    }

    @DeleteMapping("/{module}/{id}")
    public ApiResponse<Void> delete(@PathVariable String module, @PathVariable Long id) {
        managementService.delete(module, id);
        return ApiResponse.ok(null);
    }
}
