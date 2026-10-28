package com.example.students.controller;

import com.example.students.dto.StudentRequest;
import com.example.students.dto.StudentResponse;
import com.example.students.dto.StudentStats;
import com.example.students.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    /** GET /api/students?q=sara&filiere=Data Science&sortBy=moyenne&order=desc */
    @GetMapping
    public List<StudentResponse> list(
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "filiere", required = false) String filiere,
            @RequestParam(name = "sortBy", required = false) String sortBy,
            @RequestParam(name = "order", required = false) String order) {
        return service.search(q, filiere, sortBy, order);
    }

    @GetMapping("/stats")
    public StudentStats stats() {
        return service.stats();
    }

    @GetMapping("/filieres")
    public List<String> filieres() {
        return service.filieres();
    }

    @GetMapping("/{id}")
    public StudentResponse get(@PathVariable("id") Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<StudentResponse> create(@Valid @RequestBody StudentRequest request) {
        StudentResponse created = service.create(request);
        return ResponseEntity.created(URI.create("/api/students/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public StudentResponse update(@PathVariable("id") Long id, @Valid @RequestBody StudentRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
