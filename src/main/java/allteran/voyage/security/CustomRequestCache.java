//package allteran.voyage.security;//package allteran.voyage.security;
//
//import com.vaadin.flow.server.HandlerHelper;
//import com.vaadin.flow.shared.ApplicationConstants;
//import org.springframework.security.authentication.AnonymousAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.stream.Stream;
//
//public class SecurityUtils {
//    private SecurityUtils() {
//        // Util methods only
//    }
//
//    static boolean isFrameworkInternalRequest(HttpServletRequest request) {
//        final String parameterValue = request.getParameter(ApplicationConstants.REQUEST_TYPE_PARAMETER);
//        return parameterValue != null
//                && Stream.of(HandlerHelper.RequestType.values())
//                .anyMatch(r -> r.getIdentifier().equals(parameterValue));
//    }
//
//    static boolean isUserLoggedIn() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        return authentication != null
//                && !(authentication instanceof AnonymousAuthenticationToken)
//                && authentication.isAuthenticated();
//    }
//}

//
//import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//public class CustomRequestCache extends HttpSessionRequestCache {
//
//    @Override
//    public void saveRequest(HttpServletRequest request, HttpServletResponse response) {
//        if (!SecurityUtils.isFrameworkInternalRequest(request)) {
//            super.saveRequest(request, response);
//        }
//    }
//}