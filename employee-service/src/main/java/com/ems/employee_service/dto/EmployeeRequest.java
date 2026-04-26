package com.ems.employee_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String jobTitle;
    private Double salary;
    private Long departmentId;
}
