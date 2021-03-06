package com.example.MusiciansAPI.exception;

import com.example.MusiciansAPI.payload.request.response.APIResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;

@RestControllerAdvice
public class Advice {

    /**
     * Handle Autentication exceptions thrown by the app's filters (in this case JwtAuthenticationFilter)
     * @param exc
     * @param response
     * @return
     */
    @ExceptionHandler(value = AuthenticationException.class)
    public APIResponse<?> handleExpiredException(AuthenticationException exc, HttpServletResponse response) {
        APIResponse<?> apiResponse = new APIResponse<>();
        apiResponse.setError(exc.getMessage());
        return apiResponse;
    }
}
