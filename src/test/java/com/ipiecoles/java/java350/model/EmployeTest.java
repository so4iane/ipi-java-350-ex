package com.ipiecoles.java.java350.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EmployeTest {

    @Test
    public void getNombreAnneeAncienneteNow(){
        //Given
        Employe employe = new Employe();
        employe.setDateEmbauche(LocalDate.now());

        //When
        Integer anciennete = employe.getNombreAnneeAnciennete();

        //Then
        Assertions.assertEquals(0, anciennete.intValue());
    }

    @Test
    public void getNombreAnneeAnciennetePast(){
        //Given
        Employe employe = new Employe();
        employe.setDateEmbauche(LocalDate.now().minusYears(5L));

        //When
        Integer anciennete = employe.getNombreAnneeAnciennete();

        //Then
        Assertions.assertEquals(5, anciennete.intValue());
    }

    @Test
    public void getNombreAnneeAncienneteNull(){
        //Given
        Employe employe = new Employe();
        employe.setDateEmbauche(null);

        //When
        Integer anciennete = employe.getNombreAnneeAnciennete();

        //Then
        Assertions.assertEquals(0, anciennete.intValue());
    }

    @Test
    public void getNombreAnneeAncienneteFuture(){
        //Given
        Employe employe = new Employe();
        employe.setDateEmbauche(LocalDate.now().plusYears(3L));

        //When
        Integer anciennete = employe.getNombreAnneeAnciennete();

        //Then
        Assertions.assertEquals(0, anciennete.intValue());
    }

    @ParameterizedTest
    @CsvSource({
            "2, 'T44488', 0, 1.0, 2300.0",
            "2, 'T44488', 1, 0.5, 1200.0",
            "1, 'T44488', 3, 1.0, 1300.0",
            "2, 'T44488', 5, 1.0, 2800.0",
            "1, 'T44488', 2, 1.0, 1200.0",
            "2, 'M44488', 0, 1.0, 1700.0",
            "1, 'M44488', 1, 1.0, 1800.0",
            "1, 'M44488', 3, 1.0, 2000.0",
            "2, 'M44488', 5, 1.0, 2200.0"
    })
    public void getPrimeAnnuelle(Integer performance, String matricule, Long nbYearsAnciennete, Double tempsPartiel, Double primeAnnuelle){
        //Given
        Employe employe = new Employe("Nom", "Prénom", matricule, LocalDate.now().minusYears(nbYearsAnciennete), Entreprise.SALAIRE_BASE, performance, tempsPartiel);

        //When
        Double prime = employe.getPrimeAnnuelle();

        //Then
        Assertions.assertEquals(primeAnnuelle, prime);

    }
}