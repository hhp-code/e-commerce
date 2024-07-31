package com.ecommerce.api.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class LoggingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestId = UUID.randomUUID().toString();
        long startTime = System.currentTimeMillis();

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(httpRequest);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(httpResponse);
        logRequest(requestWrapper, requestId);

        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            logResponse(responseWrapper, requestId, System.currentTimeMillis() - startTime);
            responseWrapper.copyBodyToResponse();
        }
    }
    private void logRequest(ContentCachingRequestWrapper request, String requestId) {
        log.info("[{}] Request: {} {} (Client IP: {})",
                requestId,
                request.getMethod(),
                request.getRequestURI(),
                request.getRemoteAddr());
        log.debug("[{}] Headers: {}", requestId, request.getHeaderNames());
        log.trace("[{}] Request Body: {}", requestId, new String(request.getContentAsByteArray()));
    }

    private void logResponse(ContentCachingResponseWrapper response, String requestId, long duration) {
        log.info("[{}] Response: {} (Duration: {} ms)",
                requestId,
                response.getStatus(),
                duration);
        log.debug("[{}] Response Headers: {}", requestId, response.getHeaderNames());
        log.trace("[{}] Response Body: {}", requestId, new String(response.getContentAsByteArray()));
    }
}

