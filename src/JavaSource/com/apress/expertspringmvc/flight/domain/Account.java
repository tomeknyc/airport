package JavaSource.com.apress.expertspringmvc.flight.domain;

import java.util.Date;

import org.springframework.util.Assert;

public class Account {

    private Long id;
    private Name name = new Name();
    private String username;
    private String password;
    private String email;
    private Date lastUpdated;
    private Address billingAddress = new Address();

    public Account() { }
    
    public Account(Long id) {
        this.id = id;
    }

    public Account(Name name, String username, String password) {
        Assert.notNull(username, "Username may not be null");
        this.name = name;
        this.username = username;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }
    
    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        Assert.notNull(username, "Username may not be null");
        this.username = username;
    }
    
    public Address getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(Address billingAddress) {
        this.billingAddress = billingAddress;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (! (obj instanceof Account)) return false;
        Account account = (Account) obj;
        return (username.equals(account.username));
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }
    
}
