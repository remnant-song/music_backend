package com.qst.gateway.filte;

import com.qst.domain.util.JwtUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.util.Arrays;
import java.util.List;

@Component
public class AuthorizeGatewayFilterFactory extends AbstractGatewayFilterFactory<AuthorizeGatewayFilterFactory.Config> {

    public AuthorizeGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("role");
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();
            if(request.getMethod().equals(HttpMethod.OPTIONS)){
                return chain.filter(exchange);
            }
            Integer role = config.getRole();
            HttpHeaders headers = request.getHeaders();
            String token = request.getHeaders().getFirst("Authorization");
            Integer role1 = JwtUtils.getMemberRoleByJwtToken(token);
            if(role==role1){
                ServerHttpRequest build = request.mutate().header("id", String.valueOf(JwtUtils.getMemberIdByJwtToken(token))).build();
                ServerWebExchange webExchange = exchange.mutate().request(build).build();
                return chain.filter(webExchange);
            }else {
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE);
                return response.setComplete();
            }

        };
    }

    public static class Config {
        private Integer role;

        public Integer getRole() {
            return role;
        }

        public void setRole(Integer role) {
            this.role = role;
        }
    }
}