package com.example.students.controller;

import com.example.students.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:teststudents;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "app.seed.enabled=false"
})
class StudentControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    StudentRepository repository;

    @BeforeEach
    void cleanDatabase() {
        repository.deleteAll();
    }

    private static String json(String nom, String email, String filiere, int age, double moyenne) {
        return """
                {"nom":"%s","prenom":"Test","email":"%s","filiere":"%s","age":%d,"moyenne":%s}
                """.formatted(nom, email, filiere, age, moyenne);
    }

    private void create(String nom, String email, String filiere, int age, double moyenne) throws Exception {
        mvc.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON)
                        .content(json(nom, email, filiere, age, moyenne)))
                .andExpect(status().isCreated());
    }

    @Test
    void createReturns201WithLocationAndId() throws Exception {
        mvc.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON)
                        .content(json("Alaoui", "a@etu.ma", "Informatique", 21, 15.5)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nom").value("Alaoui"));
    }

    @Test
    void createWithInvalidDataReturns400WithFieldErrors() throws Exception {
        mvc.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON)
                        .content(json("", "pas-un-email", "Info", 10, 25.0)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.nom").exists())
                .andExpect(jsonPath("$.fieldErrors.email").exists())
                .andExpect(jsonPath("$.fieldErrors.age").exists())
                .andExpect(jsonPath("$.fieldErrors.moyenne").exists());
    }

    @Test
    void duplicateEmailReturns409() throws Exception {
        create("Alaoui", "dup@etu.ma", "Informatique", 21, 12);
        mvc.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON)
                        .content(json("Autre", "DUP@etu.ma", "Informatique", 22, 13)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.fieldErrors.email").exists());
    }

    @Test
    void unknownIdReturns404() throws Exception {
        mvc.perform(get("/api/students/999999")).andExpect(status().isNotFound());
        mvc.perform(delete("/api/students/999999")).andExpect(status().isNotFound());
    }

    @Test
    void updateModifiesStudent() throws Exception {
        create("Alaoui", "u@etu.ma", "Informatique", 21, 12);
        Long id = repository.findAll().get(0).getId();

        mvc.perform(put("/api/students/" + id).contentType(MediaType.APPLICATION_JSON)
                        .content(json("Nouveau", "u@etu.ma", "Data Science", 22, 17.5)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Nouveau"))
                .andExpect(jsonPath("$.filiere").value("Data Science"))
                .andExpect(jsonPath("$.moyenne").value(17.5));
    }

    @Test
    void deleteRemovesStudent() throws Exception {
        create("Alaoui", "d@etu.ma", "Informatique", 21, 12);
        Long id = repository.findAll().get(0).getId();

        mvc.perform(delete("/api/students/" + id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/students/" + id)).andExpect(status().isNotFound());
    }

    @Test
    void searchFilterAndSort() throws Exception {
        create("Benali", "b@etu.ma", "Data Science", 20, 16);
        create("Tazi", "t@etu.ma", "Réseaux", 23, 11);
        create("Chraibi", "c@etu.ma", "Data Science", 20, 18);

        mvc.perform(get("/api/students").param("q", "BEN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nom").value("Benali"));

        mvc.perform(get("/api/students").param("filiere", "Data Science")
                        .param("sortBy", "moyenne").param("order", "desc"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nom").value("Chraibi"));
    }

    @Test
    void statsAreComputed() throws Exception {
        create("A", "a1@etu.ma", "Data Science", 20, 16);
        create("B", "b1@etu.ma", "Réseaux", 21, 8);

        mvc.perform(get("/api/students/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(2))
                .andExpect(jsonPath("$.moyenneGenerale").value(12.0))
                .andExpect(jsonPath("$.meilleureMoyenne").value(16.0))
                .andExpect(jsonPath("$.tauxReussite").value(50.0))
                .andExpect(jsonPath("$.parFiliere", hasSize(2)));
    }
}
