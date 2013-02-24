package JavaSource.com.apress.expertspringmvc.flight.service;

import java.util.ArrayList;
import java.util.List;

import JavaSource.com.apress.expertspringmvc.flight.domain.Account;

public class AccountServiceImpl implements AccountService {

    public void saveAccount(Account account) {
    }

    public void updateAccount(Account account) throws AccountNotFoundException {
    }

    public void deleteAccount(Account account) throws AccountNotFoundException {
    }

    public Account findAccount(Long id) throws AccountNotFoundException {
        Account account = new Account(id);
        return account;
    }

    public Account findAccountByUsername(String username) throws AccountNotFoundException {
        if (username == null) {
            throw new AccountNotFoundException();
        }
        Account account = new Account();
        account.setUsername(username);
        return account;
    }

    public List<Account> findAccountsByFirstName(String searchBy) {
        return new ArrayList<Account>();
    }

    public List<Account> findAccountsByLastName(String searchBy) {
        return new ArrayList<Account>();
    }

    public void cancelAccount(String username) throws AccountNotFoundException {
    }

}
