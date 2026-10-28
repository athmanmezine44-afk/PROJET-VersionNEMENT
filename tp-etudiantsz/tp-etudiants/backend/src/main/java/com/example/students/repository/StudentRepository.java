package com.example.students.repository;

import com.example.students.model.Student;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    @Query("SELECT DISTINCT s.filiere FROM Student s ORDER BY s.filiere")
    List<String> findDistinctFilieres();

    /**
     * Recherche multicritère. Une chaîne vide désactive le critère correspondant.
     * q : recherche (insensible à la casse) dans le nom, le prénom et l'email.
     */
    @Query("""
            SELECT s FROM Student s
            WHERE (:q = '' OR LOWER(s.nom) LIKE LOWER(CONCAT('%', :q, '%'))
                           OR LOWER(s.prenom) LIKE LOWER(CONCAT('%', :q, '%'))
                           OR LOWER(s.email) LIKE LOWER(CONCAT('%', :q, '%')))
              AND (:filiere = '' OR s.filiere = :filiere)
            """)
    List<Student> search(@Param("q") String q, @Param("filiere") String filiere, Sort sort);
}
