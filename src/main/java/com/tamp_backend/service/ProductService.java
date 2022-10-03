package com.tamp_backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tamp_backend.constant.EntityStatusEnum;
import com.tamp_backend.constant.StatusSearchEnum;
import com.tamp_backend.constant.UserEnum;
import com.tamp_backend.convertor.PaginationConvertor;
import com.tamp_backend.customexception.DuplicatedEntityException;
import com.tamp_backend.customexception.NoSuchEntityException;
import com.tamp_backend.customexception.UnauthorizationException;
import com.tamp_backend.entity.*;
import com.tamp_backend.metamodel.CategoryEntity_;
import com.tamp_backend.metamodel.ProductEntity_;
import com.tamp_backend.metamodel.SupplierEntity_;
import com.tamp_backend.model.PaginationRequestModel;
import com.tamp_backend.model.ResourceModel;
import com.tamp_backend.model.product.CreateProductModel;
import com.tamp_backend.model.product.ProductFilterModel;
import com.tamp_backend.model.product.ProductModel;
import com.tamp_backend.model.product.UpdateProductStatusModel;
import com.tamp_backend.repository.CategoryRepository;
import com.tamp_backend.repository.ProductRepository;
import com.tamp_backend.repository.SupplierRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {
    private ProductRepository productRepository;
    private ModelMapper modelMapper;
    private SupplierRepository supplierRepository;
    private CategoryRepository categoryRepository;
    private ObjectMapper objectMapper;


    public ProductService(ProductRepository productRepository,
                          ModelMapper modelMapper,
                          SupplierRepository supplierRepository,
                          CategoryRepository categoryRepository,
                          ObjectMapper objectMapper) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
        this.supplierRepository = supplierRepository;
        this.categoryRepository = categoryRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * create new product
     * @param createProductModel
     * @param curSupplierId
     * @return saved product model
     * @throws JsonProcessingException
     */
    public ProductModel createProduct(CreateProductModel createProductModel, UUID curSupplierId) throws JsonProcessingException {
        //Check current supplier
        if(!curSupplierId.equals(createProductModel.getSupplierId())) {
            throw new UnauthorizationException("You can not access this resource");
        }

        //Check existed name of product
        if(productRepository.existsByName(createProductModel.getName()))
            throw new DuplicatedEntityException("Duplicated name of product");

        //Check exist supplier of product
        Optional<SupplierEntity> optionalSupplierEntity = supplierRepository.findById(createProductModel.getSupplierId());
        SupplierEntity supplierEntity = optionalSupplierEntity.orElseThrow(() -> new NoSuchEntityException("This supplier does not exist"));

        //Check exist category of product
        Optional<CategoryEntity> optionalCategoryEntity = categoryRepository.findById(createProductModel.getCategoryId());
        CategoryEntity categoryEntity = optionalCategoryEntity.orElseThrow(() -> new NoSuchEntityException("Not found category of product"));
        if(categoryEntity.getStatus() == EntityStatusEnum.CategoryStatusEnum.DISABLE.ordinal())
            throw new NoSuchEntityException("Not found category of product");

        //Prepare entity for save database
        ProductEntity productEntity = new ProductEntity();
        productEntity.setName(createProductModel.getName());
        productEntity.setDescription(createProductModel.getDescription());
        productEntity.setCategoryEntity(categoryEntity);
        productEntity.setSupplierEntity(supplierEntity);
        productEntity.setDefaultPrice(createProductModel.getDefaultPrice());
        productEntity.setStatus(EntityStatusEnum.ProductStatusEnum.PENDING.ordinal());
        productEntity.setCreateAt(LocalDateTime.now());
        productEntity.setApprovedBy(null);
        productEntity.setUpdateAt(LocalDateTime.now());
        if(createProductModel.getProductImgs() != null && createProductModel.getProductImgs().size() > 0) {
            productEntity.setImgList(objectMapper.writeValueAsString(createProductModel.getProductImgs()));
        } else {
            productEntity.setImgList("[]");
        }

        //Insert object to database
        ProductEntity savedProductEntity = productRepository.save(productEntity);
        ProductModel responseProductModel = modelMapper.map(savedProductEntity, ProductModel.class);
        responseProductModel.setCategoryId(createProductModel.getCategoryId());
        responseProductModel.setSupplierId(createProductModel.getSupplierId());
        return responseProductModel;
    }

    /**
     * Delete product
     * @param id
     * @param currentRoleEnum
     * @param userId
     * @return deletedProduct
     */
    public ProductModel deleteProduct(UUID id, UserEnum.RoleEnum currentRoleEnum, UUID userId) {
        //Find product with id
        Optional<ProductEntity> optionalProductEntity = productRepository.findById(id);
        ProductEntity productEntity = optionalProductEntity.orElseThrow(() -> new NoSuchEntityException("Not found product with id"));

        switch (currentRoleEnum) {
            case SYSTEM_ADMIN:
                break;
            case SUPPLIER:
                System.out.println(productEntity.getSupplierEntity().getId());
                System.out.println(userId);
                if(!userId.equals(productEntity.getSupplierEntity().getId()))
                    throw new NoSuchEntityException("You can not access this resource");
                break;
            default:
                throw new NoSuchEntityException("You can not access this resource");
        }

        //Set status for entity
        productEntity.setStatus(EntityStatusEnum.ProductStatusEnum.DELETED.ordinal());

        //Update status of category
        ProductEntity responseEntity = productRepository.save(productEntity);
        return modelMapper.map(responseEntity, ProductModel.class);
    }

    /**
     * Specification for search name
     * @param searchedValue
     * @return contains name specification
     */
    private Specification<ProductEntity> containsName(String searchedValue) {
        return ((root, query, criteriaBuilder) -> {
            String pattern = searchedValue != null ? "%" + searchedValue + "%" : "%" + "%";
            return criteriaBuilder.like(root.get(ProductEntity_.NAME), pattern);
        });
    }

    /**
     * Specification for filter status
     * @param statusType
     * @return status filter specification
     */
    private Specification<ProductEntity> statusFilter(int statusType) {
        return ((root, query, criteriaBuilder) -> {
            if (statusType < StatusSearchEnum.ProductStatusSearchEnum.ALL.ordinal()) {
                return criteriaBuilder.equal(root.get(ProductEntity_.STATUS), statusType);
            } else {
                return criteriaBuilder.lessThan(root.get(ProductEntity_.STATUS),
                        StatusSearchEnum.ProductStatusSearchEnum.ALL.ordinal());
            }
        });
    }

    /**
     * Specification for filter price greater
     * @param productFilterModel
     * @return price greater filter specification
     */
    private Specification<ProductEntity> greaterPriceFilter(ProductFilterModel productFilterModel) {
        return ((root, query, criteriaBuilder) -> {
            if (productFilterModel.isPriceFilter()) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get(ProductEntity_.DEFAULT_PRICE), productFilterModel.getMinPrice());
            } else {
                return criteriaBuilder.greaterThanOrEqualTo(root.get(ProductEntity_.DEFAULT_PRICE), 0);
            }
        });
    }

    /**
     * Specification for filter price less than
     * @param productFilterModel
     * @return price less then filter specification
     */
    private Specification<ProductEntity> lessThanPriceFilter(ProductFilterModel productFilterModel) {
        return ((root, query, criteriaBuilder) -> {
            if (productFilterModel.isPriceFilter()) {
                return criteriaBuilder.lessThanOrEqualTo(root.get(ProductEntity_.DEFAULT_PRICE), productFilterModel.getMaxPrice());
            } else {
                return criteriaBuilder.lessThanOrEqualTo(root.get(ProductEntity_.DEFAULT_PRICE), Double.MAX_VALUE);
            }
        });
    }

    /**
     * Specification for belong to supplier
     * @param supplierId
     * @return belong supplier specification
     */
    private Specification<ProductEntity> belongSupplier(UUID supplierId) {
        return (root, query, criteriaBuilder) -> {
            Join<SupplierEntity, ProductEntity> productJoins = root.join(ProductEntity_.SUPPLIER_ID);
            Predicate equalPredicate = supplierId != null
                    ? criteriaBuilder.equal(productJoins.get(SupplierEntity_.ID), supplierId)
                    : criteriaBuilder.isNotNull(root.get(SupplierEntity_.ID));
            query.distinct(true);
            return equalPredicate;
        };
    }

    /**
     * Specification for belong to category
     * @param categoryId
     * @return belong category specification
     */
    private Specification<ProductEntity> isCategory(UUID categoryId) {
        return (root, query, criteriaBuilder) -> {
            Join<CategoryEntity, ProductEntity> productJoins = root.join(ProductEntity_.CATEGORY_ID);
            Predicate equalPredicate = categoryId != null
                    ? criteriaBuilder.equal(productJoins.get(CategoryEntity_.ID), categoryId)
                    : criteriaBuilder.isNotNull(root.get(CategoryEntity_.ID));
            query.distinct(true);
            return equalPredicate;
        };
    }

    /**
     * Search and filter product
     * @param searchedValue
     * @param paginationRequestModel
     * @param productFilterModel
     * @return resource of data
     */
    public ResourceModel<ProductModel> searchProducts(String searchedValue, PaginationRequestModel paginationRequestModel,
                                                      ProductFilterModel productFilterModel) {
        PaginationConvertor<ProductModel, ProductEntity> paginationConvertor = new PaginationConvertor<>();

        String defaultSortBy = ProductEntity_.NAME;
        Pageable pageable = paginationConvertor.convertToPageable(paginationRequestModel, defaultSortBy, ProductEntity.class);

        //Find all partners
        Page<ProductEntity> productEntityPage = productRepository.findAll(containsName(searchedValue)
                .and(statusFilter(productFilterModel.getProductSearchStatusEnum()))
                .and(containsName(productFilterModel.getProductName()))
                .and(greaterPriceFilter(productFilterModel))
                .and(lessThanPriceFilter(productFilterModel))
                .and(belongSupplier(productFilterModel.getSupplierId()))
                .and(isCategory(productFilterModel.getCategoryId())), pageable);

        //Convert list of products entity to list of products model
        List<ProductModel> productModels = new ArrayList<>();
        for (ProductEntity entity : productEntityPage) {
            ProductModel productModel = modelMapper.map(entity, ProductModel.class);
            productModels.add(productModel);
        }

        //Prepare resource for return
        ResourceModel<ProductModel> resource = new ResourceModel<>();
        resource.setData(productModels);
        resource.setSearchText(searchedValue);
        resource.setSortBy(defaultSortBy);
        resource.setSortType(paginationRequestModel.getSortType());
        paginationConvertor.buildPagination(paginationRequestModel, productEntityPage, resource);
        return resource;
    }

    /**
     * Find product model by id
     * @param id
     * @return product model
     */
    public ProductModel findProductById(UUID id) {
        //Find product information by id
        Optional<ProductEntity> optionalProductEntity = productRepository.findById(id);
        ProductEntity productEntity = optionalProductEntity.orElseThrow(() -> new NoSuchEntityException("Not found product with id"));

        //Return product model
        ProductModel productModel = modelMapper.map(productEntity, ProductModel.class);
        return productModel;
    }

    /**
     * Change status of product
     * @param updateProductStatusModel
     * @return updated product model
     */
    public ProductModel updateProductStatus(UpdateProductStatusModel updateProductStatusModel) {
        //Find product information by id
        Optional<ProductEntity> optionalProductEntity = productRepository.findById(updateProductStatusModel.getId());
        ProductEntity productEntity = optionalProductEntity.orElseThrow(() -> new NoSuchEntityException("Not found product with id"));

        //Saved updated product
        productEntity.setStatus(updateProductStatusModel.getStatus());
        if(updateProductStatusModel.getStatus() == EntityStatusEnum.ProductStatusEnum.ACTIVE.ordinal()) {
            productEntity.setApprovedBy(UUID.fromString("e5facdba-b1b5-4077-8483-a9bfbb187955"));
        }
        else {
            productEntity.setApprovedBy(null);
        }
        ProductEntity insertProduct = productRepository.save(productEntity);

        //Return product model
        ProductModel productModel = modelMapper.map(productEntity, ProductModel.class);
        return productModel;
    }
}
