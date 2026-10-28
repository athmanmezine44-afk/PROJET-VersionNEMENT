package com.example.students.exception;

public class StudentNotFoundException extends RuntimeException {
    public StudentNotFoundException(Long id) {
        super("Aucun étudiant avec l'identifiant " + id);
    }
}
