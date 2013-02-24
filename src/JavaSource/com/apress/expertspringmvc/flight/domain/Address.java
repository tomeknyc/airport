package JavaSource.com.apress.expertspringmvc.flight.domain;

public class Address {

    private Long id;
    private String street;
    private String city;
    private String state;
    private String postalCode;
    
    public Address() { }
    
    public Address(Long id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public Long getId() {
        return id;
    }
    public String getPostalCode() {
        return postalCode;
    }
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public String getStreet() {
        return street;
    }
    public void setStreet(String street) {
        this.street = street;
    }
}
