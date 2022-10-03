package com.tamp_backend.service;

import com.tamp_backend.constant.EntityStatusEnum;
import com.tamp_backend.constant.UserEnum;
import com.tamp_backend.customexception.NoSuchEntityException;
import com.tamp_backend.customexception.RangeTimeException;
import com.tamp_backend.customexception.UnauthorizationException;
import com.tamp_backend.entity.CampaignCategoryEntity;
import com.tamp_backend.entity.CampaignEntity;
import com.tamp_backend.entity.CampaignProductEntity;
import com.tamp_backend.entity.ProductEntity;
import com.tamp_backend.model.campaignproduct.CampaignProductModel;
import com.tamp_backend.model.campaignproduct.CreateCampaignProductModel;
import com.tamp_backend.repository.CampaignCategoryRepository;
import com.tamp_backend.repository.CampaignProductRepository;
import com.tamp_backend.repository.CampaignRepository;
import com.tamp_backend.repository.ProductRepository;
import org.modelmapper.ModelMapper;
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
    private ModelMapper modelMapper;

    public CampaignProductService(CampaignProductRepository campaignProductRepository,
                                  CampaignRepository campaignRepository,
                                  ProductRepository productRepository,
                                  CampaignCategoryRepository campaignCategoryRepository,
                                  ModelMapper modelMapper) {
        this.campaignProductRepository = campaignProductRepository;
        this.campaignRepository = campaignRepository;
        this.productRepository = productRepository;
        this.campaignCategoryRepository = campaignCategoryRepository;
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
}
