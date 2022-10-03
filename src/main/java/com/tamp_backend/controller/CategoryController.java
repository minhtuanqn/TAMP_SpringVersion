package com.tamp_backend.controller;

import com.tamp_backend.model.ResponseModel;
import com.tamp_backend.model.category.CategoryFilterModel;
import com.tamp_backend.model.category.CategoryModel;
import com.tamp_backend.model.PaginationRequestModel;
import com.tamp_backend.model.ResourceModel;
import com.tamp_backend.model.category.CreateCategoryModel;
import com.tamp_backend.model.category.UpdateCategoryModel;
import com.tamp_backend.resolver.annotation.RequestPagingParam;
import com.tamp_backend.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@Validated
@RequestMapping(path = "/categories")
public class CategoryController {

    private CategoryService categoryService;

    public CategoryController(CategoryService categoryService){
        this.categoryService = categoryService;
    }

    /**
     * Create new category
     * @param requestModel
     * @return response entity contains created model
     */
    @PostMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<ResponseModel> createCategory(@Valid @RequestBody CreateCategoryModel requestModel) {
        CategoryModel savedModel = categoryService.createCategory(requestModel);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                .data(savedModel)
                .message("Created successfully");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * delete category
     * @param id
     * @return response entity contains deleted model
     */
    @DeleteMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<ResponseModel> deleteCategory(@PathVariable UUID id) {
        CategoryModel deletedModel = categoryService.deleteCategory(id);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                .data(deletedModel)
                .message("OK");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * Find category by id
     * @param id
     * @return response entity contains model
     */
    @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'SUPPLIER', 'PARTNER', 'AFFILIATOR')")
    public ResponseEntity<ResponseModel> findCategoryById(@PathVariable UUID id) {
        CategoryModel foundModel = categoryService.findCategoryById(id);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                .data(foundModel)
                .message("OK");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * Update category
     * @param requestModel
     * @return response entity contains model
     */
    @PutMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<ResponseModel> updateCategory(@Valid @RequestBody UpdateCategoryModel requestModel) {
        CategoryModel updatedModel = categoryService.updateCategory(requestModel);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                .data(updatedModel)
                .message("OK");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * Search categories by name
     * @param searchText
     * @param paginationRequestModel
     * @param categoryFilterModel
     * @return resource data of category
     */
    @GetMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'SUPPLIER', 'PARTNER', 'AFFILIATOR')")
    public ResponseEntity<ResponseModel> searchCategories(@RequestParam(value = "searchText", defaultValue = "") String searchText,
                                                          @RequestPagingParam PaginationRequestModel paginationRequestModel,
                                                          @ModelAttribute CategoryFilterModel categoryFilterModel) {
        ResourceModel<CategoryModel> categoryList = categoryService.searchCategories(searchText, paginationRequestModel, categoryFilterModel);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                .data(categoryList)
                .message("OK");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * Delete categories list by ids
     * @param ids
     * @return deleted categories list
     */
    @DeleteMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<ResponseModel> deleteCategories(@RequestBody List<UUID> ids) {
        List<CategoryModel> deletedModels = categoryService.deleteCategories(ids);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                .data(deletedModels)
                .message("OK");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }
}
