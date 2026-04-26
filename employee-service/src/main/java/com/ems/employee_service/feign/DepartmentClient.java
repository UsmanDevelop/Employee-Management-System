package com.ems.employee_service.feign;

import com.ems.employee_service.dto.DepartmentResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "department-service")
public interface DepartmentClient {

    @CircuitBreaker(name = "departmentService", fallbackMethod = "getDepartmentFallback")
    @GetMapping("/api/departments/{id}")
    DepartmentResponse getDepartmentById(@PathVariable Long id);

    default DepartmentResponse getDepartmentFallback(Long id, Exception ex) {
        return DepartmentResponse.builder()
                .id(id)
                .name("Department Unavailable")
                .description("Department service is currently down")
                .location("N/A")
                .build();
    }
}
