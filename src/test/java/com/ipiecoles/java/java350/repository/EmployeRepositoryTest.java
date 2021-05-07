package com.ipiecoles.java.java350.repository;

import com.ipiecoles.java.java350.model.Employe;
import com.ipiecoles.java.java350.model.Entreprise;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class EmployeRepositoryTest {

    @Autowired
    private EmployeRepository employeRepository;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
        employeRepository.deleteAll();
    }

    @Test
    void findLastMatriculeBase() {
        //Given
        employeRepository.save(new Employe("Neymar", "Jean","M00123", LocalDate.now().minusYears(2), Entreprise.SALAIRE_BASE,1,1.0));
        employeRepository.save(new Employe("Bruel", "Patrick","T00123", LocalDate.now(), Entreprise.SALAIRE_BASE,2,0.5));
        employeRepository.save(new Employe("Dulac", "Jeanne","A00123", LocalDate.now().plusYears(2), Entreprise.SALAIRE_BASE,3,1.0));

        //When
        String matricule = employeRepository.findLastMatricule();

        //Then
        Assertions.assertEquals("00123", matricule);
    }

    @Test
    void findLastMatriculeOneEmploye() {
        //Given
        employeRepository.save(new Employe("Neymar", "Jean","M00123", LocalDate.now().minusYears(2), Entreprise.SALAIRE_BASE,1,1.0));

        //When
        String matricule = employeRepository.findLastMatricule();

        //Then
        Assertions.assertEquals("00123",matricule);
    }

    @Test
    void findLastMatriculeNull() {
        //Given
        //Nothing to test null

        //When
        String matricule = employeRepository.findLastMatricule();

        //Then
        Assertions.assertNull(matricule);
    }

    @Test
    void avgPerformanceWhereMatriculeStartsWithtestNoEmploye() {
        //Given
        //Nothing to test null

        //When
        Double avgPerf = employeRepository.avgPerformanceWhereMatriculeStartsWith("C");

        //Then
        Assertions.assertNull(avgPerf);
    }

    @Test
    void avgPerformanceWhereMatriculeStartsWithSameEmployeType() {
        //Given
        employeRepository.save(new Employe("Neymar", "Jean","M00123", LocalDate.now().minusYears(2), Entreprise.SALAIRE_BASE,1,1.0));
        employeRepository.save(new Employe("Bruel", "Patrick","M00123", LocalDate.now(), Entreprise.SALAIRE_BASE,2,0.5));
        employeRepository.save(new Employe("Dulac", "Jeanne","M00123", LocalDate.now().plusYears(2), Entreprise.SALAIRE_BASE,3,1.0));

        //When
        Double avgPerf = employeRepository.avgPerformanceWhereMatriculeStartsWith("M");

        //Then
        Assertions.assertEquals(2, avgPerf);
    }

@Test
    void avgPerformanceWhereMatriculeStartsWithDifferentEmployeType() {
        //Given
        employeRepository.save(new Employe("Neymar", "Jean","T00123", LocalDate.now().minusYears(2), Entreprise.SALAIRE_BASE,1,1.0));
        employeRepository.save(new Employe("Bruel", "Patrick","M00123", LocalDate.now(), Entreprise.SALAIRE_BASE,2,0.5));
        employeRepository.save(new Employe("Dulac", "Jeanne","M00123", LocalDate.now().plusYears(2), Entreprise.SALAIRE_BASE,3,1.0));

        //When
        Double avgPerf = employeRepository.avgPerformanceWhereMatriculeStartsWith("M");

        //Then
        Assertions.assertEquals(2.5, avgPerf);
    }

    @Test
    void avgPerformanceWhereMatriculeStartsWithOneEmploye() {
        //Given
        employeRepository.save(new Employe("Neymar", "Jean","T00123", LocalDate.now().minusYears(2), Entreprise.SALAIRE_BASE,1,1.0));

        //When
        Double avgPerf = employeRepository.avgPerformanceWhereMatriculeStartsWith("T");

        //Then
        Assertions.assertEquals(1, avgPerf);
    }

}