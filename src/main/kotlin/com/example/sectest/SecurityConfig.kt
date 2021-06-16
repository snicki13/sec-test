package com.example.sectest

import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.http.UserDetailsServiceFactoryBean
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.security.Principal

@EnableWebFluxSecurity
class SecurityConfig {

    private val users = mapOf(Pair("testToken", User("snicki", "Dominik")))

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity, authenticationManager: ReactiveAuthenticationManager): SecurityWebFilterChain =
        http
            .httpBasic().disable()
            .formLogin().disable()
            .csrf().disable()
            .logout().disable()
            .authorizeExchange().pathMatchers("/sec").authenticated()
            .and()
            .addFilterAt(authenticationWebFilter(authenticationManager), SecurityWebFiltersOrder.AUTHENTICATION)
            .build()

    @Bean
    fun authManager(userDetailsService: ReactiveUserDetailsService)
        = ReactiveAuthenticationManager { auth ->
        val user: User = users[auth.credentials] ?: throw UsernameNotFoundException("Token not found!")
            PreAuthenticatedAuthenticationToken(user, auth.credentials, user.authorities)
                .toMono()
    }

    @Bean
    fun userDetailsService(): ReactiveUserDetailsService {
        return ReactiveUserDetailsService {
            users[it].toMono()
        }
    }

    private fun authenticationWebFilter(reactiveAuthenticationManager: ReactiveAuthenticationManager) =
        AuthenticationWebFilter(reactiveAuthenticationManager).apply {
            setServerAuthenticationConverter(::filterConverter)
        }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    fun filterConverter(exchange: ServerWebExchange): Mono<Authentication> {
        val token: String = exchange.request.queryParams.getFirst("sessionToken") ?: throw UsernameNotFoundException("No token given!")
        return Mono.just(UsernamePasswordAuthenticationToken(null, token))
    }
}
