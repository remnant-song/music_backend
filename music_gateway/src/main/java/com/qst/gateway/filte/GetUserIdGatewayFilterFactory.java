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
public class GetUserIdGatewayFilterFactory extends AbstractGatewayFilterFactory<GetUserIdGatewayFilterFactory.Config> {
    public GetUserIdGatewayFilterFactory() {
        super(Config.class);
    }
    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("isRequired");
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain)->{
            ServerHttpResponse response = exchange.getResponse();
            ServerHttpRequest request = exchange.getRequest();
            HttpHeaders headers = request.getHeaders();
            String authorization = headers.getFirst("Authorization");
            boolean b=authorization!=null&&!authorization.equals("");
            if(request.getMethod().equals(HttpMethod.OPTIONS)){
                return chain.filter(exchange);
            }
            else if(!b&&config.getIsRequired()==1){
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE);
                return response.setComplete();
            }
            else if(b){
                ServerHttpRequest build = request.mutate()
                        .header("id", String.valueOf(JwtUtils.getMemberIdByJwtToken(authorization)))
                        .header("role", String.valueOf(JwtUtils.getMemberRoleByJwtToken(authorization)))
                        .build();
                ServerWebExchange serverWebExchange = exchange.mutate().request(build).build();
                return chain.filter(serverWebExchange);
            }
            return chain.filter(exchange);
        };
    }

    public static class Config {
        private Integer isRequired=0;

        public Integer getIsRequired() {
            return isRequired;
        }

        public void setIsRequired(Integer isRequired) {
            this.isRequired = isRequired;
        }
    }
}