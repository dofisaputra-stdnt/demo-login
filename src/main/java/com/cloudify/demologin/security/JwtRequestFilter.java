package com.cloudify.demologin.security;

import com.cloudify.demologin.config.tenant.TenantIdentifierResolver;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtRequestFilter(JwtService jwtService, UserDetailsServiceImpl userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        return isMatchPublicPaths(request);
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String jwt = authHeader.substring(7);
            jwtService.validateToken(jwt);

            String username = jwtService.extractUsername();
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            String tenant = jwtService.extractTenant();
            TenantIdentifierResolver.setCurrentTenant(tenant);

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authToken);
            SecurityContextHolder.setContext(context);
        } catch (Exception e) {
            logger.error("Invalid JWT token: {}");
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            TenantIdentifierResolver.setCurrentTenant("public");
        }
    }

    private static boolean isMatchPublicPaths(HttpServletRequest request) {
        return Arrays.stream(AuthHelper.PUBLIC_PATHS).anyMatch(e -> isMatchWithStar(e, request.getServletPath()));
    }

    private static boolean isMatchWithStar(String a, String b){
        String star = "*";
        a = a.replace(star, "");
        b = b.substring(0, Math.min(b.length(), a.length()));
        return a.equals(b);
    }
}
