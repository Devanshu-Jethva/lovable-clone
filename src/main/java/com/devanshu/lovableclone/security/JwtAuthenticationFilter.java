package com.devanshu.lovableclone.security;

import com.devanshu.lovableclone.exceptions.ErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    JwtUtil jwtUtil;
    ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.debug("Incoming request: {}", request.getRequestURI());

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        try {
            JwtUserPrincipal principal = jwtUtil.verifyToken(token);
            if (principal != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(principal, null, principal.authorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("JWT authentication set for user: {}", principal.username());
            }
        } catch (ExpiredJwtException ex) {
            log.warn("Expired JWT token attempt on {}", request.getRequestURI());
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "EXPIRED_TOKEN", "JWT token has expired");
            return;
        } catch (SignatureException ex) {
            log.warn("Invalid JWT signature on {}", request.getRequestURI());
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "INVALID_SIGNATURE", "JWT signature verification failed");
            return;
        } catch (MalformedJwtException ex) {
            log.warn("Malformed JWT token on {}", request.getRequestURI());
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "MALFORMED_TOKEN", "JWT token format is invalid");
            return;
        } catch (JwtException ex) {
            log.warn("JWT validation failed on {}: {}", request.getRequestURI(), ex.getMessage());
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", "Invalid JWT token");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String errorCode, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(ErrorResponse.of(errorCode, message)));
    }
}
