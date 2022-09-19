package com.tamp_backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tamp_backend.constant.EntityStatusEnum;
import com.tamp_backend.constant.UserEnum;
import com.tamp_backend.customexception.DuplicatedEntityException;
import com.tamp_backend.customexception.NoSuchEntityException;
import com.tamp_backend.customexception.UnauthorizationException;
import com.tamp_backend.entity.CategoryEntity;
import com.tamp_backend.entity.ProductEntity;
import com.tamp_backend.entity.SupplierEntity;
import com.tamp_backend.model.product.CreateProductModel;
import com.tamp_backend.model.product.ProductModel;
import com.tamp_backend.repository.CategoryRepository;
import com.tamp_backend.repository.ProductRepository;
import com.tamp_backend.repository.SupplierRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
}
