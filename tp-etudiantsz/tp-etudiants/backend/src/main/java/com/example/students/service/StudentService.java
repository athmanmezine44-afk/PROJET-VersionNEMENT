package com.example.students.service;

import com.example.students.dto.StudentRequest;
import com.example.students.dto.StudentResponse;
import com.example.students.dto.StudentStats;
import com.example.students.dto.StudentStats.FiliereStat;
import com.example.students.exception.EmailAlreadyUsedException;
import com.example.students.exception.StudentNotFoundException;
import com.example.students.model.Student;
import com.example.students.repository.StudentRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

/** Logique métier : le contrôleur ne parle jamais directement au repository. */
@Service
@Transactional(readOnly = true)
public class StudentService {

    /** Champs autorisés pour le tri (évite d'exposer n'importe quelle propriété). */
    private static final Set<String> SORTABLE = Set.of("id", "nom", "prenom", "email", "filiere", "age", "moyenne");
    private static final double PASSING_GRADE = 10.0;

    private final StudentRepository repository;

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    public List<StudentResponse> search(String q, String filiere, String sortBy, String order) {
        String field = sortBy != null && SORTABLE.contains(sortBy) ? sortBy : "id";
        Sort.Direction direction = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, field);
        if (!field.equals("id")) {
            sort = sort.and(Sort.by(Sort.Direction.ASC, "id")); // ordre stable en cas d'égalité
        }
        return repository.search(clean(q), clean(filiere), sort).stream()
                .map(StudentResponse::from)
                .toList();
    }

    public StudentResponse findById(Long id) {
        return StudentResponse.from(getOrThrow(id));
    }

    public List<String> filieres() {
        return repository.findDistinctFilieres();
    }

    @Transactional
    public StudentResponse create(StudentRequest request) {
        String email = request.email().trim();
        if (repository.existsByEmailIgnoreCase(email)) {
            throw new EmailAlreadyUsedException(email);
        }
        Student student = new Student(request.nom().trim(), request.prenom().trim(), email,
                request.filiere().trim(), request.age(), request.moyenne());
        return StudentResponse.from(repository.save(student));
    }

    @Transactional
    public StudentResponse update(Long id, StudentRequest request) {
        Student student = getOrThrow(id);
        String email = request.email().trim();
        if (repository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new EmailAlreadyUsedException(email);
        }
        student.setNom(request.nom().trim());
        student.setPrenom(request.prenom().trim());
        student.setEmail(email);
        student.setFiliere(request.filiere().trim());
        student.setAge(request.age());
        student.setMoyenne(request.moyenne());
        return StudentResponse.from(repository.save(student));
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new StudentNotFoundException(id);
        }
        repository.deleteById(id);
    }

    public StudentStats stats() {
        List<Student> all = repository.findAll();
        if (all.isEmpty()) {
            return new StudentStats(0, 0, 0, 0, List.of());
        }
        double average = all.stream().mapToDouble(Student::getMoyenne).average().orElse(0);
        double best = all.stream().mapToDouble(Student::getMoyenne).max().orElse(0);
        long passing = all.stream().filter(s -> s.getMoyenne() >= PASSING_GRADE).count();

        Map<String, List<Student>> byFiliere = all.stream()
                .collect(Collectors.groupingBy(Student::getFiliere, TreeMap::new, Collectors.toList()));
        List<FiliereStat> perFiliere = byFiliere.entrySet().stream()
                .map(e -> new FiliereStat(e.getKey(), e.getValue().size(),
                        round(e.getValue().stream().mapToDouble(Student::getMoyenne).average().orElse(0))))
                .toList();

        return new StudentStats(all.size(), round(average), round(best),
                round(100.0 * passing / all.size()), perFiliere);
    }

    private Student getOrThrow(Long id) {
        return repository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
    }

    private static String clean(String value) {
        return value == null ? "" : value.trim();
    }

    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
