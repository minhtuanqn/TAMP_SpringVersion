package com.tamp_backend.controller;

import com.tamp_backend.model.CategoryModel;
import com.tamp_backend.model.PaginationRequestModel;
import com.tamp_backend.model.ResourceModel;
import com.tamp_backend.resolver.annotation.RequestPagingParam;
import com.tamp_backend.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.UUID;

@RestController
@Validated
@RequestMapping(path = "/categories")
@CrossOrigin
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
    public ResponseEntity<CategoryModel> createCategory(@Valid @RequestBody CategoryModel requestModel) {
        CategoryModel savedModel = categoryService.createCategory(requestModel);
        return new ResponseEntity<>(savedModel, HttpStatus.OK);
    }

    /**
     * delete category
     * @param id
     * @return response entity contains deleted model
     */
    @DeleteMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<CategoryModel> deleteCategory(@PathVariable UUID id) {
        CategoryModel deletedModel = categoryService.deleteCategory(id);
        return new ResponseEntity<>(deletedModel, HttpStatus.OK);
    }

    /**
     * Find category by id
     * @param id
     * @return response entity contains model
     */
    @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<CategoryModel> findCategoryById(@PathVariable UUID id) {
        CategoryModel model = categoryService.findCategoryById(id);
        return new ResponseEntity<>(model, HttpStatus.OK);
    }

    /**
     * Update category
     * @param id
     * @param requestModel
     * @return response entity contains model
     */
    @PutMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<CategoryModel> updateCategory(@PathVariable UUID id,
                                                        @Valid @RequestBody CategoryModel requestModel) {
        CategoryModel updatedModel = categoryService.updateCategory(id, requestModel);
        return new ResponseEntity<>(updatedModel, HttpStatus.OK);
    }

    /**
     * Search categories by name
     * @param searchedValue
     * @param paginationRequestModel
     * @return resource data of category
     */
    @GetMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> searchCategories(@RequestParam(value = "searchedValue", defaultValue = "") String searchedValue,
                                                  @RequestPagingParam PaginationRequestModel paginationRequestModel) {
        ResourceModel<CategoryModel> categoryList = categoryService.searchCategories(searchedValue, paginationRequestModel);
        return new ResponseEntity<>(categoryList, HttpStatus.OK);
    }
}
