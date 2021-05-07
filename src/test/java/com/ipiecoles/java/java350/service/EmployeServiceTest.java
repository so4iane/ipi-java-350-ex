package com.ipiecoles.java.java350.service;

import com.ipiecoles.java.java350.exception.EmployeException;
import com.ipiecoles.java.java350.model.Employe;
import com.ipiecoles.java.java350.model.NiveauEtude;
import com.ipiecoles.java.java350.model.Poste;
import com.ipiecoles.java.java350.repository.EmployeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityExistsException;

import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class EmployeServiceTest {

    @InjectMocks
    EmployeService employeService;

    @Mock
    EmployeRepository employeRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this.getClass());
    }

    @Test
    void embaucheNewEmployeTechnicienCapTempsPlein() throws EmployeException {
        //Given
        String prenom = "Jean";
        String nom = "Neymar";
        NiveauEtude etude = NiveauEtude.CAP;
        Poste poste = Poste.TECHNICIEN;
        Double temps = 1.0;
        Mockito.when(employeRepository.findByMatricule("T12345")).thenReturn(null);
        Mockito.when(employeRepository.findLastMatricule()).thenReturn("12344");

        //When
        employeService.embaucheEmploye(nom, prenom, poste,etude, temps);
        //embaucheEmploye return void donc on passe par un ArgumentCaptor
        ArgumentCaptor<Employe> employe = ArgumentCaptor.forClass(Employe.class);
        Mockito.verify(employeRepository, times(1)).save(employe.capture());

        //Then
        Assertions.assertEquals(prenom, employe.getValue().getPrenom());
        Assertions.assertEquals(nom, employe.getValue().getNom());
        Assertions.assertEquals(temps, employe.getValue().getTempsPartiel());
        Assertions.assertEquals("T12345", employe.getValue().getMatricule());
        //1521.22 * 1
        Assertions.assertEquals(1521.22, employe.getValue().getSalaire());
    }

    @Test
    void embaucheNewEmployeTechnicienCapTempsPleinNoLastMatricule() throws EmployeException {
        //Given
        String prenom = "Jean";
        String nom = "Neymar";
        NiveauEtude etude = NiveauEtude.CAP;
        Poste poste = Poste.TECHNICIEN;
        Double temps = 1.0;
        Mockito.when(employeRepository.findByMatricule("T00001")).thenReturn(null);
        Mockito.when(employeRepository.findLastMatricule()).thenReturn(null);

        //When
        employeService.embaucheEmploye(nom, prenom, poste,etude, temps);
        //embaucheEmploye return void donc on passe par un ArgumentCaptor
        ArgumentCaptor<Employe> employe = ArgumentCaptor.forClass(Employe.class);
        Mockito.verify(employeRepository, times(1)).save(employe.capture());

        //Then
        Assertions.assertEquals("T00001", employe.getValue().getMatricule());
    }

    @Test
    void embaucheExistingEmployeTechnicienCapTempsPlein() {
        //Given
        String prenom = "Jean";
        String nom = "Neymar";
        NiveauEtude etude = NiveauEtude.CAP;
        Poste poste = Poste.TECHNICIEN;
        Double temps = 1.0;
        Mockito.when(employeRepository.findByMatricule("T12345")).thenReturn(new Employe());
        Mockito.when(employeRepository.findLastMatricule()).thenReturn("12344");

        //When
        EntityExistsException exception = Assertions.assertThrows(EntityExistsException.class, ()-> employeService.embaucheEmploye(nom, prenom, poste,etude, temps));

        //Then
        Assertions.assertEquals("L'employé de matricule T12345 existe déjà en BDD", exception.getMessage());
    }

    @Test
    void embaucheNewEmployeManagerMasterTempsPartiel() throws EmployeException {
        //Given
        String prenom = "Jean";
        String nom = "Neymar";
        NiveauEtude etude = NiveauEtude.MASTER;
        Poste poste = Poste.MANAGER;
        Double temps = 0.5;
        Mockito.when(employeRepository.findByMatricule("M12345")).thenReturn(null);
        Mockito.when(employeRepository.findLastMatricule()).thenReturn("12344");

        //When
        employeService.embaucheEmploye(nom, prenom, poste,etude, temps);
        //embaucheEmploye return void donc on passe par un ArgumentCaptor
        ArgumentCaptor<Employe> employe = ArgumentCaptor.forClass(Employe.class);
        Mockito.verify(employeRepository, times(1)).save(employe.capture());

        //Then
        Assertions.assertEquals(prenom, employe.getValue().getPrenom());
        Assertions.assertEquals(nom, employe.getValue().getNom());
        Assertions.assertEquals(temps, employe.getValue().getTempsPartiel());
        Assertions.assertEquals("M12345", employe.getValue().getMatricule());
        //1521.22 * 1.4 * 0.5
        Assertions.assertEquals(1064.854, employe.getValue().getSalaire());
    }

    @Test
    void embaucheNewEmployeManagerMasterTempsPartielLimiteMatricule() {
        //Given
        String prenom = "Jean";
        String nom = "Neymar";
        NiveauEtude etude = NiveauEtude.MASTER;
        Poste poste = Poste.MANAGER;
        Double temps = 0.5;
        Mockito.when(employeRepository.findLastMatricule()).thenReturn("99999");

        //When
        EmployeException exception = Assertions.assertThrows(EmployeException.class, ()-> employeService.embaucheEmploye(nom, prenom, poste,etude, temps));

        //Then
        Assertions.assertEquals("Limite des 100000 matricules atteinte !", exception.getMessage());

    }

    @Test
    void calculPerformanceCommercialMauvaisCATraite() {
        //Given
        String matricule = "C24355";
        Long caTraite = null;
        Long objectifCa = 30000L;
        //Mockito.when(employeRepository.findByMatricule("T24355")).thenReturn(new Employe());

        //When
        EmployeException exception = Assertions.assertThrows(EmployeException.class, ()-> employeService.calculPerformanceCommercial(matricule, caTraite, objectifCa));

        //Then
        Assertions.assertEquals("Le chiffre d'affaire traité ne peut être négatif ou null !", exception.getMessage());
    }

    @Test
    void calculPerformanceCommercialMauvaisObjectifCa() {
        //Given
        String matricule = "C24355";
        Long caTraite = 42000L;
        Long objectifCa = null;
        //Mockito.when(employeRepository.findByMatricule("T24355")).thenReturn(new Employe());

        //When
        EmployeException exception = Assertions.assertThrows(EmployeException.class, ()-> employeService.calculPerformanceCommercial(matricule, caTraite, objectifCa));

        //Then
        Assertions.assertEquals("L'objectif de chiffre d'affaire ne peut être négatif ou null !", exception.getMessage());
    }

    @Test
    void calculPerformanceCommercialMauvaisMatricule() {
        //Given
        String matricule = "T24355";
        Long caTraite = 42000L;
        Long objectifCa = 50000L;
        //Mockito.when(employeRepository.findByMatricule("T24355")).thenReturn(new Employe());

        //When
        EmployeException exception = Assertions.assertThrows(EmployeException.class, ()-> employeService.calculPerformanceCommercial(matricule, caTraite, objectifCa));

        //Then
        Assertions.assertEquals("Le matricule ne peut être null et doit commencer par un C !", exception.getMessage());
    }
    @ParameterizedTest
    @CsvSource({
            "C24355, 4, 42000, 25200, 1",
            "C24355, 4, 42000, 37800, 3",
            "C24355, 4, 42000, 42000, 5",
            "C24355, 4, 42000, 46200, 6",
            "C24355, 4, 42000, 54600, 9",
    })
    void calculPerformanceCommercial(String matricule, int performance, Long objectifCa, Long caTraite, int expectedPerf) throws EmployeException {
        //Given
        Employe e = new Employe();
        e.setMatricule(matricule);
        e.setPerformance(performance);
        employeRepository.save(e);
        Mockito.when(employeRepository.findByMatricule("C24355")).thenReturn(e);
        Mockito.when(employeRepository.avgPerformanceWhereMatriculeStartsWith("C")).thenReturn(1.5);


        //When
        employeService.calculPerformanceCommercial(e.getMatricule(), caTraite, objectifCa);
        //embaucheEmploye return void donc on passe par un ArgumentCaptor
        ArgumentCaptor<Employe> employe = ArgumentCaptor.forClass(Employe.class);

        //Then
        Mockito.verify(employeRepository, times(2)).save(employe.capture());
        Assertions.assertEquals(expectedPerf, employe.getValue().getPerformance());
    }
}