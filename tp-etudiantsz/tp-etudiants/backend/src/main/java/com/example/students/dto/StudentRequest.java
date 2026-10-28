package com.example.students.dto;

import jakarta.validation.constraints.*;

/** Corps des requêtes POST et PUT. Les règles de validation sont déclarées ici. */
public record StudentRequest(
        @NotBlank(message = "Le nom est obligatoire")
        @Size(max = 80, message = "Le nom ne doit pas dépasser 80 caractères")
        String nom,

        @NotBlank(message = "Le prénom est obligatoire")
        @Size(max = 80, message = "Le prénom ne doit pas dépasser 80 caractères")
        String prenom,

        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "L'email n'est pas valide")
        @Size(max = 120, message = "L'email ne doit pas dépasser 120 caractères")
        String email,

        @NotBlank(message = "La filière est obligatoire")
        @Size(max = 100, message = "La filière ne doit pas dépasser 100 caractères")
        String filiere,

        @NotNull(message = "L'âge est obligatoire")
        @Min(value = 16, message = "L'âge minimum est 16 ans")
        @Max(value = 100, message = "L'âge maximum est 100 ans")
        Integer age,

        @NotNull(message = "La moyenne est obligatoire")
        @DecimalMin(value = "0.0", message = "La moyenne doit être comprise entre 0 et 20")
        @DecimalMax(value = "20.0", message = "La moyenne doit être comprise entre 0 et 20")
        Double moyenne
) {
}
