package JavaSource.com.apress.expertspringmvc.flight.web;

import JavaSource.com.apress.expertspringmvc.flight.domain.Account;

public class CreateAccount {

    private Account account = new Account();
    private String confirmPassword;
    
    public Account getAccount() {
        return account;
    }
    public void setAccount(Account account) {
        this.account = account;
    }
    public String getConfirmPassword() {
        return confirmPassword;
    }
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
    
}
