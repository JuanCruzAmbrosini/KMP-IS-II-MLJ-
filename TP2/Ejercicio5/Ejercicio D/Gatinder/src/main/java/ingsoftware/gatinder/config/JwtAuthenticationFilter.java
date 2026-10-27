package ingsoftware.gatinder.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import ingsoftware.gatinder.entity.User;
import ingsoftware.gatinder.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired private JwtService jwtService;
    @Autowired private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String token = bearerToken(request);
        if (token == null) {
            token = cookieToken(request);
        }

        if (token != null && jwtService.isValid(token)
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                String userId = jwtService.parse(token).getSubject();
                User user = userService.findById(userId);
                if (!user.isDeleted()) {
                    var authentication = new UsernamePasswordAuthenticationToken(
                            user.getEmail(), null, java.util.List.of());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception ignored) {
                // Invalid or revoked tokens remain unauthenticated and are rejected by authorization.
            }
        }
        filterChain.doFilter(request, response);
    }

    private String bearerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        return header != null && header.startsWith("Bearer ")
                ? header.substring(7).trim() : null;
    }

    private String cookieToken(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if (RememberMeInterceptor.COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
