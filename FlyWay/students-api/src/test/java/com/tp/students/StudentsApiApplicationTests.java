package com.tp.students;

import com.tp.students.model.Student;
import com.tp.students.repository.StudentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class StudentsApiApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("Le contexte Spring Boot démarre et Flyway applique la migration avec les 5 étudiants initiaux")
    void contextLoadsAndFlywaySeededInitialData() {
        List<Student> students = studentRepository.findAll();
        assertThat(students).hasSizeGreaterThanOrEqualTo(5);
        assertThat(students.stream().map(Student::getFirstName))
                .contains("Alice", "Bob", "Camille", "David", "Emma");
    }

    @Test
    @DisplayName("Flyway applique V1 et V2 et conserve les étudiants après l'évolution du schéma")
    void flywayAppliesAllMigrations() {
        Integer migrationCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM \"flyway_schema_history\" "
                        + "WHERE \"success\" = TRUE AND \"version\" IS NOT NULL", Integer.class);
        Integer createdAtColumnCount = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM INFORMATION_SCHEMA.COLUMNS
                WHERE TABLE_NAME = 'STUDENT' AND COLUMN_NAME = 'CREATED_AT'
                """,
                Integer.class);

        assertThat(migrationCount).isEqualTo(2);
        assertThat(createdAtColumnCount).isEqualTo(1);
        assertThat(jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM STUDENT WHERE CREATED_AT IS NOT NULL", Integer.class))
                .isGreaterThanOrEqualTo(5);
    }

    @Test
    @DisplayName("GET /api/students retourne la liste des étudiants en JSON")
    void shouldReturnAllStudents() throws Exception {
        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(5)));
    }

    @Test
    @DisplayName("POST /api/students crée un nouvel étudiant avec persistance")
    void shouldCreateNewStudent() throws Exception {
        String newStudentJson = """
            {
                "firstName": "Youssef",
                "lastName": "Alaoui",
                "email": "youssef.alaoui@etud.fr",
                "age": 22,
                "filiere": "Informatique"
            }
            """;

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newStudentJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.firstName").value("Youssef"));
    }

    @Test
    @DisplayName("PUT /api/students/{id} met à jour un étudiant existant")
    void shouldUpdateStudent() throws Exception {
        Student toUpdate = studentRepository.save(new Student("BeforeUpdate", "User", "before@etud.fr", 22, "Informatique"));

        String updateJson = """
            {
                "firstName": "AfterUpdate",
                "lastName": "User",
                "email": "after@etud.fr",
                "age": 23,
                "filiere": "Informatique"
            }
            """;

        mockMvc.perform(put("/api/students/" + toUpdate.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("AfterUpdate"))
                .andExpect(jsonPath("$.email").value("after@etud.fr"));
    }

    @Test
    @DisplayName("DELETE /api/students/{id} supprime un étudiant")
    void shouldDeleteStudent() throws Exception {
        // Crée un étudiant temporaire pour la suppression
        Student temp = studentRepository.save(new Student("ToDelete", "User", "del@test.fr", 20, "Physique"));

        mockMvc.perform(delete("/api/students/" + temp.getId()))
                .andExpect(status().isNoContent());

        assertThat(studentRepository.findById(temp.getId())).isEmpty();
    }
}
