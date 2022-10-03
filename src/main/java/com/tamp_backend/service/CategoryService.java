package com.tamp_backend.service;

import com.tamp_backend.constant.EntityStatusEnum;
import com.tamp_backend.constant.StatusSearchEnum;
import com.tamp_backend.convertor.PaginationConvertor;
import com.tamp_backend.customexception.DuplicatedEntityException;
import com.tamp_backend.customexception.NoSuchEntityException;
import com.tamp_backend.entity.CategoryEntity;
import com.tamp_backend.metamodel.CategoryEntity_;
import com.tamp_backend.model.category.CategoryFilterModel;
import com.tamp_backend.model.category.CategoryModel;
import com.tamp_backend.model.PaginationRequestModel;
import com.tamp_backend.model.ResourceModel;
import com.tamp_backend.model.category.CreateCategoryModel;
import com.tamp_backend.model.category.UpdateCategoryModel;
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
     * @param createCategoryModel
     * @return created model
     */
    public CategoryModel createCategory(CreateCategoryModel createCategoryModel) {
        //Check existed category
        if(categoryRepository.existsCategoryEntityByName(createCategoryModel.getName())) {
            throw new DuplicatedEntityException("Duplicated name of category");
        }

        //Prepare entity
        CategoryEntity entity = modelMapper.map(createCategoryModel, CategoryEntity.class);
        entity.setStatus(EntityStatusEnum.CategoryStatusEnum.ACTIVE.ordinal());

        //Save entity to DB
        CategoryEntity savedEntity = categoryRepository.save(entity);
        CategoryModel  responseCategoryModel = modelMapper.map(savedEntity, CategoryModel.class);

        return responseCategoryModel;
    }

    /**
     * delete a category
     * @param id
     * @return delete model
     */
    public CategoryModel deleteCategory(UUID id) {
        //Find category with id
        Optional<CategoryEntity> deletedCategoryOptional = categoryRepository.findById(id);
        CategoryEntity deletedCategoryEntity = deletedCategoryOptional.orElseThrow(() -> new NoSuchEntityException("Not found category with id"));

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
     * @param updateCategoryModel
     * @return updated category
     */
    public CategoryModel updateCategory(UpdateCategoryModel updateCategoryModel) {
        //Find category with id
        Optional<CategoryEntity> searchedCategoryOptional = categoryRepository.findById(updateCategoryModel.getId());
        searchedCategoryOptional.orElseThrow(() -> new NoSuchEntityException("Not found category with id"));

        //Check existed category with name then update model
        if(categoryRepository.existsCategoryEntityByNameAndIdNot(updateCategoryModel.getName(), updateCategoryModel.getId())) {
            throw new DuplicatedEntityException("Duplicate name for category");
        }

        //Save entity to DB
        CategoryEntity savedEntity = categoryRepository.save(modelMapper.map(updateCategoryModel, CategoryEntity.class));
        return modelMapper.map(savedEntity, CategoryModel.class);
    }

    /**
     * Specification for search name
     * @param searchedValue
     * @return specification
     */
    private Specification<CategoryEntity> containsName(String searchedValue) {
        return ((root, query, criteriaBuilder) -> {
            String pattern = searchedValue != null ? "%" + searchedValue + "%" : "%" + "%";
            return criteriaBuilder.like(root.get(CategoryEntity_.NAME), pattern);
        });
    }

    /**
     * Specification for hasStatus
     * @param searchStatus
     * @return specification
     */
    private Specification<CategoryEntity> hasStatus(int searchStatus) {
        return ((root, query, criteriaBuilder) -> {
            if(searchStatus < StatusSearchEnum.CategoryStatusSearchEnum.ALL.ordinal()) {
                return criteriaBuilder.equal(root.get(CategoryEntity_.STATUS), searchStatus);
            } else {
                return criteriaBuilder.lessThan(root.get(CategoryEntity_.STATUS), StatusSearchEnum.CategoryStatusSearchEnum.ALL.ordinal());
            }
        });
    }

    /**
     * search category like name
     * @param searchedValue
     * @param paginationRequestModel
     * @return resource of data
     */
    public ResourceModel<CategoryModel> searchCategories(String searchedValue, PaginationRequestModel paginationRequestModel,
                                                         CategoryFilterModel categoryFilterModel) {
        PaginationConvertor<CategoryModel, CategoryEntity> paginationConvertor = new PaginationConvertor<>();

        String defaultSortBy = CategoryEntity_.NAME;
        Pageable pageable = paginationConvertor.convertToPageable(paginationRequestModel, defaultSortBy, CategoryEntity.class);

        //Find all categories
        Page<CategoryEntity> categoryEntityPage = categoryRepository.findAll(containsName(searchedValue)
                .and(hasStatus(categoryFilterModel.getStatusType()))
                .and(containsName(categoryFilterModel.getCategoryName())), pageable);

        //Convert list of categories entity to list of categories model
        List<CategoryModel> categoryModels = new ArrayList<>();
        for (CategoryEntity entity : categoryEntityPage) {
            categoryModels.add(modelMapper.map(entity, CategoryModel.class));
        }

        //Prepare resource for return
        ResourceModel<CategoryModel> resource = new ResourceModel<>();
        resource.setData(categoryModels);
        resource.setSearchText(searchedValue);
        resource.setSortBy(defaultSortBy);
        resource.setSortType(paginationRequestModel.getSortType());
        paginationConvertor.buildPagination(paginationRequestModel, categoryEntityPage, resource);
        return resource;
    }

    /**
     * Delete categories by ids
     * @param ids
     * @return deleted category models
     */
    public List<CategoryModel> deleteCategories(List<UUID> ids) {
        if(ids == null) throw new NoSuchEntityException("Not found any category");
        List<CategoryModel> deletedModels = new ArrayList<>();
        for (UUID id : ids) {
            CategoryModel categoryModel = deleteCategory(id);
            deletedModels.add(categoryModel);
        }

        return deletedModels;
    }

}
