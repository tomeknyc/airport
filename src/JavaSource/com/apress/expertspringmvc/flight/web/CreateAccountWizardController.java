package JavaSource.com.apress.expertspringmvc.flight.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractWizardFormController;

import JavaSource.com.apress.expertspringmvc.flight.service.AccountService;

public class CreateAccountWizardController extends AbstractWizardFormController {
    
    private AccountService accountService;

    public CreateAccountWizardController() {
        setCommandName("createAccount");
        setCommandClass(CreateAccount.class);
        setValidator(new CreateAccountValidator());
        setPages(new String[]{"usernameAndEmail", "billingAddress"});
    }
    
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    protected void validatePage(Object command, Errors errors, int page) {
        CreateAccount createAccount = (CreateAccount) command;
        CreateAccountValidator validator = (CreateAccountValidator) getValidator();
        switch (page) {
        case 0:
            validator.validatePage0(createAccount, errors);
            break;
        case 1:
            validator.validatePage1(createAccount, errors);
            break;
        }
    }

    @Override
    protected ModelAndView processFinish(HttpServletRequest request,
            HttpServletResponse response, Object command, BindException errors)
            throws Exception {
        CreateAccount createAccount = (CreateAccount) command;
        accountService.saveAccount(createAccount.getAccount());
        return new ModelAndView("createAccountSuccess",
                "account", createAccount.getAccount());
    }
    
}
