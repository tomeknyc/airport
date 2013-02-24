package JavaSource.com.apress.expertspringmvc.flight.service;

import JavaSource.com.apress.expertspringmvc.flight.domain.Account;

public class AccountNotFoundException extends RuntimeException {

    private Account account;
    
    public AccountNotFoundException(Account account) {
        this.account = account;
    }

    public AccountNotFoundException() {
        // TODO Auto-generated constructor stub
    }

    public Account getAccount() {
        return account;
    }

}
