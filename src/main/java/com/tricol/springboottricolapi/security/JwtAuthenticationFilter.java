package com.tricol.springboottricolapi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;


import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final ApplicationContext applicationContext;
    
    private JwtDecoder jwtDecoder;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String jwt = getJwtFromRequest(request);
        
        if (StringUtils.hasText(jwt)) {
            try {
                if (tokenProvider.validateToken(jwt)) {
                    String username = tokenProvider.getUsernameFromToken(jwt);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    filterChain.doFilter(request, response);
                    return;
                }
            } catch (Exception ex) {
                logger.debug("Not a local JWT: " + ex.getMessage());
            }

            // Try Keycloak JWT
            if (jwtDecoder == null) {
                try {
                    jwtDecoder = applicationContext.getBean(JwtDecoder.class);
                } catch (Exception e) {
                    // JwtDecoder not available
                }
            }
            
            if (jwtDecoder != null) {
                try {
                    Jwt decodedJwt = jwtDecoder.decode(jwt);
                    
                    // Extract roles from Keycloak JWT
                    Collection<GrantedAuthority> authorities = extractAuthorities(decodedJwt);
                    
                    JwtAuthenticationToken authentication = new JwtAuthenticationToken(decodedJwt, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    filterChain.doFilter(request, response);
                    return;
                } catch (Exception ex) {
                    logger.debug("Not a Keycloak JWT: " + ex.getMessage());
                }
            }

        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
    Map<String, Object> realmAccess = jwt.getClaim("realm_access");
    if (realmAccess == null || realmAccess.isEmpty()) {
        return Collections.emptyList();
    }

    Collection<String> roles = (Collection<String>) realmAccess.get("roles");
    if (roles == null || roles.isEmpty()) {
        return Collections.emptyList();
    }

    return roles.stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
}

}
