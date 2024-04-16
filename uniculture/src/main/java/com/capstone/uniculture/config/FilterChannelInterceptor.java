package com.capstone.uniculture.config;

import com.capstone.uniculture.jwt.TokenProvider;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;

@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@RequiredArgsConstructor
@Configuration
public class FilterChannelInterceptor implements ChannelInterceptor {

    private final TokenProvider tokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        assert headerAccessor != null;


        if (headerAccessor.getCommand() == StompCommand.CONNECT) { // 연결 시에한 header 확인
            String token = String.valueOf(headerAccessor.getNativeHeader("Authorization").get(0));

            if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) { // 유효한 토큰이라면
                Authentication authentication = tokenProvider.getAuthentication(token); // 인증정보(Authentication)을 꺼내서
                long userId = Long.parseLong(authentication.getName());
                headerAccessor.addNativeHeader("User", String.valueOf(userId));
            }
        }
        return message;
    }
}
