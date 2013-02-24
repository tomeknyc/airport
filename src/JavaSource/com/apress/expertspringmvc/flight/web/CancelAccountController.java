package JavaSource.com.apress.expertspringmvc.flight.web;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.throwaway.ThrowawayController;

import JavaSource.com.apress.expertspringmvc.flight.service.AccountNotFoundException;
import JavaSource.com.apress.expertspringmvc.flight.service.AccountService;

public class CancelAccountController implements ThrowawayController {
    
    private AccountService accountService;
    
    private String username;
    
    public void setUsername(String username) {
        this.username = username;
    }

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    public ModelAndView execute() throws Exception {
        if (!StringUtils.hasText(username)) {
            return new ModelAndView("cancelAccount", "errorMessage",
                    "Username must not be blank.");
        }
        try {
            accountService.cancelAccount(username);
            return new ModelAndView("cancelAccountSuccess");
        } catch(AccountNotFoundException e) {
            return new ModelAndView("cancelAccount", "errorMessage",
                    "No account found with username " + username);
        }
    }

}
