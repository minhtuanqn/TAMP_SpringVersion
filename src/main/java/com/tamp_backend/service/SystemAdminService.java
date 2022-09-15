package com.tamp_backend.service;

import com.tamp_backend.repository.SystemAdminRepository;
import org.springframework.stereotype.Service;

@Service
public class SystemAdminService {

    private SystemAdminRepository systemAdminRepository;

    public SystemAdminService(SystemAdminRepository systemAdminRepository) {
        this.systemAdminRepository = systemAdminRepository;
    }
}
