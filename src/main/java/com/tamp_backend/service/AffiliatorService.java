package com.tamp_backend.service;

import com.tamp_backend.constant.EntityStatusEnum;
import com.tamp_backend.constant.StatusSearchEnum;
import com.tamp_backend.constant.UserEnum;
import com.tamp_backend.convertor.PaginationConvertor;
import com.tamp_backend.customexception.DuplicatedEntityException;
import com.tamp_backend.customexception.NoSuchEntityException;
import com.tamp_backend.entity.*;
import com.tamp_backend.metamodel.AffiliatorEntity_;
import com.tamp_backend.model.PaginationRequestModel;
import com.tamp_backend.model.ResourceModel;
import com.tamp_backend.model.affiliator.AffiliatorFilterModel;
import com.tamp_backend.model.affiliator.AffiliatorModel;
import com.tamp_backend.model.affiliator.CreateAffiliatorModel;
import com.tamp_backend.model.affiliator.UpdateAffiliatorModel;
import com.tamp_backend.model.systemaccount.AccountModel;
import com.tamp_backend.repository.*;
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
public class AffiliatorService {
    private AffiliatorRepository affiliatorRepository;
    private AffiliatorTypeRepository affiliatorTypeRepository;
    private PartnerRepository partnerRepository;
    private AccountRepository accountRepository;
    private WalletRepository walletRepository;
    private ModelMapper modelMapper;
    private PasswordEncoder passwordEncoder;

