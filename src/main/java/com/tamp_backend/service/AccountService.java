package com.tamp_backend.service;

import com.tamp_backend.constant.UserEnum;
import com.tamp_backend.customexception.NoSuchEntityException;
import com.tamp_backend.entity.AccountEntity;
import com.tamp_backend.entity.PartnerEntity;
import com.tamp_backend.entity.SupplierEntity;
import com.tamp_backend.entity.SystemAdminEntity;
import com.tamp_backend.model.systemaccount.AccountDetailsModel;
import com.tamp_backend.repository.AccountRepository;
import com.tamp_backend.repository.PartnerRepository;
import com.tamp_backend.repository.SupplierRepository;
import com.tamp_backend.repository.SystemAdminRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AccountService {

    private AccountRepository accountRepository;
    private SystemAdminRepository systemAdminRepository;
    private SupplierRepository supplierRepository;
    private PartnerRepository partnerRepository;

    public AccountService(AccountRepository accountRepository,
                          SystemAdminRepository systemAdminRepository,
                          SupplierRepository supplierRepository,
                          PartnerRepository partnerRepository)
    {
        this.accountRepository = accountRepository;
        this.systemAdminRepository = systemAdminRepository;
        this.supplierRepository = supplierRepository;
        this.partnerRepository = partnerRepository;
    }

    public AccountDetailsModel buildAccountDetailModel(String username) {
        Optional<AccountEntity> optionalAccountEntity = accountRepository.findByUsername(username);
        AccountEntity accountEntity = optionalAccountEntity.orElseThrow(() -> new NoSuchEntityException("Not found account with username"));
        AccountDetailsModel accountDetailsModel = new AccountDetailsModel();
        if(accountEntity.getRole().equals(UserEnum.RoleEnum.SYSTEM_ADMIN.toString())) {
            Optional<SystemAdminEntity> optionalSystemAdminEntity = systemAdminRepository.findByAccountId(accountEntity.getId());
            SystemAdminEntity systemAdminEntity = optionalSystemAdminEntity.orElseThrow(() -> new NoSuchEntityException("Not found information of user"));
            accountDetailsModel.id(accountEntity.getId()).role(accountEntity.getRole())
                    .email(accountEntity.getEmail())
                    .username(username)
                    .userId(systemAdminEntity.getId())
                    .password(accountEntity.getPassword());
        } else if(accountEntity.getRole().equals(UserEnum.RoleEnum.SUPPLIER.toString())) {
            Optional<SupplierEntity> optionalSupplierEntity = supplierRepository.findByAccountId(accountEntity.getId());
            SupplierEntity supplierEntity = optionalSupplierEntity.orElseThrow(() -> new NoSuchEntityException("Not found information of user"));
            accountDetailsModel.id(accountEntity.getId()).role(accountEntity.getRole())
                    .email(accountEntity.getEmail())
                    .username(username)
                    .userId(supplierEntity.getId())
                    .password(accountEntity.getPassword());
        } else if(accountEntity.getRole().equals(UserEnum.RoleEnum.PARTNER.toString())) {
            Optional<PartnerEntity> optionalPartnerEntity = partnerRepository.findByAccountId(accountEntity.getId());
            PartnerEntity partnerEntity = optionalPartnerEntity.orElseThrow(() -> new NoSuchEntityException("Not found information of user"));
            accountDetailsModel.id(accountEntity.getId()).role(accountEntity.getRole())
                    .email(accountEntity.getEmail())
                    .username(username)
                    .userId(partnerEntity.getId())
                    .password(accountEntity.getPassword());
        } else if(accountEntity.getRole().equals(UserEnum.RoleEnum.SHIPPER.toString())) {

        } else if(accountEntity.getRole().equals(UserEnum.RoleEnum.AFFILIATOR.toString())) {

        } else {
            throw new NoSuchEntityException("Not found account with username");
        }
        return accountDetailsModel;
    }
}
