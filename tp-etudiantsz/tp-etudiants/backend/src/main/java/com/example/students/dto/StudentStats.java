package com.example.students.dto;

import java.util.List;

/** Statistiques globales affichées sur le tableau de bord. */
public record StudentStats(
        long total,
        double moyenneGenerale,
        double meilleureMoyenne,
        double tauxReussite,
        List<FiliereStat> parFiliere
) {
    public record FiliereStat(String filiere, long effectif, double moyenne) {
    }
}
