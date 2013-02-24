package JavaSource.com.apress.expertspringmvc.flight.web;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.support.RequestMethodNotSupportedException;

import JavaSource.com.apress.expertspringmvc.flight.domain.Account;
import JavaSource.com.apress.expertspringmvc.flight.service.AccountNotFoundException;
import JavaSource.com.apress.expertspringmvc.flight.service.AccountService;

public class AccountController extends MultiActionController {
    
    private AccountService accountService;
    
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    public ModelAndView view(HttpServletRequest request,
            HttpServletResponse response) throws IOException,
            RequestMethodNotSupportedException {
        ensureMethod(request, METHOD_GET);
        if (!validatePresenceOf(request, response, "id")) return null;
        String id = request.getParameter("id");
        return new ModelAndView("viewSuccess", "account",
                accountService.findAccount(new Long(id)));
    }

    public long viewLastModified(HttpServletRequest request) {
        String id = request.getParameter("id");
        if (! StringUtils.hasText(id)) {
            return -1;
        }
        Account account = accountService.findAccount(new Long(id));
        return account.getLastUpdated().getTime();
    }

    public ModelAndView create(HttpServletRequest request,
            HttpServletResponse response, final Account account, BindException errors) throws IOException,
            RequestMethodNotSupportedException {
        
        return new FormWorkflow() {

            @Override
            void onGet() {
                setViewName("createAccount");
            }

            @Override
            void onPost() {
                accountService.saveAccount(account);
            }
            
        }.handleRequest(request, response, account, errors);

    }

    public ModelAndView update(HttpServletRequest request,
            HttpServletResponse response, Account account) throws
            RequestMethodNotSupportedException {
        ensureMethod(request, METHOD_POST);
        accountService.updateAccount(account);
        return new ModelAndView("updateSuccess", "account", account);
    }
    
    public ModelAndView delete(HttpServletRequest request,
            HttpServletResponse response, Account account) throws
            RequestMethodNotSupportedException {
        ensureMethod(request, METHOD_POST);
        accountService.deleteAccount(account);
        return new ModelAndView("deleteSuccess", "account", account);
    }
    
    public ModelAndView accountNotFound(HttpServletRequest request,
            HttpServletResponse response, AccountNotFoundException ex) {
        return new ModelAndView("accountNotFound", "account", ex.getAccount());
    }
    
    private void ensureMethod(HttpServletRequest request, String ... methods)
        throws RequestMethodNotSupportedException {
        for (String method : methods) {
            if (request.getMethod().equals(method)) {
                return;
            }
        }
        throw new RequestMethodNotSupportedException("Request method '" +
                request.getMethod() + "' not supported");
    }
    
    private boolean validatePresenceOf(HttpServletRequest request,
            HttpServletResponse response, String ... params) throws IOException {
        for (String param : params) {
            if (StringUtils.hasText(request.getParameter(param))) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        param + " must be provided");
                return false;
            }
        }
        return true;
    }
    
    private abstract class FormWorkflow {
        private String viewName;
        private Map model;
        
        abstract void onGet();
        abstract void onPost();
        
        protected final void setViewName(String viewName) {
            this.viewName = viewName;
        }
        
        ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse res,
                Object command, BindException errors) throws RequestMethodNotSupportedException {
            ensureMethod(req, METHOD_POST, METHOD_GET);
            model = errors.getModel();
            if (req.getMethod().equals(METHOD_GET)) {
                onGet();
            }
            if (req.getMethod().equals(METHOD_POST)) {
                if (! errors.hasErrors()) {
                    onPost();
                } else {
                    onGet();
                }
            }
            ModelAndView mav = new ModelAndView(viewName, model);
            return mav;
        }
    }
}
