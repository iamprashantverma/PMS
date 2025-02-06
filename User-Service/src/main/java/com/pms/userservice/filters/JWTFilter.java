package com.pms.userservice.filters;

import com.pms.userservice.entities.User;
import com.pms.userservice.services.JWTService;
import com.pms.userservice.services.UserService;
import com.pms.userservice.services.impl.UserServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
@Slf4j
public class JWTFilter extends OncePerRequestFilter {

    /* inject the Target class objects */
    private final JWTService jwtService;
    private final UserService userService;


    public JWTFilter(JWTService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

            /* Authorization header Extracted */
            String tokenHeader = request.getHeader("Authorization");

            /* passing the request to next filter if Header not found*/
            if ( tokenHeader == null || !tokenHeader.startsWith("Bearer") ) {
                filterChain.doFilter(request,response);
            } else {
                /* bearer token extracted */
                String bearerToken = tokenHeader.split(" ")[1];
                /* Validating the Bearer Token and fetch the user id */
                String userId = jwtService.getUserIdFromToken(bearerToken);
                if (userId != null ) {

                    /* fetch the user by  their id  */
                    User user = userService.getUserById(userId);

                    /* insuring the user is Enabled */
                    if ( !user.isEnabled())
                        throw  new DisabledException("User Account is Disabled , Please Contact Support ");
                    /* set the user in the Security Context Holder */

                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user,null,user.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource());
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
                /* passing the Request to the next filters */
                filterChain.doFilter(request,response);
            }

    }
}
