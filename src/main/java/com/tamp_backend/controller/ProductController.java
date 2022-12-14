package com.tamp_backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tamp_backend.constant.UserEnum;
import com.tamp_backend.model.PaginationRequestModel;
import com.tamp_backend.model.ResourceModel;
import com.tamp_backend.model.ResponseModel;
import com.tamp_backend.model.category.CategoryModel;
import com.tamp_backend.model.partner.PartnerModel;
import com.tamp_backend.model.partnertype.PartnerTypeFilterModel;
import com.tamp_backend.model.partnertype.PartnerTypeModel;
import com.tamp_backend.model.product.CreateProductModel;
import com.tamp_backend.model.product.ProductFilterModel;
import com.tamp_backend.model.product.ProductModel;
import com.tamp_backend.model.product.UpdateProductStatusModel;
import com.tamp_backend.model.supplier.SupplierModel;
import com.tamp_backend.resolver.annotation.RequestPagingParam;
import com.tamp_backend.service.ProductService;
import com.tamp_backend.service.SupplierService;
import com.tamp_backend.service.SystemAdminService;
import com.tamp_backend.utils.UserUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@Validated
@RequestMapping(path = "/products")
public class ProductController {
    private ProductService productService;
    private SupplierService supplierService;
    private SystemAdminService systemAdminService;

    public ProductController(ProductService productService,
                             SupplierService supplierService,
                             SystemAdminService systemAdminService) {
        this.productService = productService;
        this.supplierService = supplierService;
        this.systemAdminService = systemAdminService;
    }

    /**
     * Create new product
     * @param requestModel
     * @return response model contains product
     */
    @PostMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('SUPPLIER')")
    public ResponseEntity<ResponseModel> createProduct(@Valid @ModelAttribute CreateProductModel requestModel) throws JsonProcessingException {
        String username = UserUtils.getCurUsername();
        SupplierModel supplierModel = supplierService.findSupplierByUsername(username);
        ProductModel savedProductModel = productService.createProduct(requestModel, supplierModel.getId());
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                .data(savedProductModel)
                .message("Created successfully");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * Delete product
     * @param id
     * @return deleted product model
     */
    @DeleteMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'SUPPLIER')")
    public ResponseEntity<ProductModel> deleteProduct(@PathVariable UUID id) {
        String username = UserUtils.getCurUsername();
        String curRole = UserUtils.getCurRole();
        UUID curUserId = null;
        if(curRole.equals(UserEnum.RoleEnum.SYSTEM_ADMIN.toString())) {
            curUserId = systemAdminService.findSystemAdminByUsername(username).getId();
        } else {
            curUserId = supplierService.findSupplierByUsername(username).getId();
        }
        ProductModel deletedModel = productService.deleteProduct(id, Enum.valueOf(UserEnum.RoleEnum.class, curRole), curUserId);
        return new ResponseEntity<>(deletedModel, HttpStatus.OK);
    }

    /**
     * search product with conditions
     * @param searchText
     * @param paginationRequestModel
     * @param productFilterModel
     * @return list of product
     */
    @GetMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'SUPPLIER')")
    public ResponseEntity<ResponseModel> searchProducts(@RequestParam(value = "searchText", defaultValue = "") String searchText,
                                                           @RequestPagingParam PaginationRequestModel paginationRequestModel,
                                                           @ModelAttribute ProductFilterModel productFilterModel) {
        ResourceModel<ProductModel> productList = productService
                .searchProducts(searchText, paginationRequestModel, productFilterModel);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                .data(productList)
                .message("OK");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * Find product by id
     * @param id
     * @return response model contains product model
     */
    @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PARTNER')")
    public ResponseEntity<ResponseModel> findProductById(@PathVariable UUID id) {
        ProductModel productModel = productService.findProductById(id);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value()).data(productModel).message("OK");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * Update status of product
     * @param updateProductStatusModel
     * @return response model contains updated product model
     */
    @PatchMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<ResponseModel> updateStatusProduct(@RequestBody UpdateProductStatusModel updateProductStatusModel) {
        ProductModel productModel = productService.updateProductStatus(updateProductStatusModel);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value()).data(productModel).message("OK");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }
}
