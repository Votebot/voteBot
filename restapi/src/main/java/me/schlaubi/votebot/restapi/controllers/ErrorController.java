package me.schlaubi.votebot.restapi.controllers;

import me.schlaubi.votebot.restapi.entities.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ErrorController {

    @ExceptionHandler(Exception.class)
    public ApiError handleError(HttpServletRequest servlet, Exception exception) {
        HttpStatus status = HttpStatus.resolve(getStatusCode(servlet));
        String debugMessage = exception.getMessage() == null ? "No debug message avalible" : exception.getMessage();
        assert status != null;
        return new ApiError(status, status.getReasonPhrase(), debugMessage);
    }

    private int getStatusCode(HttpServletRequest servlet) {
        return (int) servlet.getAttribute("javax.servlet.error.status_code");
    }
}
