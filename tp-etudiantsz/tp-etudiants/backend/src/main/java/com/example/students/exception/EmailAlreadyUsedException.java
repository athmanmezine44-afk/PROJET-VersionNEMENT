package com.example.students.exception;

public class EmailAlreadyUsedException extends RuntimeException {
    public EmailAlreadyUsedException(String email) {
        super("Cet email est déjà utilisé : " + email);
    }
}
