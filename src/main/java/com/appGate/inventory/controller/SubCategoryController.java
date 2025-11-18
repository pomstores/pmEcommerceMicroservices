package com.appGate.inventory.controller;

import com.appGate.inventory.dto.SubCategoryDto;
import com.appGate.inventory.service.SubCategoryService;
import org.springframework.web.bind.annotation.*;

import com.appGate.inventory.dto.CategoryDto;
import com.appGate.inventory.response.BaseResponse;
import com.appGate.inventory.service.CategoryService;

@RestController
@RequestMapping(path = "/api")
public class SubCategoryController {

    public SubCategoryController(SubCategoryService subCategoryService) {
        this.subCategoryService = subCategoryService;
    }

    private final SubCategoryService subCategoryService;

    @PostMapping("/admin/sub-categories")
    public BaseResponse createCategory(@RequestBody SubCategoryDto subCategoryDto) {
        return subCategoryService.createSubCategory(subCategoryDto);
    }

    @GetMapping("/admin/sub-categories")
    public BaseResponse getAllCategories() {
        return subCategoryService.getAllSubCategories();
    }

    @GetMapping("/admin/sub-categories/{id}")
    public BaseResponse getCategory(@PathVariable Long id) {
        return subCategoryService.getSubCategory(id);
    }

    @PutMapping("/admin/sub-categories/{id}")
    public BaseResponse updateCategory(@PathVariable Long id, @RequestBody SubCategoryDto subCategoryDto) {
        return subCategoryService.updateSubCategory(id, subCategoryDto);
    }
    
}
