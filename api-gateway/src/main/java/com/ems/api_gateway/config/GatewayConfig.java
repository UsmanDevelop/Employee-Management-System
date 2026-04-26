package com.ems.api_gateway.config;

import org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;


@Configuration
public class GatewayConfig {

    @Bean
    public RouterFunction<ServerResponse> employeeServiceRoute() {
        return GatewayRouterFunctions.route("employee-service")
                .route(RequestPredicates.path("/api/employees/**"),
                        HandlerFunctions.http())
                .filter(LoadBalancerFilterFunctions.lb("employee-service"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> departmentServiceRoute() {
        return GatewayRouterFunctions.route("department-service")
                .route(RequestPredicates.path("/api/departments/**"),
                        HandlerFunctions.http())
                .filter(LoadBalancerFilterFunctions.lb("department-service"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> authRoute() {
        return GatewayRouterFunctions.route("auth-service")
                .route(RequestPredicates.path("/api/auth/**"),
                        HandlerFunctions.http())
                .filter(LoadBalancerFilterFunctions.lb("employee-service"))
                .build();
    }


}
