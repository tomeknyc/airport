package JavaSource.com.apress.expertspringmvc.flight.web;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class CreateAccountValidator implements Validator {
    
    private Validator addressValidator = new AddressValidator();
    
    public boolean supports(Class clazz) {
        return CreateAccount.class.isAssignableFrom(clazz);
    }

    public void validate(Object obj, Errors errors) {
        validatePage0((CreateAccount)obj, errors);
        validatePage1((CreateAccount)obj, errors);
    }

    public void validatePage0(CreateAccount command, Errors errors) {
        isRequired(errors, "account.username");
        isRequired(errors, "account.password");
        isRequired(errors, "account.email");
        isRequired(errors, "confirmPassword");
        
        if (! command.getAccount().getPassword().equals(
                command.getConfirmPassword())) {
            errors.rejectValue("account.password", "errors.must-match",
                    new Object[]{
                    new DefaultMessageSourceResolvable("account.password"),
                    new DefaultMessageSourceResolvable("confirmPassword")},
                    "Password must match Confirm Password");
        }
        
        String email = command.getAccount().getEmail();
        if (email != null && email.indexOf('@') < 0) {
            errors.rejectValue("account.email", "errors.invalid-format",
                    new Object[]{
                    new DefaultMessageSourceResolvable("account.email")},
                    "Email must have a valid format.");
        }
    }
    
    public void validatePage1(CreateAccount command, Errors errors) {
        addressValidator.validate(command.getAccount().getBillingAddress(),
                errors);
    }
    
    protected void isRequired(Errors errors, String fieldName) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors,
                fieldName,
                "errors.required", new Object[]{
                new DefaultMessageSourceResolvable(fieldName)},
                fieldName + " is required");
    }

}
