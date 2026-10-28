package com.example.students.dto;

import com.example.students.model.Student;

/** Représentation JSON d'un étudiant renvoyée par l'API. */
public record StudentResponse(
        Long id,
        String nom,
        String prenom,
        String email,
        String filiere,
        int age,
        double moyenne
) {
    public static StudentResponse from(Student s) {
        return new StudentResponse(s.getId(), s.getNom(), s.getPrenom(), s.getEmail(),
                s.getFiliere(), s.getAge(), s.getMoyenne());
    }
}
