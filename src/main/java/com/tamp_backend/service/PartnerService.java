package com.tamp_backend.service;

import com.tamp_backend.constant.EntityStatusEnum;
import com.tamp_backend.constant.StatusSearchEnum;
import com.tamp_backend.constant.UserEnum;
import com.tamp_backend.convertor.PaginationConvertor;
import com.tamp_backend.customexception.DuplicatedEntityException;
import com.tamp_backend.customexception.NoSuchEntityException;
import com.tamp_backend.entity.AccountEntity;
import com.tamp_backend.entity.PartnerEntity;
import com.tamp_backend.entity.PartnerTypeEntity;
import com.tamp_backend.entity.WalletEntity;
import com.tamp_backend.metamodel.PartnerEntity_;
import com.tamp_backend.model.PaginationRequestModel;
import com.tamp_backend.model.ResourceModel;
import com.tamp_backend.model.partner.CreatePartnerModel;
import com.tamp_backend.model.partner.PartnerFilterModel;
import com.tamp_backend.model.partner.PartnerModel;
import com.tamp_backend.model.partner.UpdatePartnerModel;
import com.tamp_backend.model.systemaccount.AccountModel;
import com.tamp_backend.repository.AccountRepository;
import com.tamp_backend.repository.PartnerRepository;
import com.tamp_backend.repository.PartnerTypeRepository;
import com.tamp_backend.repository.WalletRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PartnerService {
    private PartnerRepository partnerRepository;

    private PartnerTypeRepository partnerTypeRepository;
    private AccountRepository accountRepository;
    private WalletRepository walletRepository;
    private ModelMapper modelMapper;
    private PasswordEncoder passwordEncoder;

    public PartnerService(PartnerRepository partnerRepository, PartnerTypeRepository partnerTypeRepository, AccountRepository accountRepository, WalletRepository walletRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.partnerRepository = partnerRepository;
        this.partnerTypeRepository = partnerTypeRepository;
        this.accountRepository = accountRepository;
        this.walletRepository = walletRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Create new partner
     *
     * @param createPartnerModel
     * @return partner
     */

    public PartnerModel createPartner(CreatePartnerModel createPartnerModel, String logoUrl) {
        //Check existed username or email in DB
        if (accountRepository.existsByUsername(createPartnerModel.getUsername()))
            throw new DuplicatedEntityException("This username existed");
        if (accountRepository.existsByEmail(createPartnerModel.getEmail()))
            throw new DuplicatedEntityException("This email has been already for another account");

        //check exist partner type
        Optional<PartnerTypeEntity> optionalPartnerTypeEntity = partnerTypeRepository.findById(createPartnerModel.getTypeId());
        PartnerTypeEntity partnerTypeEntity = optionalPartnerTypeEntity.orElseThrow(() -> new NoSuchEntityException("Not found partner type with id"));

        //Save new account
        AccountEntity createdAccount = modelMapper.map(createPartnerModel, AccountEntity.class);
        createdAccount.setStatus(EntityStatusEnum.AccountStatusEnum.ACTIVE.ordinal());
        createdAccount.setRole(UserEnum.RoleEnum.PARTNER.toString());
        createdAccount.setPassword(passwordEncoder.encode(createPartnerModel.getPassword()));
        AccountEntity insertedAccount = accountRepository.save(createdAccount);

        //Save new information of partner
        PartnerEntity createdPartner = modelMapper.map(createPartnerModel, PartnerEntity.class);
        createdPartner.setStatus(EntityStatusEnum.AccountStatusEnum.ACTIVE.ordinal());
        createdPartner.setAccountId(insertedAccount.getId());
        createdPartner.setCreateAt(LocalDateTime.now());
        createdPartner.setUpdateAt(LocalDateTime.now());
        createdPartner.setLogo(logoUrl);
        createdPartner.setCreateBy(UUID.fromString("e5facdba-b1b5-4077-8483-a9bfbb187955"));
        PartnerEntity insertedPartner = partnerRepository.save(createdPartner);

        //Create new wallet for partner
        WalletEntity createdWallet = new WalletEntity();
        createdWallet.setAccountId(insertedAccount.getId());
        createdWallet.setOwnerType(UserEnum.WalletTypeEnum.PARTNER.toString());
        createdWallet.setStatus(EntityStatusEnum.AccountStatusEnum.ACTIVE.ordinal());
        createdWallet.setAmount(0);
        createdWallet.setCreateAt(LocalDateTime.now());
        WalletEntity insertedWallet = walletRepository.save(createdWallet);

        //Return saved partner model
        PartnerModel partnerModel = modelMapper.map(insertedPartner, PartnerModel.class);
        partnerModel.setAccountModel(modelMapper.map(insertedAccount, AccountModel.class));
        return partnerModel;
    }

    /**
     * Delete a partner by id
     *
     * @param partnerId
     * @return deleted partner model
     */
    public PartnerModel deletePartner(UUID partnerId) {
        Optional<PartnerEntity> optionalPartnerEntity = partnerRepository.findById(partnerId);
        PartnerEntity partnerEntity = optionalPartnerEntity.orElseThrow(() -> new NoSuchEntityException("Not found partner with id"));

        Optional<AccountEntity> optionalAccountEntity = accountRepository.findById(partnerEntity.getAccountId());
        AccountEntity accountEntity = optionalAccountEntity.orElseThrow(() -> new NoSuchEntityException("Not found partner with id"));

        //Update partner information
        partnerEntity.setStatus(EntityStatusEnum.AccountStatusEnum.DISABLE.ordinal());
        partnerEntity.setUpdateAt(LocalDateTime.now());
        PartnerEntity deletedPartnerEntity = partnerRepository.save(partnerEntity);

        //Update partner account
        accountEntity.setStatus(EntityStatusEnum.AccountStatusEnum.DISABLE.ordinal());
        AccountEntity deletedAccountEntity = accountRepository.save(accountEntity);

        //Return deleted partner model
        PartnerModel partnerModel = modelMapper.map(deletedPartnerEntity, PartnerModel.class);
        partnerModel.setAccountModel(modelMapper.map(deletedAccountEntity, AccountModel.class));
        return partnerModel;
    }

    /**
     * Find partner model by id
     *
     * @param id
     * @return partner model
     */
    public PartnerModel findPartnerById(UUID id) {
        //Find partner information by id
        Optional<PartnerEntity> optionalPartnerEntity = partnerRepository.findById(id);
        PartnerEntity partnerEntity = optionalPartnerEntity.orElseThrow(() -> new NoSuchEntityException("Not found partner with id"));

        //Find account information of partner
        Optional<AccountEntity> optionalAccountEntity = accountRepository.findById(partnerEntity.getAccountId());
        AccountEntity accountEntity = optionalAccountEntity.orElseThrow(() -> new NoSuchEntityException("Not found account of partner with id"));

        //Return partner model
        PartnerModel partnerModel = modelMapper.map(partnerEntity, PartnerModel.class);
        partnerModel.setAccountModel(modelMapper.map(accountEntity, AccountModel.class));
        return partnerModel;
    }

    /**
     * Delete list of partner
     *
     * @param ids
     * @return list of deleted partner models
     */
    public List<PartnerModel> deletePartnersByIds(List<UUID> ids) {
        if (ids == null) throw new IllegalArgumentException("Not found any partner");
        List<PartnerModel> partnerModels = new ArrayList<>();

        for (UUID id : ids) {
            PartnerModel partnerModel = deletePartner(id);
            partnerModels.add(partnerModel);
        }
        return partnerModels;
    }

    /**
     * Specification for search name
     *
     * @param searchedValue
     * @return contains name specification
     */
    private Specification<PartnerEntity> containsName(String searchedValue) {
        return ((root, query, criteriaBuilder) -> {
            String pattern = "%" + searchedValue + "%";
            return criteriaBuilder.like(root.get(PartnerEntity_.NAME), pattern);
        });
    }

    /**
     * Specification for search address
     *
     * @param searchedValue
     * @return contains address specification
     */
    private Specification<PartnerEntity> containsAddress(String searchedValue) {
        return ((root, query, criteriaBuilder) -> {
            String pattern = "%" + searchedValue + "%";
            return criteriaBuilder.like(root.get(PartnerEntity_.ADDRESS), pattern);
        });
    }

    /**
     * Specification for search phone
     *
     * @param searchedValue
     * @return contains phone specification
     */
    private Specification<PartnerEntity> containsPhone(String searchedValue) {
        return ((root, query, criteriaBuilder) -> {
            String pattern = "%" + searchedValue + "%";
            return criteriaBuilder.like(root.get(PartnerEntity_.PHONE), pattern);
        });
    }

    /**
     * Specification for filter status
     *
     * @param searchedEnum
     * @return status filter specification
     */
    private Specification<PartnerEntity> statusFilter(StatusSearchEnum.AccountStatusSearchEnum searchedEnum) {
        return ((root, query, criteriaBuilder) -> {
            int searchedStatusNum = searchedEnum != null ? searchedEnum.ordinal() : StatusSearchEnum.AccountStatusSearchEnum.ALL.ordinal();
            if (searchedStatusNum < StatusSearchEnum.AccountStatusSearchEnum.ALL.ordinal()) {
                return criteriaBuilder.equal(root.get(PartnerEntity_.STATUS), searchedStatusNum);
            } else {
                return criteriaBuilder.lessThan(root.get(PartnerEntity_.STATUS), StatusSearchEnum.AccountStatusSearchEnum.ALL.ordinal());
            }
        });
    }

    /**
     * Search and filter partner
     *
     * @param searchedValue
     * @param paginationRequestModel
     * @param partnerFilterModel
     * @return resource of data
     */
    public ResourceModel<PartnerModel> searchPartners(String searchedValue, PaginationRequestModel paginationRequestModel, PartnerFilterModel partnerFilterModel) {
        PaginationConvertor<PartnerModel, PartnerEntity> paginationConvertor = new PaginationConvertor<>();

        String defaultSortBy = PartnerEntity_.NAME;
        Pageable pageable = paginationConvertor.convertToPageable(paginationRequestModel, defaultSortBy, PartnerEntity.class);

        //Find all partners
        String searchedName = partnerFilterModel.getName() != null ? partnerFilterModel.getName() : "";
        String searchedAddress = partnerFilterModel.getAddress() != null ? partnerFilterModel.getAddress() : "";
        String searchedPhone = partnerFilterModel.getPhone() != null ? partnerFilterModel.getPhone() : "";

        Page<PartnerEntity> partnerEntityPage = partnerRepository.findAll(containsName(searchedValue).and(statusFilter(partnerFilterModel.getStatusType())).and(containsAddress(searchedAddress)).and(containsPhone(searchedPhone)).and(containsName(searchedName)), pageable);

        //Convert list of partners entity to list of partners model
        List<PartnerModel> partnerModels = new ArrayList<>();
        for (PartnerEntity entity : partnerEntityPage) {
            PartnerModel partnerModel = modelMapper.map(entity, PartnerModel.class);
            Optional<AccountEntity> optionalAccountEntity = accountRepository.findById(entity.getAccountId());
            AccountEntity accountEntity = optionalAccountEntity.orElseThrow(() -> new NoSuchEntityException("Not found account of partner with id"));
            partnerModel.setAccountModel(modelMapper.map(accountEntity, AccountModel.class));
            partnerModels.add(partnerModel);
        }

        //Prepare resource for return
        ResourceModel<PartnerModel> resource = new ResourceModel<>();
        resource.setData(partnerModels);
        paginationConvertor.buildPagination(paginationRequestModel, partnerEntityPage, resource);
        return resource;
    }

    /**
     * Update partner
     *
     * @param updatePartnerModel
     * @param logoUrl
     * @return update partner model
     */


    public PartnerModel updatePartner(UpdatePartnerModel updatePartnerModel, String logoUrl) {
        //Check existed partner
        Optional<PartnerEntity> optionalPartnerEntity = partnerRepository.findById(updatePartnerModel.getId());
        PartnerEntity partnerEntity = optionalPartnerEntity.orElseThrow(() -> new NoSuchEntityException("Not found partner with id"));

        //Check existed account of partner
        Optional<AccountEntity> optionalAccountEntity = accountRepository.findById(partnerEntity.getAccountId());
        AccountEntity accountEntity = optionalAccountEntity.orElseThrow(() -> new NoSuchEntityException("Not found account of partner with id"));

        //check exist partner type
        Optional<PartnerTypeEntity> optionalPartnerTypeEntity = partnerTypeRepository.findById(updatePartnerModel.getTypeId());
        PartnerTypeEntity partnerTypeEntity = optionalPartnerTypeEntity.orElseThrow(() -> new NoSuchEntityException("Not found partner type with id"));

        //Save partner information
        if (logoUrl != null) partnerEntity.setLogo(logoUrl);
        partnerEntity.setAddress(updatePartnerModel.getAddress());
        partnerEntity.setName(updatePartnerModel.getName());
        partnerEntity.setStatus(updatePartnerModel.getStatus().ordinal());
        partnerEntity.setDescription(updatePartnerModel.getDescription());
        partnerEntity.setPhone(updatePartnerModel.getPhone());
        partnerEntity.setUpdateAt(LocalDateTime.now());
        partnerEntity.setTypeId(updatePartnerModel.getTypeId());
        PartnerEntity savedPartner = partnerRepository.save(partnerEntity);

        //Save account information of partner
        accountEntity.setStatus(updatePartnerModel.getStatus().ordinal());
        accountEntity.setEmail(updatePartnerModel.getEmail());
        AccountEntity savedAccount = accountRepository.save(accountEntity);

        //Prepare model for response
        PartnerModel updatedPartnerModel = modelMapper.map(savedPartner, PartnerModel.class);
        updatedPartnerModel.setAccountModel(modelMapper.map(savedAccount, AccountModel.class));
        return updatedPartnerModel;
    }

}
