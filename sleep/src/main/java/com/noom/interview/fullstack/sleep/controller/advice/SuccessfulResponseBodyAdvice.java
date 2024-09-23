package com.noom.interview.fullstack.sleep.controller.advice;

import com.noom.interview.fullstack.sleep.model.dto.response.StandardResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * Class that handles successful results from the application and wrap them into a
 * standard response.
 */
@ControllerAdvice
public class SuccessfulResponseBodyAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(
            MethodParameter returnType,
            @NotNull Class<? extends HttpMessageConverter<?>> converterTyp
    ) {
        return (AnnotationUtils.findAnnotation(returnType.getContainingClass(), ResponseBody.class) != null ||
                returnType.getMethodAnnotation(ResponseBody.class) != null);
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            @NotNull MethodParameter returnType,
            @NotNull MediaType selectedContentType,
            @NotNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
            @NotNull ServerHttpRequest request, @NotNull ServerHttpResponse response
    ) {
        if (response instanceof ServletServerHttpResponse) {
            var status = ((ServletServerHttpResponse) response).getServletResponse().getStatus();
            if (HttpStatus.OK.equals(HttpStatus.valueOf(status)))
                return new StandardResponse<>(HttpStatus.OK, body);
            if (HttpStatus.CREATED.equals(HttpStatus.valueOf(status)))
                return new StandardResponse<>(HttpStatus.CREATED, body);
        }

        return body;
    }
}
