package com.tamp_backend.security;

import com.tamp_backend.constant.EntityStatusEnum;
import com.tamp_backend.constant.UserEnum;
import com.tamp_backend.customexception.NoSuchEntityException;
import com.tamp_backend.entity.AccountEntity;
import com.tamp_backend.repository.AccountRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    public  CustomUserDetailsService (AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<SimpleGrantedAuthority> roles;
        Optional<AccountEntity> optionalAccountEntity = accountRepository.findByUsername(username);
        AccountEntity accountEntity = optionalAccountEntity.orElseThrow(() -> new NoSuchEntityException("Not found account with username"));
        if(accountEntity.getStatus() == EntityStatusEnum.AccountStatusEnum.DISABLE.ordinal())
            throw new NoSuchEntityException("Not found account with username");
        if(accountEntity.getRole().equals(UserEnum.RoleEnum.SYSTEM_ADMIN.toString())) {
            roles = List.of(new SimpleGrantedAuthority("ROLE_" + UserEnum.RoleEnum.SYSTEM_ADMIN));
        } else if(accountEntity.getRole().equals(UserEnum.RoleEnum.AFFILIATOR.toString())) {
            roles = List.of(new SimpleGrantedAuthority("ROLE_" + UserEnum.RoleEnum.AFFILIATOR));
        } else if(accountEntity.getRole().equals(UserEnum.RoleEnum.PARTNER.toString())) {
            roles = List.of(new SimpleGrantedAuthority("ROLE_" + UserEnum.RoleEnum.PARTNER));
        } else if(accountEntity.getRole().equals(UserEnum.RoleEnum.SHIPPER.toString())) {
            roles = List.of(new SimpleGrantedAuthority("ROLE_" + UserEnum.RoleEnum.SHIPPER));
        } else if(accountEntity.getRole().equals(UserEnum.RoleEnum.SUPPLIER.toString())) {
            roles = List.of(new SimpleGrantedAuthority("ROLE_" + UserEnum.RoleEnum.SUPPLIER));
        } else {
            throw new NoSuchEntityException("User not found with username: " + username);
        }
        return new User(username, accountEntity.getPassword(), roles);
    }
}
