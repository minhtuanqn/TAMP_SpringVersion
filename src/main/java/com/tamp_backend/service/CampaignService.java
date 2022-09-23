package com.tamp_backend.service;

import com.tamp_backend.constant.EntityStatusEnum;
import com.tamp_backend.customexception.DuplicatedEntityException;
import com.tamp_backend.customexception.NoSuchEntityException;
import com.tamp_backend.entity.CampaignEntity;;
import com.tamp_backend.model.campaign.CampaignModel;
import com.tamp_backend.model.campaign.CreateCampaignModel;
import com.tamp_backend.repository.CampaignRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CampaignService {
    private CampaignRepository campaignRepository;

    private ModelMapper modelMapper;

    public CampaignService(CampaignRepository campaignRepository, ModelMapper modelMapper) {
        this.campaignRepository = campaignRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Create new campaign
     *
     * @param createCampaignModel
     * @return campaign
     */

    public CampaignModel createCampaign(CreateCampaignModel createCampaignModel, String coverPhotoUrl, UUID adminId) {
        // Check exist campaign name in DB
        if (campaignRepository.existsByName(createCampaignModel.getName()))
            throw new DuplicatedEntityException("Duplicated name of campaign");

        // Compare start time and end time

        //Save new information of campaign
        CampaignEntity createdCampaign = modelMapper.map(createCampaignModel, CampaignEntity.class);
        createdCampaign.setCoverPhoto(coverPhotoUrl);
        createdCampaign.setStatus(EntityStatusEnum.CampaignStatusEnum.ACTIVE.ordinal());
        createdCampaign.setCreateBy(adminId);

        //Save entity to DB
        CampaignEntity savedEntity = campaignRepository.save(createdCampaign);
        CampaignModel responseCampaignModel = modelMapper.map(savedEntity, CampaignModel.class);
        return responseCampaignModel;
    }

    /**
     * Find a campaign by id
     * @param id
     * @return found model
     */
    public CampaignModel findCampaignById(UUID id) {
        //Find campaign with id
        Optional<CampaignEntity> searchedCampaignOptional = campaignRepository.findById(id);
        CampaignEntity campaignEntity = searchedCampaignOptional.orElseThrow(() -> new NoSuchEntityException("Not found campaign"));
        return modelMapper.map(campaignEntity, CampaignModel.class);
    }

    /**
     * delete a campaign
     * @param id
     * @return delete model
     */
    public CampaignModel deleteCampaign(UUID id) {
        //Find campaign with id
        Optional<CampaignEntity> deletedCampaignOptional = campaignRepository.findById(id);
        CampaignEntity deletedCampaignEntity = deletedCampaignOptional.orElseThrow(() -> new NoSuchEntityException("Not found campaign with id"));

        //Set status for entity
        deletedCampaignEntity.setStatus(EntityStatusEnum.CampaignStatusEnum.DELETED.ordinal());

        //Update status of campaign
        CampaignEntity responseEntity = campaignRepository.save(deletedCampaignEntity);
        return modelMapper.map(responseEntity, CampaignModel.class);
    }

}
