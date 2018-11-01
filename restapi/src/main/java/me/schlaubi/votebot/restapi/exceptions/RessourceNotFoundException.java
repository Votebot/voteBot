package me.schlaubi.votebot.restapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Ressource not found")
public class RessourceNotFoundException extends RuntimeException {
}
