package com.tamp_backend.service;

import com.tamp_backend.customexception.NoSuchEntityException;
import com.tamp_backend.entity.AccountEntity;
import com.tamp_backend.entity.SystemAdminEntity;
import com.tamp_backend.model.systemadmin.SystemAdminModel;
import com.tamp_backend.repository.AccountRepository;
import com.tamp_backend.repository.SystemAdminRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
}
