package com.example.students.model;

import jakarta.persistence.*;

/** Entité JPA : une ligne de la table « students ». */
@Entity
@Table(name = "students",
        uniqueConstraints = @UniqueConstraint(name = "uk_students_email", columnNames = "email"))
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String nom;

    @Column(nullable = false, length = 80)
    private String prenom;

    @Column(nullable = false, length = 120)
    private String email;

    @Column(nullable = false, length = 100)
    private String filiere;

    @Column(nullable = false)
    private int age;

    @Column(nullable = false)
    private double moyenne;

    protected Student() {
        // requis par JPA
    }

    public Student(String nom, String prenom, String email, String filiere, int age, double moyenne) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.filiere = filiere;
        this.age = age;
        this.moyenne = moyenne;
    }

    public Long getId() { return id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFiliere() { return filiere; }
    public void setFiliere(String filiere) { this.filiere = filiere; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public double getMoyenne() { return moyenne; }
    public void setMoyenne(double moyenne) { this.moyenne = moyenne; }
}
