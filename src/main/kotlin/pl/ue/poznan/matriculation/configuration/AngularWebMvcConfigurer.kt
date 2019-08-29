package pl.ue.poznan.matriculation.configuration

import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.resource.PathResourceResolver



@Configuration
class AngularWebMvcConfigurer: WebMvcConfigurer {

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/**/*")
                .addResourceLocations("classpath:/resources/")
                .resourceChain(true)
                .addResolver(object : PathResourceResolver() {
                    override fun getResource(resourcePath: String, location: Resource): Resource {
                        val requestedResource = location.createRelative(resourcePath)
                        return if (requestedResource.exists() && requestedResource.isReadable) requestedResource
                        else ClassPathResource("/resources/index.html")
                    }
                })
    }
}