    public AffiliatorService(AffiliatorRepository affiliatorRepository, AffiliatorTypeRepository affiliatorTypeRepository, PartnerRepository partnerRepository, AccountRepository accountRepository, WalletRepository walletRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.affiliatorRepository = affiliatorRepository;
        this.affiliatorTypeRepository = affiliatorTypeRepository;
        this.partnerRepository = partnerRepository;
        this.accountRepository = accountRepository;
        this.walletRepository = walletRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Create new affiliator
     *
     * @param createAffiliatorModel
     * @return affiliator
     */

    public AffiliatorModel createAffiliator(CreateAffiliatorModel createAffiliatorModel, String avatar,UserEnum.RoleEnum currentRoleEnum, UUID userId) {
        //Check existed username or email in DB
        if (accountRepository.existsByUsername(createAffiliatorModel.getUsername()))
            throw new DuplicatedEntityException("This username existed");

        if (accountRepository.existsByEmail(createAffiliatorModel.getEmail()))
            throw new DuplicatedEntityException("This email has been already for another account");

        switch (currentRoleEnum) {
            case SYSTEM_ADMIN:
                break;
            case PARTNER:
                if (!userId.equals(createAffiliatorModel.getPartnerId())) {
                    throw new NoSuchEntityException("You can not access this resource");
                }
                break;
            default:
                throw new NoSuchEntityException("You can not access this resource");
        }

        //check exist partner
        Optional<PartnerEntity> optionalPartnerEntity = partnerRepository.findById(createAffiliatorModel.getPartnerId());
        PartnerEntity partnerEntity = optionalPartnerEntity.orElseThrow(() -> new NoSuchEntityException("Not found partner with id"));


        //check exist affiliator type
        Optional<AffiliatorTypeEntity> optionalAffiliatorTypeEntity = affiliatorTypeRepository.findById(createAffiliatorModel.getTypeId());
        AffiliatorTypeEntity affiliatorTypeEntity = optionalAffiliatorTypeEntity.orElseThrow(() -> new NoSuchEntityException("Not found affiliator type with id"));

        //Save new account
        AccountEntity createdAccount = modelMapper.map(createAffiliatorModel, AccountEntity.class);
        createdAccount.setStatus(EntityStatusEnum.AccountStatusEnum.ACTIVE.ordinal());
        createdAccount.setRole(UserEnum.RoleEnum.AFFILIATOR.toString());
        createdAccount.setPassword(passwordEncoder.encode(createAffiliatorModel.getPassword()));
        AccountEntity insertedAccount = accountRepository.save(createdAccount);

        //Create new wallet for affiliator
        WalletEntity createdWallet = new WalletEntity();
        createdWallet.setAccountId(insertedAccount.getId());
        createdWallet.setOwnerType(UserEnum.WalletTypeEnum.AFFILIATOR.toString());
        createdWallet.setStatus(EntityStatusEnum.AccountStatusEnum.ACTIVE.ordinal());
        createdWallet.setAmount(0);
        createdWallet.setCreateAt(LocalDateTime.now());
        WalletEntity insertedWallet = walletRepository.save(createdWallet);

        //Save new information of affiliator
        AffiliatorEntity affiliatorEntity = new AffiliatorEntity();
        affiliatorEntity.setDob(createAffiliatorModel.getDob());
        affiliatorEntity.setPhone(createAffiliatorModel.getPhone());
        affiliatorEntity.setGender(createAffiliatorModel.getGender());
        affiliatorEntity.setFullName(createAffiliatorModel.getFullName());
        affiliatorEntity.setAddress(createAffiliatorModel.getAddress());
        affiliatorEntity.setAvatar(avatar);
        affiliatorEntity.setPartnerEntity(partnerEntity);
        affiliatorEntity.setCode(createAffiliatorModel.getCode());
        affiliatorEntity.setCreateAt(LocalDateTime.now());
        affiliatorEntity.setUpdateAt(LocalDateTime.now());
        affiliatorEntity.setAffiliatorTypeEntity(affiliatorTypeEntity);
        affiliatorEntity.setAccountId(insertedAccount.getId());
        affiliatorEntity.setStatus(EntityStatusEnum.AccountStatusEnum.ACTIVE.ordinal());

        //Return saved affiliator model
        AffiliatorEntity savedAffiliatorEntity = affiliatorRepository.save(affiliatorEntity);
        AffiliatorModel responseAffiliatorModel = modelMapper.map(savedAffiliatorEntity, AffiliatorModel.class);
        responseAffiliatorModel.setAccountModel(modelMapper.map(insertedAccount, AccountModel.class));
        responseAffiliatorModel.setPartnerId(createAffiliatorModel.getPartnerId());
        responseAffiliatorModel.setTypeId(createAffiliatorModel.getTypeId());
        return responseAffiliatorModel;
    }

    /**
     * Delete an affiliator by id
     *
     * @param affiliatorId
     * @return deleted affiliator model
     */
    public AffiliatorModel deleteAffiliator(UUID affiliatorId, UserEnum.RoleEnum currentRoleEnum, UUID userId) {
        Optional<AffiliatorEntity> optionalAffiliatorEntity = affiliatorRepository.findById(affiliatorId);
        AffiliatorEntity affiliatorEntity = optionalAffiliatorEntity.orElseThrow(() -> new NoSuchEntityException("Not found affiliator with id"));

        switch (currentRoleEnum) {
            case SYSTEM_ADMIN:
                break;
            case PARTNER:
                System.out.println(affiliatorEntity.getPartnerEntity().getId());
                System.out.println(userId);
                if (!userId.equals(affiliatorEntity.getPartnerEntity().getId())) {
                    throw new NoSuchEntityException("This affiliator does not belong to you");
                }
                break;
            default:
                throw new NoSuchEntityException("You can not access this resource");
        }
        Optional<AccountEntity> optionalAccountEntity = accountRepository.findById(affiliatorEntity.getAccountId());
        AccountEntity accountEntity = optionalAccountEntity.orElseThrow(() -> new NoSuchEntityException("Not found affiliator with id"));


        //Update affiliator information
        affiliatorEntity.setStatus(EntityStatusEnum.AccountStatusEnum.DISABLE.ordinal());
        affiliatorEntity.setUpdateAt(LocalDateTime.now());
        AffiliatorEntity deletedAffiliatorEntity = affiliatorRepository.save(affiliatorEntity);

        //Update affiliator account
        accountEntity.setStatus(EntityStatusEnum.AccountStatusEnum.DISABLE.ordinal());
        AccountEntity deletedAccountEntity = accountRepository.save(accountEntity);

        //Return deleted affiliator model
        AffiliatorModel affiliatorModel = modelMapper.map(deletedAffiliatorEntity, AffiliatorModel.class);
        affiliatorModel.setAccountModel(modelMapper.map(deletedAccountEntity, AccountModel.class));
        return affiliatorModel;
    }

    /**
     * Find affiliator model by id
     *
     * @param id
     * @return affiliator model
     */
    public AffiliatorModel findAffiliatorById(UUID id, UserEnum.RoleEnum currentRoleEnum, UUID userId) {
        //Find affiliator information by id
        Optional<AffiliatorEntity> optionalAffiliatorEntity = affiliatorRepository.findById(id);
        AffiliatorEntity affiliatorEntity = optionalAffiliatorEntity.orElseThrow(() -> new NoSuchEntityException("Not found affiliator with id"));

        switch (currentRoleEnum) {
            case SYSTEM_ADMIN:
                break;
            case AFFILIATOR:
                if (!userId.equals(id))
                    throw new NoSuchEntityException("You can not access this resource");
                break;
            case PARTNER:
                if (!userId.equals(affiliatorEntity.getPartnerEntity().getId())) {
                    throw new NoSuchEntityException("You can not access this resource");
                }
                break;
            default:
                throw new NoSuchEntityException("You can not access this resource");
        }

        //Find account information of affiliator
        Optional<AccountEntity> optionalAccountEntity = accountRepository.findById(affiliatorEntity.getAccountId());
        AccountEntity accountEntity = optionalAccountEntity.orElseThrow(() -> new NoSuchEntityException("Not found account of affiliator with id"));

        //Return affiliator model
        AffiliatorModel affiliatorModel = modelMapper.map(affiliatorEntity, AffiliatorModel.class);
        affiliatorModel.setAccountModel(modelMapper.map(accountEntity, AccountModel.class));
        return affiliatorModel;
    }

    /**
     * Delete list of affiliator
     *
     * @param ids
     * @return list of deleted affiliator models
     */
    public List<AffiliatorModel> deleteAffiliatorsByIds(List<UUID> ids, UserEnum.RoleEnum currentRoleEnum, UUID userId) {
        if (ids == null) throw new IllegalArgumentException("Not found any affiliator");
        List<AffiliatorModel> affiliatorModels = new ArrayList<>();

        for (UUID id : ids) {
            AffiliatorModel affiliatorModel = deleteAffiliator(id, currentRoleEnum, userId);
            affiliatorModels.add(affiliatorModel);
        }
        return affiliatorModels;
    }

    /**
     * Specification for search name
     *
     * @param searchedValue
     * @return contains name specification
     */
    private Specification<AffiliatorEntity> containsName(String searchedValue) {
        return ((root, query, criteriaBuilder) -> {
            String pattern = "%" + searchedValue + "%";
            return criteriaBuilder.like(root.get(AffiliatorEntity_.FULL_NAME), pattern);
        });
    }

    /**
     * Specification for search address
     *
     * @param searchedValue
     * @return contains address specification
     */
    private Specification<AffiliatorEntity> containsAddress(String searchedValue) {
        return ((root, query, criteriaBuilder) -> {
            String pattern = "%" + searchedValue + "%";
            return criteriaBuilder.like(root.get(AffiliatorEntity_.ADDRESS), pattern);
        });
    }

    /**
     * Specification for search phone
     *
     * @param searchedValue
     * @return contains phone specification
     */
    private Specification<AffiliatorEntity> containsPhone(String searchedValue) {
        return ((root, query, criteriaBuilder) -> {
            String pattern = "%" + searchedValue + "%";
            return criteriaBuilder.like(root.get(AffiliatorEntity_.PHONE), pattern);
        });
    }

    /**
     * Specification for filter status
     *
     * @param searchedEnum
     * @return status filter specification
     */
    private Specification<AffiliatorEntity> statusFilter(StatusSearchEnum.AccountStatusSearchEnum searchedEnum) {
        return ((root, query, criteriaBuilder) -> {
            int searchedStatusNum = searchedEnum != null ? searchedEnum.ordinal() : StatusSearchEnum.AccountStatusSearchEnum.ALL.ordinal();
            if (searchedStatusNum < StatusSearchEnum.AccountStatusSearchEnum.ALL.ordinal()) {
                return criteriaBuilder.equal(root.get(AffiliatorEntity_.STATUS), searchedStatusNum);
            } else {
                return criteriaBuilder.lessThan(root.get(AffiliatorEntity_.STATUS), StatusSearchEnum.AccountStatusSearchEnum.ALL.ordinal());
            }
        });
    }

    /**
     * Search and filter affiliator
     *
     * @param searchedValue
     * @param paginationRequestModel
     * @param affiliatorFilterModel
     * @return resource of data
     */
    public ResourceModel<AffiliatorModel> searchAffiliators(String searchedValue, PaginationRequestModel paginationRequestModel, AffiliatorFilterModel affiliatorFilterModel) {
        PaginationConvertor<AffiliatorModel, AffiliatorEntity> paginationConvertor = new PaginationConvertor<>();

        String defaultSortBy = AffiliatorEntity_.FULL_NAME;
        Pageable pageable = paginationConvertor.convertToPageable(paginationRequestModel, defaultSortBy, AffiliatorEntity.class);

        //Find all affiliators
        String searchedName = affiliatorFilterModel.getFullName() != null ? affiliatorFilterModel.getFullName() : "";
        String searchedAddress = affiliatorFilterModel.getAddress() != null ? affiliatorFilterModel.getAddress() : "";
        String searchedPhone = affiliatorFilterModel.getPhone() != null ? affiliatorFilterModel.getPhone() : "";

        Page<AffiliatorEntity> affiliatorEntityPage = affiliatorRepository.findAll(containsName(searchedValue).and(statusFilter(affiliatorFilterModel.getStatusType())).and(containsAddress(searchedAddress)).and(containsPhone(searchedPhone)).and(containsName(searchedName)), pageable);

        //Convert list of affiliators entity to list of affiliators model
        List<AffiliatorModel> affiliatorModels = new ArrayList<>();
        for (AffiliatorEntity entity : affiliatorEntityPage) {
            AffiliatorModel affiliatorModel = modelMapper.map(entity, AffiliatorModel.class);
            Optional<AccountEntity> optionalAccountEntity = accountRepository.findById(entity.getAccountId());
            AccountEntity accountEntity = optionalAccountEntity.orElseThrow(() -> new NoSuchEntityException("Not found account of affiliator with id"));
            affiliatorModel.setAccountModel(modelMapper.map(accountEntity, AccountModel.class));
            affiliatorModels.add(affiliatorModel);
        }

        //Prepare resource for return
        ResourceModel<AffiliatorModel> resource = new ResourceModel<>();
        resource.setData(affiliatorModels);
        paginationConvertor.buildPagination(paginationRequestModel, affiliatorEntityPage, resource);
        return resource;
    }


    /**
     * Update affiliator
     *
     * @param updateAffiliatorModel
     * @param avatar
     * @return update affiliator model
     */


    public AffiliatorModel updateAffiliator(UpdateAffiliatorModel updateAffiliatorModel, String avatar, UserEnum.RoleEnum currentRoleEnum, UUID userId) {
        //Check existed affiliator
        Optional<AffiliatorEntity> optionalAffiliatorEntity = affiliatorRepository.findById(updateAffiliatorModel.getId());
        AffiliatorEntity affiliatorEntity = optionalAffiliatorEntity.orElseThrow(() -> new NoSuchEntityException("Not found affiliator with id"));

        switch (currentRoleEnum) {
            case SYSTEM_ADMIN:
                break;
            case AFFILIATOR:
                if (!userId.equals(affiliatorEntity.getId()))
                    throw new NoSuchEntityException("This affiliator is not you");
                break;
            case PARTNER:
                if (!userId.equals(affiliatorEntity.getPartnerEntity().getId())) {
                    throw new NoSuchEntityException("This affiliator does not belong to you");
                }
                break;
            default:
                throw new NoSuchEntityException("You can not access this resource");
        }

        //Check existed account of affiliator
        Optional<AccountEntity> optionalAccountEntity = accountRepository.findById(affiliatorEntity.getAccountId());
        AccountEntity accountEntity = optionalAccountEntity.orElseThrow(() -> new NoSuchEntityException("Not found account of affiliator with id"));

        //check exist affiliator type
        Optional<AffiliatorTypeEntity> optionalAffiliatorTypeEntity = affiliatorTypeRepository.findById(updateAffiliatorModel.getTypeId());
        AffiliatorTypeEntity affiliatorTypeEntity = optionalAffiliatorTypeEntity.orElseThrow(() -> new NoSuchEntityException("Not found affiliator type with id"));

        //Save affiliator information

        if (avatar != null) affiliatorEntity.setAvatar(avatar);
        affiliatorEntity.setAddress(updateAffiliatorModel.getAddress());
        affiliatorEntity.setDob(updateAffiliatorModel.getDob());
        affiliatorEntity.setFullName(updateAffiliatorModel.getFullName());
        affiliatorEntity.setGender(updateAffiliatorModel.getGender());
        affiliatorEntity.setPhone(updateAffiliatorModel.getPhone());
        affiliatorEntity.setUpdateAt(LocalDateTime.now());
        affiliatorEntity.setAffiliatorTypeEntity(affiliatorTypeEntity);
        if (!currentRoleEnum.equals(UserEnum.RoleEnum.AFFILIATOR)) {
            affiliatorEntity.setStatus(updateAffiliatorModel.getStatus().ordinal());
            affiliatorEntity.setCode(updateAffiliatorModel.getCode());
        }


        AffiliatorEntity savedAffiliator = affiliatorRepository.save(affiliatorEntity);

        //Save account information of affiliator
        accountEntity.setStatus(updateAffiliatorModel.getStatus().ordinal());
        accountEntity.setEmail(updateAffiliatorModel.getEmail());
        AccountEntity savedAccount = accountRepository.save(accountEntity);

        //Prepare model for response
        AffiliatorModel updatedAffiliatorModel = modelMapper.map(savedAffiliator, AffiliatorModel.class);
        updatedAffiliatorModel.setAccountModel(modelMapper.map(savedAccount, AccountModel.class));
        return updatedAffiliatorModel;
    }

    /**
     * Find affiliator by username
     * @param username
     * @return affiliator model
     */
    public AffiliatorModel findAffiliatorByUsername(String username) {
        Optional<AccountEntity> optionalAccountEntity = accountRepository.findByUsername(username);
        AccountEntity accountEntity = optionalAccountEntity.orElseThrow(() -> new NoSuchEntityException("Not found account with username"));

        Optional<AffiliatorEntity> optionalAffiliatorEntity = affiliatorRepository.findByAccountId(accountEntity.getId());
        AffiliatorEntity affiliatorEntity = optionalAffiliatorEntity.orElseThrow(() -> new NoSuchEntityException("Not found information of affiliator"));
        return  modelMapper.map(affiliatorEntity, AffiliatorModel.class);
    }

}
