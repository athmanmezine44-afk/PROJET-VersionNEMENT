package com.tp.students.repository;

import com.tp.students.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository Spring Data JPA pour l'entité Student.
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
}
