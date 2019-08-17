package com.template.webserver

import org.springframework.boot.Banner
import org.springframework.boot.SpringApplication
import org.springframework.boot.WebApplicationType.SERVLET
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * Our Spring Boot application.
 */
@SpringBootApplication
private open class Starter

/**
 * Starts our Spring Boot application.
 */
fun main(args: Array<String>) {
    val app = SpringApplication(Starter::class.java)
    app.setBannerMode(Banner.Mode.OFF)
    app.webApplicationType = SERVLET
    app.run(*args)


}

//@Bean
//fun corsConfigurer(): WebMvcConfigurer {
//    return object : WebMvcConfigurer {
//        override fun addCorsMappings(registry: CorsRegistry) {
//            registry.addMapping("/**").allowedOrigins("http://localhost:3000").allowedHeaders("*").allowedMethods("*")
//        }
//    }
//}