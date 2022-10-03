package com.tamp_backend.service;

import com.tamp_backend.constant.EntityStatusEnum;
import com.tamp_backend.constant.StatusSearchEnum;
import com.tamp_backend.constant.UserEnum;
import com.tamp_backend.convertor.PaginationConvertor;
import com.tamp_backend.customexception.DuplicatedEntityException;
import com.tamp_backend.customexception.NoSuchEntityException;
import com.tamp_backend.entity.AccountEntity;
import com.tamp_backend.entity.SupplierEntity;
import com.tamp_backend.entity.WalletEntity;
import com.tamp_backend.metamodel.SupplierEntity_;
import com.tamp_backend.model.PaginationRequestModel;
import com.tamp_backend.model.ResourceModel;
import com.tamp_backend.model.supplier.CreateSupplierModel;
import com.tamp_backend.model.supplier.SupplierFilterModel;
import com.tamp_backend.model.supplier.SupplierModel;
import com.tamp_backend.model.supplier.UpdateSupplierModel;
import com.tamp_backend.model.systemaccount.AccountModel;
import com.tamp_backend.repository.AccountRepository;
import com.tamp_backend.repository.SupplierRepository;
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
public class SupplierService {
    private SupplierRepository supplierRepository;
    private AccountRepository accountRepository;
    private WalletRepository walletRepository;
    private ModelMapper modelMapper;
    private PasswordEncoder passwordEncoder;

    public SupplierService(SupplierRepository supplierRepository,
                           ModelMapper modelMapper,
                           PasswordEncoder passwordEncoder,
                           AccountRepository accountRepository,
                           WalletRepository walletRepository) {
        this.supplierRepository = supplierRepository;
        this.modelMapper = modelMapper;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.walletRepository = walletRepository;
    }

    /**
     * Create new supplier
     *
     * @param createSupplierModel
     * @return supplier
     */
    public SupplierModel createSupplier(CreateSupplierModel createSupplierModel, String logoUrl) {
        //Check existed username or email in DB
        if (accountRepository.existsByUsername(createSupplierModel.getUsername()))
            throw new DuplicatedEntityException("This username existed");
        if (accountRepository.existsByEmail(createSupplierModel.getEmail()))
            throw new DuplicatedEntityException("This email has been already for another account");

        //Save new account
        AccountEntity createdAccount = modelMapper.map(createSupplierModel, AccountEntity.class);
        createdAccount.setStatus(EntityStatusEnum.AccountStatusEnum.ACTIVE.ordinal());
        createdAccount.setRole(UserEnum.RoleEnum.SUPPLIER.toString());
        createdAccount.setPassword(passwordEncoder.encode(createSupplierModel.getPassword()));
        AccountEntity insertedAccount = accountRepository.save(createdAccount);

        //Save new information of supplier
        SupplierEntity createdSupplier = modelMapper.map(createSupplierModel, SupplierEntity.class);
        createdSupplier.setStatus(EntityStatusEnum.AccountStatusEnum.ACTIVE.ordinal());
        createdSupplier.setAccountId(insertedAccount.getId());
        createdSupplier.setCreateAt(LocalDateTime.now());
        createdSupplier.setUpdateAt(LocalDateTime.now());
        createdSupplier.setLogo(logoUrl);
        createdSupplier.setCreateBy(UUID.fromString("e5facdba-b1b5-4077-8483-a9bfbb187955"));
        SupplierEntity insertedSupplier = supplierRepository.save(createdSupplier);

        //Create new wallet for supplier
        WalletEntity createdWallet = new WalletEntity();
        createdWallet.setAccountId(insertedAccount.getId());
        createdWallet.setOwnerType(UserEnum.WalletTypeEnum.SUPPLIER.toString());
        createdWallet.setStatus(EntityStatusEnum.AccountStatusEnum.ACTIVE.ordinal());
        createdWallet.setAmount(0);
        createdWallet.setCreateAt(LocalDateTime.now());
        WalletEntity insertedWallet = walletRepository.save(createdWallet);

        //Return saved supplier model
        SupplierModel supplierModel = modelMapper.map(insertedSupplier, SupplierModel.class);
        supplierModel.setAccountModel(modelMapper.map(insertedAccount, AccountModel.class));
        return supplierModel;
    }

