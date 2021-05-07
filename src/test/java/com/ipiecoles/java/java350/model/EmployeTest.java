package com.ipiecoles.java.java350.model;

import com.ipiecoles.java.java350.exception.EmployeException;
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
            "1, 'M44488', 1, 0.5, 900.0",
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

    @Test
    public void augmenterSalairebase() throws EmployeException {
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
    public void augmenterSalaireNull() throws EmployeException {
        //Given
        Employe employe = new Employe();
        double augmentation = 0D;


        //When
        EmployeException exception = Assertions.assertThrows(EmployeException.class, ()-> employe.augmenterSalaire(augmentation)) ;

        //Then
        Assertions.assertEquals("Le pourcentage d'augmentation doit être supérieur à 0 et ne peut-être négatif!", exception.getMessage());
    }

    @Test
    public void augmenterSalaireSupA50() throws EmployeException {
        //Given
        Employe employe = new Employe();
        double augmentation = 50.01;


        //When
        EmployeException exception = Assertions.assertThrows(EmployeException.class, ()-> employe.augmenterSalaire(augmentation)) ;

        //Then
        Assertions.assertEquals("Le pourcentage d'augmentation doit être inférieur ou égal à 50!", exception.getMessage());
    }

}