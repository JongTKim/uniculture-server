package com.capstone.uniculture.config;

import com.capstone.uniculture.jwt.JwtHandshakeInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSockConfig implements WebSocketMessageBrokerConfigurer {


  private final FilterChannelInterceptor filterChannelInterceptor;

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.enableSimpleBroker("/sub"); // 구독 url
    registry.setApplicationDestinationPrefixes("/pub"); // prefix 정의
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("ws")
            .setAllowedOriginPatterns("*");
            //.withSockJS()
            // WebSocket 연결 전 사용자의 로그인 정보에서 ID 값을 불러와 저장하는 인터셉터
            //.setInterceptors(new JwtHandshakeInterceptor());
  }


  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(filterChannelInterceptor);
  }

}
