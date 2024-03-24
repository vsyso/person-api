package ua.dp.syso.person.config

import org.springframework.context.annotation.Configuration
import org.springframework.core.MethodParameter
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.data.web.SortArgumentResolver
import org.springframework.data.web.SortHandlerMethodArgumentResolver
import org.springframework.data.web.config.EnableSpringDataWebSupport
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
class WebConfiguration(private val sortArgumentResolver: SortArgumentResolver): WebMvcConfigurer {

    /**
     * Argument resolver to replace getters with entity fields
     */
    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>){

        val pageableResolver = PageableHandlerMethodArgumentResolver(object: SortHandlerMethodArgumentResolver() {
            override fun supportsParameter(parameter: MethodParameter): Boolean {
                return sortArgumentResolver.supportsParameter(parameter)
            }
            override fun resolveArgument(
                parameter: MethodParameter,
                mavContainer: ModelAndViewContainer?,
                webRequest: NativeWebRequest,
                binderFactory: WebDataBinderFactory?
            ): Sort {
                val originalSort = sortArgumentResolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory)
                if (originalSort.isEmpty) {
                    return originalSort
                }
                var updatedSort = Sort.unsorted()
                for (order in originalSort) {
                    updatedSort = if (order.property == "age") {
                        updatedSort.and(Sort.by(
                            if (order.direction.isAscending) Sort.Direction.DESC else Sort.Direction.ASC,
                           "dateOfBirth"))
                    } else {
                        updatedSort.and(Sort.by(order.direction, order.property))
                    }
                }
                return updatedSort
            }
        })
        resolvers.add(pageableResolver)
    }
}