    /**
     * Delete a supplier by id
     *
     * @param supplierId
     * @return deleted supplier model
     */
    public SupplierModel deleteSupplier(UUID supplierId) {
        Optional<SupplierEntity> optionalSupplierEntity = supplierRepository.findById(supplierId);
        SupplierEntity supplierEntity = optionalSupplierEntity.orElseThrow(() -> new NoSuchEntityException("Not found supplier with id"));

        Optional<AccountEntity> optionalAccountEntity = accountRepository.findById(supplierEntity.getAccountId());
        AccountEntity accountEntity = optionalAccountEntity.orElseThrow(() -> new NoSuchEntityException("Not found supplier with id"));

        //Update supplier information
        supplierEntity.setStatus(EntityStatusEnum.AccountStatusEnum.DISABLE.ordinal());
        supplierEntity.setUpdateAt(LocalDateTime.now());
        SupplierEntity deletedSupplierEntity = supplierRepository.save(supplierEntity);

        //Update supplier account
        accountEntity.setStatus(EntityStatusEnum.AccountStatusEnum.DISABLE.ordinal());
        AccountEntity deletedAccountEntity = accountRepository.save(accountEntity);

        //Return deleted supplier model
        SupplierModel supplierModel = modelMapper.map(deletedSupplierEntity, SupplierModel.class);
        supplierModel.setAccountModel(modelMapper.map(deletedAccountEntity, AccountModel.class));
        return supplierModel;
    }

    /**
     * Find supplier model by id
     *
     * @param id
     * @return supplier model
     */
    public SupplierModel findSupplierById(UUID id) {
        //Find supplier information by id
        Optional<SupplierEntity> optionalSupplierEntity = supplierRepository.findById(id);
        SupplierEntity supplierEntity = optionalSupplierEntity.orElseThrow(() -> new NoSuchEntityException("Not found supplier with id"));

        //Find account information of supplier
        Optional<AccountEntity> optionalAccountEntity = accountRepository.findById(supplierEntity.getAccountId());
        AccountEntity accountEntity = optionalAccountEntity.orElseThrow(() -> new NoSuchEntityException("Not found account of supplier with id"));

        //Return supplier model
        SupplierModel supplierModel = modelMapper.map(supplierEntity, SupplierModel.class);
        supplierModel.setAccountModel(modelMapper.map(accountEntity, AccountModel.class));
        return supplierModel;
    }

    /**
     * Delete list of supplier
     *
     * @param ids
     * @return list of deleted supplier models
     */
    public List<SupplierModel> deleteSuppliersByIds(List<UUID> ids) {
        if (ids == null)
            throw new IllegalArgumentException("Not found any suppliers");
        List<SupplierModel> supplierModels = new ArrayList<>();

        for (UUID id : ids) {
            SupplierModel supplierModel = deleteSupplier(id);
            supplierModels.add(supplierModel);
        }
        return supplierModels;
    }

    /**
     * Update supplier
     *
     * @param updateSupplierModel
     * @param logoUrl
     * @return update supplier model
     */
    public SupplierModel updateSupplier(UpdateSupplierModel updateSupplierModel, String logoUrl) {
        //Check existed supplier
        Optional<SupplierEntity> optionalSupplierEntity = supplierRepository.findById(updateSupplierModel.getId());
        SupplierEntity supplierEntity = optionalSupplierEntity.orElseThrow(() -> new NoSuchEntityException("Not found supplier with id"));

        //Check existed account of supplier
        Optional<AccountEntity> optionalAccountEntity = accountRepository.findById(supplierEntity.getAccountId());
        AccountEntity accountEntity = optionalAccountEntity.orElseThrow(() -> new NoSuchEntityException("Not found account of supplier with id"));

        //Save supplier information
        if (logoUrl != null) supplierEntity.setLogo(logoUrl);
        supplierEntity.setAddress(updateSupplierModel.getAddress());
        supplierEntity.setName(updateSupplierModel.getName());
        supplierEntity.setStatus(updateSupplierModel.getStatus());
        supplierEntity.setDescription(updateSupplierModel.getDescription());
        supplierEntity.setPhone(updateSupplierModel.getPhone());
        supplierEntity.setUpdateAt(LocalDateTime.now());
        SupplierEntity savedSupplier = supplierRepository.save(supplierEntity);

        //Save account information of supplier
        accountEntity.setStatus(updateSupplierModel.getStatus());
        accountEntity.setEmail(updateSupplierModel.getEmail());
        AccountEntity savedAccount = accountRepository.save(accountEntity);

        //Prepare model for response
        SupplierModel updatedSupplierModel = modelMapper.map(savedSupplier, SupplierModel.class);
        updatedSupplierModel.setAccountModel(modelMapper.map(savedAccount, AccountModel.class));
        return updatedSupplierModel;
    }

