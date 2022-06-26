package pl.poznan.ue.matriculation.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.resource.PathResourceResolver


@Configuration
class AngularWebMvcConfigurer : WebMvcConfigurer {

    @Value("\${pl.poznan.ue.matriculation.service.home}")
    private lateinit var appServiceHome: String

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/**")
            .addResourceLocations("classpath:/static/")
            .resourceChain(true)
            .addResolver(object : PathResourceResolver() {
                override fun getResource(resourcePath: String, location: Resource): Resource {
                    val requestedResource = location.createRelative(resourcePath)
                    return when {
                        requestedResource.exists() && requestedResource.isReadable -> requestedResource
                        resourcePath.startsWith("api") -> throw ResponseStatusException(HttpStatus.NOT_FOUND)
                        else -> ClassPathResource("static/index.html")
                    }
                }
            })
        registry.addResourceHandler("/files/**")
            .addResourceLocations("classpath:/files/")
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins(appServiceHome)
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
    }
}
