package com.ipiecoles.java.java350.service;

import com.ipiecoles.java.java350.exception.EmployeException;
import com.ipiecoles.java.java350.model.Employe;
import com.ipiecoles.java.java350.model.Entreprise;
import com.ipiecoles.java.java350.model.NiveauEtude;
import com.ipiecoles.java.java350.model.Poste;
import com.ipiecoles.java.java350.repository.EmployeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.mockito.Mockito.times;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class EmployeServiceIntegrationTest {

    @Autowired
    EmployeService employeService;

    @Autowired
    private EmployeRepository employeRepository;

    @BeforeEach
    @AfterEach
    public void setup(){
        employeRepository.deleteAll();
    }

    @Test
    public void testIntegrationEmbaucheEmploye() throws EmployeException {
        //Given
        employeRepository.save(new Employe("Neymar", "Jean", "M01234", LocalDate.now(), Entreprise.SALAIRE_BASE, 2, 1.0));
        String nom = "Neymar";
        String prenom = "Jean";
        Poste poste = Poste.MANAGER;
        NiveauEtude niveauEtude = NiveauEtude.MASTER;
        Double tempsPartiel = 1.0;

        //When
        employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);

        //Then
        Employe employe = employeRepository.findByMatricule("M01234");
        Assertions.assertNotNull(employe);
        Assertions.assertEquals(nom, employe.getNom());
        Assertions.assertEquals(prenom, employe.getPrenom());
        Assertions.assertEquals(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")), employe.getDateEmbauche().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        Assertions.assertEquals("M01234", employe.getMatricule());
        Assertions.assertEquals(1.0, employe.getTempsPartiel().doubleValue());
        Assertions.assertEquals(1521.22, employe.getSalaire().doubleValue());
    }

    @Test
    void testIntegrationCalculPerformanceCommercialCas2() throws EmployeException {
        //Given
        Employe e = new Employe();
        e.setMatricule("C24355");
        e.setPerformance(4);
        employeRepository.save(e);
        Long objectifCa = 42000L;
        Long caTraite = 37800L;


        //When
        employeService.calculPerformanceCommercial(e.getMatricule(), caTraite, objectifCa);
        //embaucheEmploye return void donc on passe par un ArgumentCaptor

        //Then
        Employe employe = employeRepository.findByMatricule("C24355");
        Assertions.assertEquals(2, employe.getPerformance());
    }
}
