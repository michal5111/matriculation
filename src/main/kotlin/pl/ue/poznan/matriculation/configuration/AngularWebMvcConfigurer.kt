package pl.ue.poznan.matriculation.configuration

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.http.converter.HttpMessageConverter
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

    override fun extendMessageConverters(converters: List<HttpMessageConverter<*>>) {
        for (converter in converters) {
            if (converter is org.springframework.http.converter.json.MappingJackson2HttpMessageConverter) {
                val mapper = converter.objectMapper
                mapper.registerModule(Hibernate5Module())
            }
        }
    }
}