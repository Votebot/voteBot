package me.schlaubi.votebot.restapi.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@SuppressWarnings("unused")
public class ApiError {

    private final HttpStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    @JsonProperty("timestamp")
    private final LocalDateTime timeStamp;
    private final String message;
    @JsonProperty("debugmessage")
    private final String debugMessage;

    public ApiError(HttpStatus status, String message, String debugMessage) {
        this.status = status;
        this.message = message;
        this.timeStamp = LocalDateTime.now();
        this.debugMessage = debugMessage;
    }
}
