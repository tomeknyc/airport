package JavaSource.com.apress.expertspringmvc.flight.service;

import java.util.List;

import JavaSource.com.apress.expertspringmvc.flight.domain.Account;

public interface AccountService {

    void saveAccount(Account account);
    
    void updateAccount(Account account) throws AccountNotFoundException;
    
    void deleteAccount(Account account) throws AccountNotFoundException;

    Account findAccount(Long id) throws AccountNotFoundException;

    Account findAccountByUsername(String username) throws AccountNotFoundException;

    List<Account> findAccountsByFirstName(String searchBy);

    List<Account> findAccountsByLastName(String searchBy);

    void cancelAccount(String username) throws AccountNotFoundException;
    
}
