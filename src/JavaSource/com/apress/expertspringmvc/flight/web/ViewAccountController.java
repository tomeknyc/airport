package JavaSource.com.apress.expertspringmvc.flight.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContextException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.support.RequestMethodNotSupportedException;

import JavaSource.com.apress.expertspringmvc.flight.domain.Account;
import JavaSource.com.apress.expertspringmvc.flight.service.AccountNotFoundException;
import JavaSource.com.apress.expertspringmvc.flight.service.AccountService;

public class ViewAccountController extends MultiActionController {
    
    private AccountService accountService;
   
    public ViewAccountController() throws ApplicationContextException {
        setSupportedMethods(new String[]{METHOD_GET});
    }

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }
    
    public ModelAndView accountNotFound(HttpServletRequest request,
            HttpServletResponse response, AccountNotFoundException e) {
        List<String> errorMessages = new ArrayList<String>();
        errorMessages.add("No account found for " +
                request.getParameter("searchBy"));
        return new ModelAndView("accountFindError", "errorMessages",
                errorMessages);
    }
    
    public ModelAndView findByUsername(HttpServletRequest request,
            HttpServletResponse response, SearchCriteria criteria)
            throws RequestMethodNotSupportedException {
        ensureMethod(request, METHOD_GET);
        Account account = accountService.findAccountByUsername(
                criteria.getSearchBy());
        return new ModelAndView("viewAccount", "account", account);
    }
    
    public ModelAndView findByFirstName(HttpServletRequest request,
            HttpServletResponse response, SearchCriteria criteria) {
        List<Account> accounts = accountService.findAccountsByFirstName(
                criteria.getSearchBy());
        return new ModelAndView("viewAccounts", "accounts", accounts);
    }

    public ModelAndView findByLastName(HttpServletRequest request,
            HttpServletResponse response, SearchCriteria criteria) {
        List<Account> accounts = accountService.findAccountsByLastName(
                criteria.getSearchBy());
        return new ModelAndView("viewAccounts", "accounts", accounts);
    }
    
    public static class SearchCriteria {
        private String searchBy;

        public String getSearchBy() {
            return searchBy;
        }

        public void setSearchBy(String searchBy) {
            this.searchBy = searchBy;
        }
    }
    
    private void ensureMethod(HttpServletRequest request, String ... methods)
            throws RequestMethodNotSupportedException {
        for (String method : methods) {
            if (request.getMethod().equals(method)) {
                return;
            }
        }
        throw new RequestMethodNotSupportedException("The request method " +
                request.getMethod() + " is not allowed");
    }
}
