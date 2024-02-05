package com.capstone.uniculture.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    private final TokenProvider tokenProvider;


    // Request 헤더에서 토큰 정보를 꺼내오는 메소드이다.
    // Front 에서 AUTHORIZATION_HEADER 라는 헤더에 토큰 정보를 담아 보내므로 꼭 필요하다.
    // ** substring 은 프론트에서 보내는 내용에따라 나중에 손봐야할것중 하나임 **
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7); // 앞에 prefix 짜르기 위해
        }
        return null;
    }

    // 필터링을 진행하는 메소드이다
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = resolveToken(request);
        if(StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)){ // 유효한 토큰이라면
            Authentication authentication = tokenProvider.getAuthentication(jwt); // 인증정보(Authentication)을 꺼내서
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // SecurityContext에 저장한다. 허가된 uri이외의 모든 요청은 이 필터를 거치게되며, 토큰 정보가 없으면 수행X
            // 대문이라고 생각하면됨.
        }

        filterChain.doFilter(request, response);
    }

}
