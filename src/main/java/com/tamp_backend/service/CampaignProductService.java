package com.tamp_backend.service;

import com.tamp_backend.constant.EntityStatusEnum;
import com.tamp_backend.constant.UserEnum;
import com.tamp_backend.convertor.PaginationConvertor;
import com.tamp_backend.customexception.NoSuchEntityException;
import com.tamp_backend.customexception.RangeTimeException;
import com.tamp_backend.customexception.UnauthorizationException;
import com.tamp_backend.entity.*;
import com.tamp_backend.metamodel.CampaignCategoryEntity_;
import com.tamp_backend.model.PaginationRequestModel;
import com.tamp_backend.model.ResourceModel;
import com.tamp_backend.model.campaigncategory.CampaignCategoryFilterModel;
import com.tamp_backend.model.campaigncategory.CampaignCategoryResponseModel;
import com.tamp_backend.model.campaignproduct.CampaignProductFilterModel;
import com.tamp_backend.model.campaignproduct.CampaignProductModel;
import com.tamp_backend.model.campaignproduct.CampaignProductResponseModel;
import com.tamp_backend.model.campaignproduct.CreateCampaignProductModel;
import com.tamp_backend.model.orthermodel.CommissionRateModel;
import com.tamp_backend.model.product.ProductModel;
import com.tamp_backend.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CampaignProductService {

    private CampaignProductRepository campaignProductRepository;
    private CampaignRepository campaignRepository;
    private ProductRepository productRepository;
    private CampaignCategoryRepository campaignCategoryRepository;
    private PartnerRepository partnerRepository;
    private AffiliatorRepository affiliatorRepository;
    private AffiliatorTypeRepository affiliatorTypeRepository;
    private PartnerTypeRepository partnerTypeRepository;
    private ModelMapper modelMapper;

    public CampaignProductService(CampaignProductRepository campaignProductRepository, CampaignRepository campaignRepository, ProductRepository productRepository, CampaignCategoryRepository campaignCategoryRepository, PartnerRepository partnerRepository, AffiliatorRepository affiliatorRepository, AffiliatorTypeRepository affiliatorTypeRepository, PartnerTypeRepository partnerTypeRepository, ModelMapper modelMapper) {
        this.campaignProductRepository = campaignProductRepository;
        this.campaignRepository = campaignRepository;
        this.productRepository = productRepository;
        this.campaignCategoryRepository = campaignCategoryRepository;
        this.partnerRepository = partnerRepository;
        this.affiliatorRepository = affiliatorRepository;
        this.affiliatorTypeRepository = affiliatorTypeRepository;
        this.partnerTypeRepository = partnerTypeRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Add product to campaign
     * @param campaignId
     * @param createCampaignProductModel
     * @param currentRoleEnum
     * @param userId
     * @return list of added campaign product models
     */
    public List<CampaignProductModel> addProductsToCampaign(UUID campaignId, CreateCampaignProductModel createCampaignProductModel,
                                                     UserEnum.RoleEnum currentRoleEnum, UUID userId) {
        //Check existed campaign
        Optional<CampaignEntity> optionalCampaign = campaignRepository.findById(campaignId);
        CampaignEntity campaign = optionalCampaign.orElseThrow(() -> new NoSuchEntityException("Not found campaign with id"));

        //Check condition stage for add product to campaign
        if (campaign.getStatus() != EntityStatusEnum.CampaignStatusEnum.PRODUCT_PREPARING.ordinal()
                && campaign.getStatus() != EntityStatusEnum.CampaignStatusEnum.READY.ordinal())
            throw new RangeTimeException("Just add product to campaign when status of campaign is product preparing or ready");

        List<UUID> addedProductIds = createCampaignProductModel.getProductIds();
        List<CampaignProductEntity> campaignProducts = new ArrayList<>();
        List<CampaignProductModel> savedModels = new ArrayList<>();
        for (UUID productId: addedProductIds) {
            Optional<ProductEntity> optionalProduct = productRepository.findById(productId);
            ProductEntity product = optionalProduct.orElseThrow(() -> new NoSuchEntityException("Not found product with id"));

            if(product.getStatus() != EntityStatusEnum.ProductStatusEnum.ACTIVE.ordinal())
                throw new NoSuchEntityException("Not found product with id");

            switch (currentRoleEnum)
            {
                case SYSTEM_ADMIN:
                    break;
                case SUPPLIER:
                    if (!userId.equals(product.getSupplierEntity().getId()))
                        throw new UnauthorizationException("Owner of this product is not you");
                    break;
                default:
                    throw new UnauthorizationException("You can not access this rescource");
            }

            Optional<CampaignCategoryEntity> optionalCampaignCategory = campaignCategoryRepository
                    .findCampaignCategoryEntitiesByCampaignEntityIdAndCategoryEntityId(campaignId, product.getCategoryEntity().getId());
            CampaignCategoryEntity campaignCategoryEntity = optionalCampaignCategory
                    .orElseThrow(() -> new NoSuchEntityException("This campaign does not contain category of added product"));
            Optional<CampaignProductEntity> optionalCampaignProduct = campaignProductRepository
                    .findByCampaignEntityIdAndProductEntityId(campaignId, productId);

            if(optionalCampaignProduct.isPresent()) {
                CampaignProductEntity campaignProduct = optionalCampaignProduct.get();
                campaignProduct.setStatus(EntityStatusEnum.CampaignProductStatusEnum.ACTIVE.ordinal());
                CampaignProductEntity savedCampaignProduct = campaignProductRepository.save(campaignProduct);
                savedModels.add(modelMapper.map(savedCampaignProduct, CampaignProductModel.class));
            } else {
                CampaignProductEntity campaignProduct = modelMapper.map(createCampaignProductModel, CampaignProductEntity.class);
                campaignProduct.setStatus(EntityStatusEnum.CampaignProductStatusEnum.ACTIVE.ordinal());
                campaignProducts.add(campaignProduct);
            }
        }
        List<CampaignProductEntity> savedCampaignProducts = (List<CampaignProductEntity>) campaignProductRepository.saveAll(campaignProducts);
        for (CampaignProductEntity entity: savedCampaignProducts) {
            savedModels.add(modelMapper.map(entity, CampaignProductModel.class));
        }
        return savedModels;
    }

    /**
     * Delete product from campaign
     *
     * @param campaignId
     * @param productId
     * @param userId
     * @param currentRoleEnum
     * @return campaign product
     */

    public CampaignProductModel deleteProductFromCampaign(UUID campaignId, UUID productId, UserEnum.RoleEnum currentRoleEnum, UUID userId){
        //check exist campaign
        Optional<CampaignEntity> optionalCampaignEntity = campaignRepository.findById(campaignId);
        CampaignEntity campaignEntity = optionalCampaignEntity.orElseThrow(() -> new NoSuchEntityException("Not found campaign with id"));

        //check exist product
        Optional<ProductEntity> optionalProductEntity = productRepository.findById(productId);
        ProductEntity productEntity = optionalProductEntity.orElseThrow(() -> new NoSuchEntityException("Not found information of product"));

        //check exist product in campaign
        Optional<CampaignProductEntity> optionalCampaignProductEntity = campaignProductRepository.findByCampaignEntityIdAndProductEntityId(campaignId, productId);
        CampaignProductEntity campaignProductEntity = optionalCampaignProductEntity.orElseThrow(() -> new NoSuchEntityException("Not found product in this campaign"));

        if(campaignEntity.getStatus() != EntityStatusEnum.CampaignStatusEnum.PRODUCT_PREPARING.ordinal()){
            throw new NoSuchEntityException("Just remove product from campaign when status of campaign is product preparing");
        }

        switch (currentRoleEnum) {
            case SYSTEM_ADMIN:
                break;
            case SUPPLIER:
                if (!userId.equals(productEntity.getSupplierEntity().getId())) {
                    throw new NoSuchEntityException("You can not access this resource");
                }
                break;
            default:
                throw new NoSuchEntityException("You can not access this resource");
        }

        //Save to DB
        campaignProductEntity.setStatus(EntityStatusEnum.CampaignProductStatusEnum.DELETED.ordinal());
        CampaignProductEntity deleteCampaignProduct = campaignProductRepository.save(campaignProductEntity);

        //Return campaign product model
        CampaignProductModel campaignProductModel = modelMapper.map(deleteCampaignProduct, CampaignProductModel.class);
        return campaignProductModel;
    }

    /**
     * Delete products from campaign
     *
     * @param campaignId
     * @param productsIds
     * @return campaign product
     */
    public List<CampaignProductModel> deleteProductsFromCampaign(UUID campaignId, List<UUID> productsIds, UserEnum.RoleEnum currentRoleEnum, UUID userId){
        List<CampaignProductModel> campaignProductModels = new ArrayList<>();
        for (UUID productId: productsIds) {
            CampaignProductModel campaignProductModel = deleteProductFromCampaign(campaignId,productId,currentRoleEnum,userId);
            campaignProductModels.add(campaignProductModel);
        }
        return campaignProductModels;
    }

    /**
     * Find product of campaign
     *
     * @param campaignId
     * @param productId
     * @param currentRoleEnum
     * @param userId
     * @return campaign category
     */
    public CampaignProductResponseModel findProductByIdOfCampaign(UUID campaignId, UUID productId, UserEnum.RoleEnum currentRoleEnum, UUID userId){
        double totalPartnerRate = 0;
        double affNetRate = 0;
        switch (currentRoleEnum) {
            case SYSTEM_ADMIN:
                break;
            case PARTNER:
                Optional<PartnerEntity> optionalPartnerEntity = partnerRepository.findById(userId);
                PartnerEntity partnerEntity = optionalPartnerEntity.orElseThrow(() -> new NoSuchEntityException("you can not access this resource"));

                Optional<PartnerTypeEntity> optionalPartnerTypeEntity = partnerTypeRepository.findById(partnerEntity.getTypeId());
                PartnerTypeEntity partnerTypeEntity = optionalPartnerTypeEntity.orElseThrow(() -> new NoSuchEntityException("Not found type of partner"));
                totalPartnerRate = partnerTypeEntity.getCommissionRate() / 100;
                break;
            case AFFILIATOR:
                Optional<AffiliatorEntity> optionalAffiliatorEntity = affiliatorRepository.findById(userId);
                AffiliatorEntity affiliatorEntity = optionalAffiliatorEntity.orElseThrow(() -> new NoSuchEntityException("you can not access this resource"));

                Optional<PartnerEntity> optionalPartnerOfAffEntity = partnerRepository.findById(affiliatorEntity.getPartnerEntity().getId());
                PartnerEntity partnerOfAffEntity = optionalPartnerOfAffEntity.orElseThrow(() -> new NoSuchEntityException("you can not access this resource"));

                Optional<PartnerTypeEntity> optionalPartnerTypeOfPartnerEntity = partnerTypeRepository.findById(partnerOfAffEntity.getTypeId());
                PartnerTypeEntity partnerTypeOfPartnerEntity = optionalPartnerTypeOfPartnerEntity.orElseThrow(() -> new NoSuchEntityException("Not found type of partner"));

                Optional<AffiliatorTypeEntity> optionalAffTypeOfAffEntity = affiliatorTypeRepository.findById(affiliatorEntity.getAffiliatorTypeEntity().getId());
                AffiliatorTypeEntity affTypeOfAffEntity = optionalAffTypeOfAffEntity.orElseThrow(() -> new NoSuchEntityException("Not found type of affiliator"));

                totalPartnerRate = partnerTypeOfPartnerEntity.getCommissionRate() / 100;
                affNetRate = affTypeOfAffEntity.getCommissionRate() / 100;
                break;
            default:
                throw new NoSuchEntityException("You can not access this resource");
        }

        CommissionRateModel commissionRateModel = new CommissionRateModel();
        commissionRateModel.setNetAffRate(affNetRate);
        commissionRateModel.setTotalPartnerRate(totalPartnerRate);

        Optional<ProductEntity> optionalProductEntity = productRepository.findById(productId);
        ProductEntity productEntity = optionalProductEntity.orElseThrow(() -> new NoSuchEntityException("Not found product"));

        return buildCampaignProductResponseModel(campaignId, productEntity,commissionRateModel);

    }

    /**
     * Build CampaignProductResponseDTO
     *
     * @param campaignId
     * @param productEntity
     * @param commissionRateModel
     * @return CampaignProductResponseModel
     */
    public CampaignProductResponseModel buildCampaignProductResponseModel(UUID campaignId, ProductEntity productEntity, CommissionRateModel commissionRateModel){
        if(productEntity == null){
            throw new NoSuchEntityException("Not found product");
        }

        double totalCommissionRate = 0;
        double totalPartnerRate = commissionRateModel.getTotalPartnerRate();
        double affNetRate = commissionRateModel.getNetAffRate();

        //check exist product in campaign
        Optional<CampaignProductEntity> optionalCampaignProductEntity = campaignProductRepository.findByCampaignEntityIdAndProductEntityId(campaignId, productEntity.getId());
        CampaignProductEntity existCampaignProduct = optionalCampaignProductEntity.orElseThrow(() -> new NoSuchEntityException("Not found campaign or product in this campaign"));

        Optional<CampaignCategoryEntity> optionalCampaignCategoryEntity = campaignCategoryRepository.findCampaignCategoryEntitiesByCampaignEntityIdAndCategoryEntityId(campaignId, productEntity.getCategoryEntity().getId());
        CampaignCategoryEntity campaignCategoryEntity = optionalCampaignCategoryEntity.get();
        if(campaignCategoryEntity != null){
            totalCommissionRate = campaignCategoryEntity.getCommissionRate() / 100;
        }

        CampaignProductResponseModel campaignProductResponseModel = new CampaignProductResponseModel();
        campaignProductResponseModel.setProductModel(modelMapper.map(productEntity, ProductModel.class));
        campaignProductResponseModel.setId(existCampaignProduct.getId());
        campaignProductResponseModel.setStatus(existCampaignProduct.getStatus());
        campaignProductResponseModel.setCampaignId(campaignId);

        if (totalPartnerRate > 0 && totalCommissionRate > 0 && affNetRate > 0)
            campaignProductResponseModel.setNetAffCommission(productEntity.getDefaultPrice() * totalCommissionRate * totalPartnerRate * affNetRate);
        if (totalPartnerRate > 0 && totalCommissionRate > 0)
            campaignProductResponseModel.setTotalPartnerCommission(productEntity.getDefaultPrice() * totalCommissionRate * totalPartnerRate);
        if (totalCommissionRate > 0)
            campaignProductResponseModel.setTotalCommission(productEntity.getDefaultPrice() * totalCommissionRate);

        return campaignProductResponseModel;
    }

}
