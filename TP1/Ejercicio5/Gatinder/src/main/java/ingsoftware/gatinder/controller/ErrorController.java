package ingsoftware.gatinder.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ErrorController {

    @RequestMapping("/error-page")
    public ModelAndView renderErrorPage(HttpServletRequest httpRequest) {
        ModelAndView errorPage = new ModelAndView("error");
        int httpErrorCode = getErrorCode(httpRequest);
        String errorMsg;

        switch (httpErrorCode) {
            case 400:
                errorMsg = "Http Error Code: 400. Bad Request";
                break;
            case 401:
                errorMsg = "Http Error Code: 401. Unauthorized";
                break;
            case 403:
                errorMsg = "Http Error Code: 403. Forbidden";
                break;
            case 404:
                errorMsg = "Http Error Code: 404. Resource not found";
                break;
            case 500:
                errorMsg = "Http Error Code: 500. Internal Server Error";
                break;
            default:
                errorMsg = "Http Error Code: " + httpErrorCode + ". Error Inesperado";
                break;
        }

        errorPage.addObject("errorMsg", errorMsg);
        return errorPage;
    }

    private int getErrorCode(HttpServletRequest httpRequest) {
        Object statusCode = httpRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (statusCode == null) {
            statusCode = httpRequest.getAttribute("jakarta.servlet.error.status_code");
        }
        if (statusCode == null) {
            statusCode = httpRequest.getAttribute("javax.servlet.error.status_code");
        }
        if (statusCode instanceof Integer) {
            return (Integer) statusCode;
        }
        return 500;
    }
}
