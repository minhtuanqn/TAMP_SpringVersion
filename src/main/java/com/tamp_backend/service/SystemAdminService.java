package com.tamp_backend.service;

import com.tamp_backend.customexception.NoSuchEntityException;
import com.tamp_backend.entity.AccountEntity;
import com.tamp_backend.entity.CategoryEntity;
import com.tamp_backend.entity.SystemAdminEntity;
import com.tamp_backend.model.category.CategoryModel;
import com.tamp_backend.model.systemaccount.AccountModel;
import com.tamp_backend.model.systemadmin.SystemAdminModel;
import com.tamp_backend.repository.AccountRepository;
import com.tamp_backend.repository.SystemAdminRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class SystemAdminService {

    private SystemAdminRepository systemAdminRepository;
    private AccountRepository accountRepository;
    private ModelMapper modelMapper;

    public SystemAdminService(SystemAdminRepository systemAdminRepository,
                              AccountRepository accountRepository,
                              ModelMapper modelMapper) {
        this.systemAdminRepository = systemAdminRepository;
        this.accountRepository = accountRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Find system admin by username
     * @param username
     * @return system admin model
     */
    public SystemAdminModel findSystemAdminByUsername(String username) {
        Optional<AccountEntity> optionalAccountEntity = accountRepository.findByUsername(username);
        AccountEntity accountEntity = optionalAccountEntity.orElseThrow(() -> new NoSuchEntityException("Not found account with username"));

        Optional<SystemAdminEntity> optionalSystemAdminEntity = systemAdminRepository.findByAccountId(accountEntity.getId());
        SystemAdminEntity systemAdminEntity = optionalSystemAdminEntity.orElseThrow(() -> new NoSuchEntityException("Not found information of system admin"));
        return  modelMapper.map(systemAdminEntity, SystemAdminModel.class);
    }

    /**
     * Find a system admin by id
     * @param id
     * @return found model
     */
    public SystemAdminModel findSystemAdminById(UUID id) {
        //Find system admin with id
        Optional<SystemAdminEntity> systemAdminOptional = systemAdminRepository.findById(id);
        SystemAdminEntity systemAdmin = systemAdminOptional.orElseThrow(() -> new NoSuchEntityException("Not found account of system admin"));

        //Find account of system admin
        Optional<AccountEntity> accountOptional = accountRepository.findById(systemAdmin.getAccountId());
        AccountEntity account = accountOptional.orElseThrow(() -> new NoSuchEntityException("Not found account of system admin"));
        SystemAdminModel systemAdminModel = modelMapper.map(systemAdmin, SystemAdminModel.class);
        systemAdminModel.setAccountModel(modelMapper.map(account, AccountModel.class));
        return systemAdminModel;
    }

}
