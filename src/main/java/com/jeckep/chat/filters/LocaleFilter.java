package com.jeckep.chat.filters;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class LocaleFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String locale = request.getParameter("locale");
        if(locale != null){
            ((HttpServletRequest)request).getSession().setAttribute("locale", locale);
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
