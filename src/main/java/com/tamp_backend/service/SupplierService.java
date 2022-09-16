package com.tamp_backend.service;

import com.tamp_backend.constant.EntityStatusEnum;
import com.tamp_backend.constant.UserEnum;
import com.tamp_backend.customexception.DuplicatedEntityException;
import com.tamp_backend.customexception.NoSuchEntityException;
import com.tamp_backend.entity.AccountEntity;
import com.tamp_backend.entity.SupplierEntity;
import com.tamp_backend.entity.WalletEntity;
import com.tamp_backend.model.supplier.CreateSupplierModel;
import com.tamp_backend.model.supplier.SupplierModel;
import com.tamp_backend.model.systemaccount.AccountModel;
import com.tamp_backend.repository.AccountRepository;
import com.tamp_backend.repository.SupplierRepository;
import com.tamp_backend.repository.WalletRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
                           WalletRepository walletRepository){
        this.supplierRepository = supplierRepository;
        this.modelMapper = modelMapper;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.walletRepository =walletRepository;
    }

    /**
     * Create new supplier
     * @param createSupplierModel
     * @return supplier
     */
    public SupplierModel createSupplier(CreateSupplierModel createSupplierModel, String logoUrl) {
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
}