    /**
     * Specification for search name
     *
     * @param searchedValue
     * @return contains name specification
     */
    private Specification<SupplierEntity> containsName(String searchedValue) {
        return ((root, query, criteriaBuilder) -> {
            String pattern = searchedValue != null ? "%" + searchedValue + "%" : "%" + "%";
            return criteriaBuilder.like(root.get(SupplierEntity_.NAME), pattern);
        });
    }

    /**
     * Specification for search address
     *
     * @param searchedValue
     * @return contains address specification
     */
    private Specification<SupplierEntity> containsAddress(String searchedValue) {
        return ((root, query, criteriaBuilder) -> {
            String pattern = searchedValue != null ? "%" + searchedValue + "%" : "%" + "%";
            return criteriaBuilder.like(root.get(SupplierEntity_.ADDRESS), pattern);
        });
    }

    /**
     * Specification for search phone
     *
     * @param searchedValue
     * @return contains phone specification
     */
    private Specification<SupplierEntity> containsPhone(String searchedValue) {
        return ((root, query, criteriaBuilder) -> {
            String pattern = searchedValue != null ? "%" + searchedValue + "%" : "%" + "%";
            return criteriaBuilder.like(root.get(SupplierEntity_.PHONE), pattern);
        });
    }

    /**
     * Specification for filter status
     *
     * @param searchStatus
     * @return status filter specification
     */
    private Specification<SupplierEntity> statusFilter(int searchStatus) {
        return ((root, query, criteriaBuilder) -> {
            if (searchStatus < StatusSearchEnum.AccountStatusSearchEnum.ALL.ordinal()) {
                return criteriaBuilder.equal(root.get(SupplierEntity_.STATUS), searchStatus);
            } else {
                return criteriaBuilder.lessThan(root.get(SupplierEntity_.STATUS), StatusSearchEnum.AccountStatusSearchEnum.ALL.ordinal());
            }
        });
    }

    /**
     * Search and filter supplier
     * @param searchedValue
     * @param paginationRequestModel
     * @param supplierFilterModel
     * @return resource of data
     */
    public ResourceModel<SupplierModel> searchSuppliers(String searchedValue, PaginationRequestModel paginationRequestModel,
                                                        SupplierFilterModel supplierFilterModel) {
        PaginationConvertor<SupplierModel, SupplierEntity> paginationConvertor = new PaginationConvertor<>();

        String defaultSortBy = SupplierEntity_.NAME;
        Pageable pageable = paginationConvertor.convertToPageable(paginationRequestModel, defaultSortBy, SupplierEntity.class);

        //Find all suppliers
        Page<SupplierEntity> supplierEntityPage = supplierRepository.findAll(
                containsName(searchedValue)
                        .and(statusFilter(supplierFilterModel.getStatusType()))
                        .and(containsAddress(supplierFilterModel.getAddress()))
                        .and(containsPhone(supplierFilterModel.getPhone()))
                        .and(containsName(supplierFilterModel.getName())), pageable);

        //Convert list of suppliers entity to list of suppliers model
        List<SupplierModel> supplierModels = new ArrayList<>();
        for (SupplierEntity entity : supplierEntityPage) {
            SupplierModel supplierModel = modelMapper.map(entity, SupplierModel.class);
            Optional<AccountEntity> optionalAccountEntity = accountRepository.findById(entity.getAccountId());
            AccountEntity accountEntity = optionalAccountEntity
                    .orElseThrow(() -> new NoSuchEntityException("Not found account of supplier with id"));
            supplierModel.setAccountModel(modelMapper.map(accountEntity, AccountModel.class));
            supplierModels.add(supplierModel);
        }

        //Prepare resource for return
        ResourceModel<SupplierModel> resource = new ResourceModel<>();
        resource.setData(supplierModels);
        resource.setSearchText(searchedValue);
        resource.setSortBy(defaultSortBy);
        resource.setSortType(paginationRequestModel.getSortType());
        paginationConvertor.buildPagination(paginationRequestModel, supplierEntityPage, resource);
        return resource;
    }

    /**
     * Find supplier by usernmae
     * @param username
     * @return supplier model
     */
    public SupplierModel findSupplierByUsername(String username) {
        Optional<AccountEntity> optionalAccountEntity = accountRepository.findByUsername(username);
        AccountEntity accountEntity = optionalAccountEntity.orElseThrow(() -> new NoSuchEntityException("Not found account with username"));

        Optional<SupplierEntity> optionalSupplierEntity = supplierRepository.findByAccountId(accountEntity.getId());
        SupplierEntity supplierEntity = optionalSupplierEntity.orElseThrow(() -> new NoSuchEntityException("Not found information of supplier"));
        return  modelMapper.map(supplierEntity, SupplierModel.class);
    }
}
