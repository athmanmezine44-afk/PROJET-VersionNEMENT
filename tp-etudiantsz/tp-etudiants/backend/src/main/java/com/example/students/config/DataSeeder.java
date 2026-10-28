package com.example.students.config;

import com.example.students.model.Student;
import com.example.students.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

/** Insère des étudiants de démonstration au premier démarrage (table vide). */
@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DataSeeder implements CommandLineRunner {

    private final StudentRepository repository;

    public DataSeeder(StudentRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }
        repository.saveAll(List.of(
                new Student("Alaoui", "Yassine", "yassine.alaoui@etu.ma", "Génie Informatique", 21, 15.5),
                new Student("Benali", "Sara", "sara.benali@etu.ma", "Data Science", 20, 16.8),
                new Student("El Idrissi", "Omar", "omar.elidrissi@etu.ma", "Cybersécurité", 22, 14.2),
                new Student("Mansouri", "Lina", "lina.mansouri@etu.ma", "Génie Informatique", 21, 17.1),
                new Student("Tazi", "Mehdi", "mehdi.tazi@etu.ma", "Réseaux et Télécoms", 23, 13.4),
                new Student("Chraibi", "Imane", "imane.chraibi@etu.ma", "Data Science", 20, 18.0)
        ));
    }
}
