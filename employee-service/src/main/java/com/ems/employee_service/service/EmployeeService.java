package com.ems.employee_service.service;

import com.ems.employee_service.dto.DepartmentResponse;
import com.ems.employee_service.dto.EmployeeRequest;
import com.ems.employee_service.dto.EmployeeResponse;
import com.ems.employee_service.entity.Employee;
import com.ems.employee_service.exception.ResourceNotFoundException;
import com.ems.employee_service.feign.DepartmentClient;
import com.ems.employee_service.repository.EmployeeRepo;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private final EmployeeRepo employeeRepo;
    private final DepartmentClient departmentClient;

    EmployeeService(EmployeeRepo employeeRepo,
                    DepartmentClient departmentClient){
        this.employeeRepo = employeeRepo;
        this.departmentClient = departmentClient;
    }

    @CacheEvict(value = "employees", allEntries = true)
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        if (employeeRepo.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Employee with email '"
                    + request.getEmail() + "' already exists");
        }

        DepartmentResponse department = departmentClient
                .getDepartmentById(request.getDepartmentId());

        Employee employee = Employee.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .jobTitle(request.getJobTitle())
                .salary(request.getSalary())
                .departmentId(request.getDepartmentId())
                .build();

        Employee saved = employeeRepo.save(employee);
        return mapToResponse(saved, department);
    }

    @Cacheable(value = "employees", key = "#id")
    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = employeeRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with id: " + id));

        DepartmentResponse department = departmentClient
                .getDepartmentById(employee.getDepartmentId());

        return mapToResponse(employee, department);
    }

    @Cacheable(value = "employees", key = "'all'")
    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepo.findAll()
                .stream()
                .map(employee -> {
                    DepartmentResponse department = departmentClient
                            .getDepartmentById(employee.getDepartmentId());
                    return mapToResponse(employee, department);
                })
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "employees", allEntries = true)
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        Employee employee = employeeRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with id: " + id));

        DepartmentResponse department = departmentClient
                .getDepartmentById(request.getDepartmentId());

        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setPhone(request.getPhone());
        employee.setJobTitle(request.getJobTitle());
        employee.setSalary(request.getSalary());
        employee.setDepartmentId(request.getDepartmentId());

        Employee updated = employeeRepo.save(employee);
        return mapToResponse(updated, department);
    }

    @CacheEvict(value = "employees", allEntries = true)
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with id: " + id));
        employeeRepo.delete(employee);
    }

    public List<EmployeeResponse> getEmployeesByDepartment(Long departmentId) {
        DepartmentResponse department = departmentClient
                .getDepartmentById(departmentId);

        return employeeRepo.findByDepartmentId(departmentId)
                .stream()
                .map(employee -> mapToResponse(employee, department))
                .collect(Collectors.toList());
    }


    private EmployeeResponse mapToResponse(Employee employee,
                                           DepartmentResponse department) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .phone(employee.getPhone())
                .jobTitle(employee.getJobTitle())
                .salary(employee.getSalary())
                .departmentId(employee.getDepartmentId())
                .departmentName(department.getName())
                .departmentLocation(department.getLocation())
                .createdAt(employee.getCreatedAt().toString())
                .updatedAt(employee.getUpdatedAt().toString())
                .build();
    }
}
