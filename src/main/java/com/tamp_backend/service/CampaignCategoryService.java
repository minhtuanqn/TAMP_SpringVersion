package com.tamp_backend.service;

import com.tamp_backend.constant.EntityStatusEnum;
import com.tamp_backend.customexception.NoSuchEntityException;
import com.tamp_backend.entity.CampaignCategoryEntity;
import com.tamp_backend.entity.CampaignEntity;
import com.tamp_backend.entity.CategoryEntity;
import com.tamp_backend.model.campaign.CampaignModel;
import com.tamp_backend.model.campaigncategory.CampaignCategoryModel;
import com.tamp_backend.model.campaigncategory.CampaignCategoryResponseModel;
import com.tamp_backend.model.campaigncategory.CreateCampaignCategoryModel;
import com.tamp_backend.model.campaigncategory.UpdateCampaignCategoryModel;
import com.tamp_backend.model.category.CategoryModel;
import com.tamp_backend.repository.CampaignCategoryRepository;
import com.tamp_backend.repository.CampaignRepository;
import com.tamp_backend.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CampaignCategoryService {
    private CampaignRepository campaignRepository;
    private CategoryRepository categoryRepository;
    private CampaignCategoryRepository campaignCategoryRepository;

    private ModelMapper modelMapper;

    public CampaignCategoryService(CampaignRepository campaignRepository, CategoryRepository categoryRepository, CampaignCategoryRepository campaignCategoryRepository, ModelMapper modelMapper) {
        this.campaignRepository = campaignRepository;
        this.categoryRepository = categoryRepository;
        this.campaignCategoryRepository = campaignCategoryRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Add category to campaign
     *
     * @param createCampaignCategoryModel,
     * @return campaign category
     */
    public List<CampaignCategoryModel> addCategoryToCampaign(UUID campaignId, CreateCampaignCategoryModel createCampaignCategoryModel) {

        //check exist campaign
        Optional<CampaignEntity> optionalCampaignEntity = campaignRepository.findById(campaignId);
        CampaignEntity campaignEntity = optionalCampaignEntity.orElseThrow(() -> new NoSuchEntityException("Not found campaign with id"));

        List<UUID> categoryIds = new ArrayList<>(createCampaignCategoryModel.getCategories().keySet());
        List<CampaignCategoryEntity> campaignCategories = new ArrayList<>();
        List<CampaignCategoryModel> savedDTO = new ArrayList<>();

        for (UUID categoryId : categoryIds) {
            // check exist category
            Optional<CategoryEntity> optionalCategoryEntity = categoryRepository.findById(categoryId);
            CategoryEntity existCategory = optionalCategoryEntity.orElseThrow(() -> new NoSuchEntityException("Not found category with id"));
            Optional<CampaignCategoryEntity>  optionalCampaignCategoryEntity = campaignCategoryRepository.findCampaignCategoryEntitiesByCampaignEntityIdAndCategoryEntityId(campaignId, categoryId);
            if (!optionalCampaignCategoryEntity.isEmpty()) {
                CampaignCategoryEntity existCampaignCategory = optionalCampaignCategoryEntity.get();
                existCampaignCategory.setStatus(EntityStatusEnum.CampaignCategoryStatusEnum.ACTIVE.ordinal());
                if (createCampaignCategoryModel.getCategories().get(categoryId) > 0) {
                    existCampaignCategory.setCommissionRate(createCampaignCategoryModel.getCategories().get(categoryId));
                } else {
                    existCampaignCategory.setCommissionRate(existCategory.getCommissionRate());
                }
                CampaignCategoryEntity savedCampaignCategory = campaignCategoryRepository.save(existCampaignCategory);
                savedDTO.add(modelMapper.map(savedCampaignCategory, CampaignCategoryModel.class));
            } else {
                CampaignCategoryEntity campaignCategoryEntity = new CampaignCategoryEntity();
                campaignCategoryEntity.setCampaignEntity(campaignEntity);
                campaignCategoryEntity.setCategoryEntity(existCategory);
                campaignCategoryEntity.setStatus(EntityStatusEnum.CampaignCategoryStatusEnum.ACTIVE.ordinal());
                if (createCampaignCategoryModel.getCategories().get(categoryId) > 0) {
                    campaignCategoryEntity.setCommissionRate(createCampaignCategoryModel.getCategories().get(categoryId));
                } else {
                    campaignCategoryEntity.setCommissionRate(existCategory.getCommissionRate());
                }
                campaignCategories.add(campaignCategoryEntity);
            }
        }

        List<CampaignCategoryEntity> insertedCampaignCategories = (List<CampaignCategoryEntity>) campaignCategoryRepository.saveAll(campaignCategories);
        List<CampaignCategoryModel> insertDTOs = new ArrayList<>();
        for (CampaignCategoryEntity campaignCategory : insertedCampaignCategories) {
            insertDTOs.add(modelMapper.map(campaignCategory, CampaignCategoryModel.class));
        }
        for (CampaignCategoryModel campaignCategoryModel : savedDTO) {
            insertDTOs.add(campaignCategoryModel);
        }
        return insertDTOs;
    }

    /**
     * Delete category from campaign
     *
     * @param campaignId
     * @param categoryId
     * @return campaign category
     */

    public CampaignCategoryModel deleteCategoryFromCampaign(UUID campaignId, UUID categoryId) {
        //check exist campaign
        Optional<CampaignEntity> optionalCampaignEntity = campaignRepository.findById(campaignId);
        CampaignEntity campaignEntity = optionalCampaignEntity.orElseThrow(() -> new NoSuchEntityException("Not found campaign with id"));

        if (campaignEntity.getStatus() != EntityStatusEnum.CampaignStatusEnum.ACTIVE.ordinal()) {
            throw new NoSuchEntityException("Just remove category from campaign when status of campaign is active");
        }

        //check exist category in campaign
        Optional<CampaignCategoryEntity> optionalCampaignCategoryEntity = campaignCategoryRepository.findCampaignCategoryEntitiesByCampaignEntityIdAndCategoryEntityId(campaignId, categoryId);
        CampaignCategoryEntity campaignCategoryEntity = optionalCampaignCategoryEntity.orElseThrow(() -> new NoSuchEntityException("Not found category in this campaign"));

        // Save to DB
        campaignCategoryEntity.setStatus(EntityStatusEnum.CampaignCategoryStatusEnum.DELETED.ordinal());
        CampaignCategoryEntity deleteCampaignCategory = campaignCategoryRepository.save(campaignCategoryEntity);

        // Return campaign category model
        CampaignCategoryModel campaignCategoryModel = modelMapper.map(deleteCampaignCategory, CampaignCategoryModel.class);
        return campaignCategoryModel;
    }

    /**
     * Update category to campaign
     *
     * @param campaignId
     * @param updateCampaignCategoryModel
     * @return campaign category
     */

    public CampaignCategoryModel updateCategoryToCampaign(UUID campaignId, UpdateCampaignCategoryModel updateCampaignCategoryModel) {
        //check exist campaign
        Optional<CampaignEntity> optionalCampaignEntity = campaignRepository.findById(campaignId);
        CampaignEntity campaignEntity = optionalCampaignEntity.orElseThrow(() -> new NoSuchEntityException("Not found campaign with id"));

        if (campaignEntity.getStatus() != EntityStatusEnum.CampaignStatusEnum.ACTIVE.ordinal()) {
            throw new NoSuchEntityException("Just update category from campaign when status of campaign is active");
        }

        //check exist category in campaign
        Optional<CampaignCategoryEntity> optionalCampaignCategoryEntity = campaignCategoryRepository.findCampaignCategoryEntitiesByCampaignEntityIdAndCategoryEntityId(campaignId, updateCampaignCategoryModel.getCategoryId());
        CampaignCategoryEntity campaignCategoryEntity = optionalCampaignCategoryEntity.orElseThrow(() -> new NoSuchEntityException("Not found category in this campaign"));


        //Update campaign category info
        campaignCategoryEntity.setCommissionRate(updateCampaignCategoryModel.getCommissionRate());
        campaignCategoryEntity.setStatus(updateCampaignCategoryModel.getStatus());

        //Save to DB
        CampaignCategoryEntity updatedCampaignCategory = campaignCategoryRepository.save(campaignCategoryEntity);

        // Return campaign category model
        CampaignCategoryModel campaignCategoryModel = modelMapper.map(updatedCampaignCategory, CampaignCategoryModel.class);
        return campaignCategoryModel;
    }

    /**
     * Update category to campaign
     *
     * @param campaignId
     * @param categoryId
     * @return campaign category
     */

    public CampaignCategoryResponseModel findCategoryByIdOfCampaign(UUID campaignId, UUID categoryId) {
        //Check exist category
        Optional<CategoryEntity> optionalCategoryEntity = categoryRepository.findById(categoryId);
        CategoryEntity categoryEntity = optionalCategoryEntity.orElseThrow(() -> new NoSuchEntityException("Not found information of this category"));

        //Check exist campaign
        Optional<CampaignEntity> optionalCampaignEntity = campaignRepository.findById(campaignId);
        CampaignEntity campaignEntity = optionalCampaignEntity.orElseThrow(() -> new NoSuchEntityException("Not found information of this campaign"));
        //check exist category in campaign
        Optional<CampaignCategoryEntity> optionalCampaignCategoryEntity = campaignCategoryRepository.findCampaignCategoryEntitiesByCampaignEntityIdAndCategoryEntityId(campaignId, categoryId);
        CampaignCategoryEntity campaignCategoryEntity = optionalCampaignCategoryEntity.orElseThrow(() -> new NoSuchEntityException("Not found category in this campaign"));

        //Return campaign category model
        CampaignCategoryResponseModel campaignCategoryResponseModel = modelMapper.map(campaignCategoryEntity, CampaignCategoryResponseModel.class);
        campaignCategoryResponseModel.setCampaignModel(modelMapper.map(campaignEntity, CampaignModel.class));
        campaignCategoryResponseModel.setCategoryModel(modelMapper.map(categoryEntity, CategoryModel.class));
        campaignCategoryResponseModel.setCommissionRate(campaignCategoryEntity.getCommissionRate());
        campaignCategoryResponseModel.setStatus(campaignCategoryEntity.getStatus());

        return campaignCategoryResponseModel;

    }

    /**
     * Delete categories from campaign
     *
     * @param campaignId
     * @param categoryIds
     * @return campaign category
     */
    public List<CampaignCategoryModel> deleteCategoriesFromCampaign(UUID campaignId, List<UUID> categoryIds) {
        List<CampaignCategoryModel> campaignCategoryModels = new ArrayList<>();
        for (UUID categoryId : categoryIds) {
            CampaignCategoryModel campaignCategoryModel = deleteCategoryFromCampaign(campaignId, categoryId);
            campaignCategoryModels.add(campaignCategoryModel);
        }
        return campaignCategoryModels;
    }
}
