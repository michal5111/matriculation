package pl.ue.poznan.matriculation.security

import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter
import javax.servlet.http.HttpServletRequest

class APIKeyAuthFilter(
        private var principalRequestHeader: String?
): AbstractPreAuthenticatedProcessingFilter() {

    override fun getPreAuthenticatedCredentials(request: HttpServletRequest): String? {
        return request.getHeader(principalRequestHeader)
    }

    override fun getPreAuthenticatedPrincipal(p0: HttpServletRequest?): String {
        return "test"
    }

}