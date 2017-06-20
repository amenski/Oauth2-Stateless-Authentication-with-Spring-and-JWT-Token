package com.tinmegali.controllers;

import com.tinmegali.exceptions.RestError;
import com.tinmegali.models.Account;
import com.tinmegali.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.login.AccountException;

@RestController
@PreAuthorize("isAuthenticated()")
public class ApiController {

    @Autowired
    private AccountService accountService;

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/api/hello")
    public ResponseEntity<?> hello() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        String msg = String.format("Hello %s", name);
        return new ResponseEntity<Object>(msg, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping(path = "/api/me", produces = "application/json" )
    public Account me() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return accountService.findAccountByUsername(username);
    }

    @PreAuthorize("hasAuthority('ROLE_REGISTER')")
    @PostMapping(path = "/api/register", produces = "application/json")
    public ResponseEntity<?> register(@RequestBody Account account) {
        try {
            account.grantAuthority("USER");
            return new ResponseEntity<Object>(
                    accountService.register( account ), HttpStatus.OK);
        } catch (AccountException e) {
            e.printStackTrace();
            return new ResponseEntity<RestError>(new RestError(e.getMessage()),HttpStatus.BAD_REQUEST );
        }
    }

}
