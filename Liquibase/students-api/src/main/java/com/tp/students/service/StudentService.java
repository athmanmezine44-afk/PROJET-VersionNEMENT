package com.tp.students.service;

import com.tp.students.model.Student;
import com.tp.students.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service gérant les opérations CRUD sur les étudiants via Spring Data JPA.
 */
@Service
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    /**
     * Retourne tous les étudiants.
     */
    @Transactional(readOnly = true)
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    /**
     * Retourne un étudiant par son ID.
     */
    @Transactional(readOnly = true)
    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    /**
     * Ajoute un nouvel étudiant.
     */
    public Student addStudent(Student student) {
        // Garantit que l'ID n'est pas pré-défini pour déclencher l'auto-génération
        student.setId(null);
        return studentRepository.save(student);
    }

    /**
     * Met à jour un étudiant existant.
     */
    public Optional<Student> updateStudent(Long id, Student updated) {
        return studentRepository.findById(id)
                .map(existing -> {
                    existing.setFirstName(updated.getFirstName());
                    existing.setLastName(updated.getLastName());
                    existing.setEmail(updated.getEmail());
                    existing.setAge(updated.getAge());
                    existing.setFiliere(updated.getFiliere());
                    return studentRepository.save(existing);
                });
    }

    /**
     * Supprime un étudiant par ID.
     */
    public boolean deleteStudent(Long id) {
        if (studentRepository.existsById(id)) {
            studentRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
