package org.hyperledger.justitia.scheduler.controller.filters;

import org.hyperledger.justitia.common.RequestContext;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class RequestContextFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        fillRequestContext((HttpServletRequest) request);

        try {
            chain.doFilter(request, response);
        } finally {
            // Due to tomcat thread reuse, it must be emptied
            clearRequestContext();
        }
    }

    private void clearRequestContext() {
        RequestContext.clearAllInfo();
    }

    private void fillRequestContext(HttpServletRequest request) {
        // User info
        String userId = getUserInfo(request);
        if (userId != null) {
            RequestContext.setUser(userId);
        }

        // Local info
        String locale = getLocaleInfo(request);
        if (locale != null) {
            RequestContext.setLocale(locale);
        }
    }

    private String getLocaleInfo(HttpServletRequest request) {
        return null;
    }

    private String getUserInfo(HttpServletRequest request) {
      return null;
    }

    @Override
    public void destroy() {

    }
}
