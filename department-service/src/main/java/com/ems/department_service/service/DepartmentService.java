package com.ems.department_service.service;

import com.ems.department_service.dto.DepartmentRequest;
import com.ems.department_service.dto.DepartmentResponse;
import com.ems.department_service.entity.Department;
import com.ems.department_service.exception.ResourceNotFoundException;
import com.ems.department_service.feign.EmployeeClient;
import com.ems.department_service.repository.DepartmentRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepo;

    DepartmentService(DepartmentRepository departmentRepo){
        this.departmentRepo = departmentRepo;
    }

    @CacheEvict(value = "departments", allEntries = true)
    public DepartmentResponse createDepartment(DepartmentRequest request){

        if (departmentRepo.existsByName(request.getName())) {
            throw new RuntimeException("Department with name '"
                    + request.getName() + "' already exists");
        }
        Department departmentBuild = Department.builder()
                .name(request.getName())
                .location(request.getLocation())
                .description(request.getDescription())
                .build();
        Department saved = departmentRepo.save(departmentBuild);
        return mapToResponse(saved);
    }

    @Cacheable(value = "departments", key = "#id")
    public DepartmentResponse getDepartmentById(Long id){
        Department department = departmentRepo.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException(
                        "Department Not Found: "+ id
                ));

        return mapToResponse(department);
    }

    @Cacheable(value = "departments", key = "'all'")
    public List<DepartmentResponse> getAllDepartment(){
        return departmentRepo.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "departments", allEntries = true)
    public DepartmentResponse updateDepartment(Long id, DepartmentRequest request){
        Department department = departmentRepo.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException(
                        "Department Not Found: "+ id
                ));
        department.setName(request.getName());
        department.setLocation(request.getLocation());
        department.setDescription(request.getDescription());

        Department updated = departmentRepo.save(department);
        return mapToResponse(updated);
    }

    @CacheEvict(value = "departments", allEntries = true)
    public void deleteDepartment(Long id){
        Department department = departmentRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Department not found with id: " + id));
        departmentRepo.delete(department);
    }

    private DepartmentResponse mapToResponse(Department department) {
        return DepartmentResponse.builder()
                .id(department.getId())
                .name(department.getName())
                .description(department.getDescription())
                .location(department.getLocation())
                .createdAt(department.getCreatedAt().toString())
                .updatedAt(department.getUpdatedAt().toString())
                .build();
    }

}