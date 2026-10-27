package ingsoftware.gatinder.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import ingsoftware.gatinder.dto.UserDto;
import ingsoftware.gatinder.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RememberMeInterceptor implements HandlerInterceptor {
    public static final String COOKIE_NAME = "GATINDER_REMEMBER_ME";
    public static final int COOKIE_MAX_AGE = 2 * 24 * 60 * 60;
    public static final String SESSION_USER = "loggedUser";

    @Autowired private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (request.getSession().getAttribute(SESSION_USER) != null) {
            return true;
        }
        Cookie rememberCookie = findCookie(request.getCookies());
        if (rememberCookie == null) {
            return true;
        }
        UserDto user = userService.findByRememberToken(rememberCookie.getValue());
        if (user == null) {
            deleteCookie(response);
        } else {
            request.getSession().setAttribute(SESSION_USER, user);
        }
        return true;
    }

    private Cookie findCookie(Cookie[] cookies) {
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (COOKIE_NAME.equals(cookie.getName())) {
                return cookie;
            }
        }
        return null;
    }

    public static void addCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(COOKIE_NAME, token);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(COOKIE_MAX_AGE);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public static void deleteCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(COOKIE_NAME, "");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
