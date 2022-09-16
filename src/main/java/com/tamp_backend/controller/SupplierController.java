package com.tamp_backend.controller;

import com.tamp_backend.model.CategoryModel;
import com.tamp_backend.model.ResponseModel;
import com.tamp_backend.model.supplier.CreateSupplierModel;
import com.tamp_backend.model.supplier.SupplierModel;
import com.tamp_backend.service.SupplierService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import javax.validation.Valid;

@RestController
@Validated
@RequestMapping(path = "/suppliers")
public class SupplierController {
    private SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @PostMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseModel> createCategory(@Valid @ModelAttribute CreateSupplierModel requestModel, @RequestPart MultipartFile logo) {
        SupplierModel savedSupplierModel = supplierService.createSupplier(requestModel, "");
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                .data(savedSupplierModel)
                .message("Created successfully");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }
}
