package com.mastercard.labs.bps.discovery.filter;

import com.mastercard.labs.bps.discovery.util.HttpServletRequestCopier;
import com.mastercard.labs.bps.discovery.util.HttpServletResponseCopier;
import com.mastercard.labs.bps.discovery.exceptions.ResourceNotFoundException;
import com.mastercard.labs.bps.discovery.exceptions.SignatureVerificationException;
import com.mastercard.labs.bps.discovery.security.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class RestAuthenticationProcessingFilter implements Filter {

    public final static String webHookURL = "/bps-discovery/api/v0.1/";

    @Value("${webhook.tolerance}")
    private Long tolerance;

    @Value("${webhook.headerName}")
    private String webhookHeaderName;

    //@Value("${webhook.secretKey}")
    private String apiKey;

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        if (!org.springframework.http.HttpStatus.valueOf(httpServletResponse.getStatus()).is2xxSuccessful()) {
            switch (HttpStatus.valueOf(httpServletResponse.getStatus())) {
                case UNAUTHORIZED:
                    httpServletResponse.sendRedirect("sendError?errorCode=TOKEN_INVALID");
                    break;
                default:
                    break;
            }
        } else {
            if ("POST".equalsIgnoreCase(((HttpServletRequest) servletRequest).getMethod()) ||
                    "PUT".equalsIgnoreCase(((HttpServletRequest) servletRequest).getMethod())) {

                HttpServletRequestCopier requestCopier = new HttpServletRequestCopier((HttpServletRequest) servletRequest);
                HttpServletResponseCopier responseCopier = new HttpServletResponseCopier(httpServletResponse);
                try {
                    if (((HttpServletRequest) servletRequest).getRequestURI().contains(webHookURL)) {
                        if (requestCopier.getHeader(webhookHeaderName) != null) {
                            if (!Webhook.Signature.verifyHeader(requestCopier.getCachedBytes().toString(), requestCopier.getHeader(webhookHeaderName), apiKey, tolerance)) {
                                httpServletResponse.sendRedirect("sendError?errorCode=WEBHOOK_SIGNATURE_ERROR");
                            }
                        } else {
                            httpServletResponse.sendRedirect("sendError?errorCode=WEBHOOK_SIGNATURE_NOT_FOUND");
                        }
                    }
                } catch (SignatureVerificationException e) {
                    httpServletResponse.sendRedirect("sendError?errorCode=WEBHOOK_SIGNATURE_ERROR");
                }
                filterChain.doFilter(requestCopier, responseCopier);
                responseCopier.flushBuffer();
            } else {
                filterChain.doFilter(servletRequest, servletResponse);
            }
        }
    }


    @RequestMapping("/sendError")
    public String processError(HttpServletRequest request) {
        throw new ResourceNotFoundException(request.getParameter("errorCode") != null ?
                request.getParameter("errorCode") : "AUTHENTICATION_FAILED");
    }

}