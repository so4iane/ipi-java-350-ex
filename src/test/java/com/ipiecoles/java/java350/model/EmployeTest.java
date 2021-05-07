package com.ipiecoles.java.java350.model;

import com.ipiecoles.java.java350.exception.EmployeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.persistence.criteria.CriteriaBuilder;
import java.time.LocalDate;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class EmployeTest {

    @Test
    void getNombreAnneeAncienneteNow(){
        //Given
        Employe employe = new Employe();
        employe.setDateEmbauche(LocalDate.now());

        //When
        Integer anciennete = employe.getNombreAnneeAnciennete();

        //Then
        Assertions.assertEquals(0, anciennete.intValue());
    }

    @Test
    void getNombreAnneeAnciennetePast(){
        //Given
        Employe employe = new Employe();
        employe.setDateEmbauche(LocalDate.now().minusYears(5L));

        //When
        Integer anciennete = employe.getNombreAnneeAnciennete();

        //Then
        Assertions.assertEquals(5, anciennete.intValue());
    }

    @Test
    void getNombreAnneeAncienneteNull(){
        //Given
        Employe employe = new Employe();
        employe.setDateEmbauche(null);

        //When
        Integer anciennete = employe.getNombreAnneeAnciennete();

        //Then
        Assertions.assertEquals(0, anciennete.intValue());
    }

    @Test
    void getNombreAnneeAncienneteFuture(){
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
            "1, 'M44488', 1, 0.5, 900.0",
            "1, 'M44488', 3, 1.0, 2000.0",
            "2, 'M44488', 5, 1.0, 2200.0"
    })
    void getPrimeAnnuelle(Integer performance, String matricule, Long nbYearsAnciennete, Double tempsPartiel, Double primeAnnuelle){
        //Given
        Employe employe = new Employe("Nom", "Prénom", matricule, LocalDate.now().minusYears(nbYearsAnciennete), Entreprise.SALAIRE_BASE, performance, tempsPartiel);

        //When
        Double prime = employe.getPrimeAnnuelle();

        //Then
        Assertions.assertEquals(primeAnnuelle, prime);

    }

    @Test
    void augmenterSalairebase() throws EmployeException {
        //Given
        Employe employe = new Employe();
        double augmentation = 41.24;


        //When
        double pourcentage = employe.augmenterSalaire(augmentation);

        //Then
        //(100+41.24)/100
        Assertions.assertEquals(1.4124, pourcentage);
    }

    @Test
    void augmenterSalaireNull() throws EmployeException {
        //Given
        Employe employe = new Employe();
        double augmentation = 0D;


        //When
        EmployeException exception = Assertions.assertThrows(EmployeException.class, ()-> employe.augmenterSalaire(augmentation)) ;

        //Then
        Assertions.assertEquals("Le pourcentage d'augmentation doit être supérieur à 0 et ne peut-être négatif!", exception.getMessage());
    }

    @Test
    void augmenterSalaireSupA50() throws EmployeException {
        //Given
        Employe employe = new Employe();
        double augmentation = 50.01;


        //When
        EmployeException exception = Assertions.assertThrows(EmployeException.class, ()-> employe.augmenterSalaire(augmentation)) ;

        //Then
        Assertions.assertEquals("Le pourcentage d'augmentation doit être inférieur ou égal à 50!", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
            "2019, 1.0, 8",
            "2021, 0.5, 5",
            "2022, 1.0, 10",
            "2032, 1.0, 11",
    })
    void getNbRtt(Integer year ,Double tempsPartiel, Integer RTT){
        //Given
        Employe employe = new Employe("Neymar", "Jean", "T44524",LocalDate.now().minusYears(2), Entreprise.SALAIRE_BASE, 1, tempsPartiel);
        LocalDate date = LocalDate.of(year,1,1);

        //When
        Integer totalRTT = employe.getNbRtt(date);

        //Then
        Assertions.assertEquals(RTT, totalRTT);

    }

    //Test pour coverage 100%
    @Test
    void getNbRttNow(){
        //Given
        Employe employe = new Employe("Neymar", "Jean", "T44524",LocalDate.now().minusYears(2), Entreprise.SALAIRE_BASE, 1, 0.5);

        //When
        Integer totalRTT = employe.getNbRtt();

        //Then (Valable qu'en 2021)
        Assertions.assertEquals(5,totalRTT);
    }

    //Test pour coverage 100%
    @ParameterizedTest
    @CsvSource({
            "1, 26",
            "4, 29",
            "0, 25",
            "2, 27",
    })
    void getNbConges(Integer anciennete, Integer nbConges){
        //Given
        Employe employe = new Employe("Neymar", "Jean", "T44524",LocalDate.now().minusYears(anciennete), Entreprise.SALAIRE_BASE, 1, 0.5);

        //When
        int totalConges = employe.getNbConges();

        //Then
        Assertions.assertEquals(nbConges, totalConges);

    }

}