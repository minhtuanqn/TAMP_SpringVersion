package com.tamp_backend.service;

import com.tamp_backend.constant.EntityStatusEnum;
import com.tamp_backend.convertor.PaginationConvertor;
import com.tamp_backend.customexception.DuplicatedEntityException;
import com.tamp_backend.customexception.NoSuchEntityException;
import com.tamp_backend.entity.CategoryEntity;
import com.tamp_backend.metamodel.CategoryEntity_;
import com.tamp_backend.model.category.CategoryModel;
import com.tamp_backend.model.PaginationRequestModel;
import com.tamp_backend.model.ResourceModel;
import com.tamp_backend.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CategoryService {

    private CategoryRepository categoryRepository;
    private ModelMapper modelMapper;

    public CategoryService(CategoryRepository categoryRepository,
                           ModelMapper modelMapper)
    {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * create a category
     * @param model
     * @return created model
     */
    public CategoryModel createCategory(CategoryModel model) {
        //Check existed category
        if(categoryRepository.existsCategoryEntityByName(model.getName())) {
            throw new DuplicatedEntityException("This category has been existed");
        }

        //Prepare entity
        CategoryEntity entity = modelMapper.map(model, CategoryEntity.class);
        entity.setStatus(EntityStatusEnum.CategoryStatusEnum.ACTIVE.ordinal());

        //Save entity to DB
        CategoryEntity savedEntity = categoryRepository.save(entity);
        model = modelMapper.map(savedEntity, CategoryModel.class);

        return model;
    }

    /**
     * delete a category
     * @param id
     * @return delete model
     */
    public CategoryModel deleteCategory(UUID id) {
        //Find category with id
        Optional<CategoryEntity> deletedCategoryOptional = categoryRepository.findById(id);
        CategoryEntity deletedCategoryEntity = deletedCategoryOptional.orElseThrow(() -> new NoSuchEntityException("Not found category"));

        //Set status for entity
        deletedCategoryEntity.setStatus(EntityStatusEnum.CategoryStatusEnum.DISABLE.ordinal());

        //Update status of category
        CategoryEntity responseEntity = categoryRepository.save(deletedCategoryEntity);
        return modelMapper.map(responseEntity, CategoryModel.class);
    }

    /**
     * Find a category by id
     * @param id
     * @return found model
     */
    public CategoryModel findCategoryById(UUID id) {
        //Find category with id
        Optional<CategoryEntity> searchedCategoryOptional = categoryRepository.findById(id);
        CategoryEntity categoryEntity = searchedCategoryOptional.orElseThrow(() -> new NoSuchEntityException("Not found category"));
        return modelMapper.map(categoryEntity, CategoryModel.class);
    }

    /**
     * Update category
     * @param id
     * @param categoryModel
     * @return updated category
     */
    public CategoryModel updateCategory(UUID id, CategoryModel categoryModel) {
        //Find category with id
        Optional<CategoryEntity> searchedCategoryOptional = categoryRepository.findById(id);
        searchedCategoryOptional.orElseThrow(() -> new NoSuchEntityException("Not found category"));

        //Check existed category with name then update model
        if(categoryRepository.existsCategoryEntityByNameAndIdNot(categoryModel.getName(), id)) {
            throw new DuplicatedEntityException("This category existed");
        }

        //Prepare entity for saving to DB
        categoryModel.setId(id);

        //Save entity to DB
        CategoryEntity savedEntity = categoryRepository.save(modelMapper.map(categoryModel, CategoryEntity.class));
        return modelMapper.map(savedEntity, CategoryModel.class);
    }

    /**
     * Specification for search name
     * @param searchedValue
     * @return specification
     */
    private Specification<CategoryEntity> containsName(String searchedValue) {
        return ((root, query, criteriaBuilder) -> {
            String pattern = "%" + searchedValue + "%";
            return criteriaBuilder.like(root.get(CategoryEntity_.NAME), pattern);
        });
    }

    /**
     * search category like name
     * @param searchedValue
     * @param paginationRequestModel
     * @return resource of data
     */
    public ResourceModel<CategoryModel> searchCategories(String searchedValue, PaginationRequestModel paginationRequestModel) {
        PaginationConvertor<CategoryModel, CategoryEntity> paginationConvertor = new PaginationConvertor<>();

        String defaultSortBy = CategoryEntity_.NAME;
        Pageable pageable = paginationConvertor.convertToPageable(paginationRequestModel, defaultSortBy, CategoryEntity.class);

        //Find all categories
        Page<CategoryEntity> categoryEntityPage = categoryRepository.findAll(containsName(searchedValue), pageable);

        //Convert list of categories entity to list of categories model
        List<CategoryModel> categoryModels = new ArrayList<>();
        for (CategoryEntity entity : categoryEntityPage) {
            categoryModels.add(modelMapper.map(entity, CategoryModel.class));
        }

        //Prepare resource for return
        ResourceModel<CategoryModel> resource = new ResourceModel<>();
        resource.setData(categoryModels);
        paginationConvertor.buildPagination(paginationRequestModel, categoryEntityPage, resource);
        return resource;
    }

}
