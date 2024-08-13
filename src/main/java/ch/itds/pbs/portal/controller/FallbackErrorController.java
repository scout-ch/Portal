package ch.itds.pbs.portal.controller;

import ch.itds.pbs.portal.dto.error.ErrorResponse;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.MessageSource;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.util.NestedServletException;

import java.io.IOException;
import java.util.Locale;

@Slf4j
@Controller
public class FallbackErrorController implements ErrorController {

    public static final String SKIP_ERROR_VIEW = "X-SKIP-ERROR-VIEW";
    private final MessageSource messageSource;

    public FallbackErrorController(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    // workaround because the RestExceptionHandler does not work for filter exceptions
    @RequestMapping(value = "/error", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @SuppressWarnings("PMD.NPathComplexity")
    public ResponseEntity<ErrorResponse> handleErrorRest(HttpServletRequest request) {

        Object statusObj = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object messageObj = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object exceptionObj = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        Object dispatcherExceptionObj = request.getAttribute(DispatcherServlet.EXCEPTION_ATTRIBUTE);

        if (exceptionObj == null && dispatcherExceptionObj != null) {
            exceptionObj = dispatcherExceptionObj;
        }

        if (exceptionObj instanceof NestedServletException nse) {
            if (nse.getCause() != null) {
                exceptionObj = nse.getCause();
            }
        }

        HttpStatus httpStatus = (statusObj instanceof Integer) ? HttpStatus.valueOf((Integer) statusObj) : HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "unknown error";
        if (!httpStatus.is5xxServerError() && messageObj instanceof String && StringUtils.hasText((String) messageObj)) {
            message = (String) messageObj;
        } else if (exceptionObj instanceof AccessDeniedException accessDeniedException) {
            httpStatus = HttpStatus.FORBIDDEN;
            message = accessDeniedException.getMessage();
        } else if (exceptionObj instanceof Exception exception1 && (!(messageObj instanceof String) || !StringUtils.hasText((String) messageObj))) {
            message = exception1.getMessage();
        }

        ErrorResponse errorResponse = ErrorResponse.builder().code(httpStatus.value()).message(message).build();

        return ResponseEntity.status(errorResponse.getCode()).body(errorResponse);

    }

    @RequestMapping("/error")
    @SuppressWarnings("PMD.NPathComplexity")
    public ModelAndView handleError(HttpServletRequest request, HttpServletResponse response, Locale locale, Model model) {

        model.addAttribute("error", messageSource.getMessage("error.general.title", null, locale));
        model.addAttribute("message", messageSource.getMessage("error.general.message", null, locale));

        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        Object requestUri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        Object dispatcherException = request.getAttribute(DispatcherServlet.EXCEPTION_ATTRIBUTE);

        if (exception == null && dispatcherException != null) {
            exception = dispatcherException;
        }

        if (exception instanceof NestedServletException nse) {
            if (nse.getCause() != null) {
                exception = nse.getCause();
            }
        }

        if (exception instanceof Throwable && !(exception instanceof IOException) && !(exception instanceof NoResourceFoundException) && log.isErrorEnabled()) {
            //noinspection LoggingPlaceholderCountMatchesArgumentCount
            log.error("an exception occurred for {} or {}: {}", request.getRequestURL(), requestUri, ((Throwable) exception).getMessage(), exception);
        }

        if (status == null) {
            model.addAttribute("status", -1);
        } else {
            Integer statusCode = Integer.valueOf(status.toString());
            model.addAttribute("status", statusCode);
        }
        boolean messageSet = false;
        if (message instanceof String && !((String) message).trim().isEmpty()) {
            model.addAttribute("exceptionMessage", message);
            messageSet = true;
        }
        if (exception instanceof Exception) {
            model.addAttribute("exception", exception);
            model.addAttribute("exceptionClass", ((Exception) exception).getClass().getSimpleName());
            if (!messageSet) {
                model.addAttribute("exceptionMessage", ((Exception) exception).getMessage());
            }
        }
        if (requestUri instanceof String) {
            model.addAttribute("requestUri", requestUri);
        }

        if (response.containsHeader(HttpHeaders.CONTENT_DISPOSITION)) {
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline().build().toString());
        }

        if (request.getAttribute(SKIP_ERROR_VIEW) == null) {
            return new ModelAndView("error", model.asMap());
        } else {
            return null;
        }

    }
}
