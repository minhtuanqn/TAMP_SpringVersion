package com.tamp_backend.controller;

import com.tamp_backend.model.ResponseModel;
import com.tamp_backend.model.account.AccountDetailsModel;
import com.tamp_backend.model.account.UsernameLoginModel;
import com.tamp_backend.security.CustomUserDetailsService;
import com.tamp_backend.security.JWTUtils;
import com.tamp_backend.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Validated
@RequestMapping(path = "/accounts")
public class AccountController {
    private AccountService accountService;
    private AuthenticationManager authenticationManager;
    private JWTUtils jwtUtils;
    private CustomUserDetailsService userDetailsService;

    public AccountController(AccountService accountService,
                             AuthenticationManager authenticationManager,
                             JWTUtils jwtUtils, CustomUserDetailsService userDetailsService) {
        this.accountService = accountService;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    /**
     *
     * @param usernameLoginModel
     * @return response model contain token
     * @throws Exception
     */
    @PostMapping(path = "/login/username", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseModel> loginByUsername(@Valid @RequestBody UsernameLoginModel usernameLoginModel)
            throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    usernameLoginModel.getUsername(), usernameLoginModel.getPassword()));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
        final UserDetails userDetails = userDetailsService.loadUserByUsername(usernameLoginModel.getUsername());
        final AccountDetailsModel accountDetailsModel = accountService.buildAccountDetailModel(userDetails.getUsername());
        final String token = jwtUtils.generateToken(accountDetailsModel);

        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                                                         .data(token)
                                                         .message("Login successfully");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }
}
