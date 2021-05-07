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
    void embaucheNewEmployeTechnicienCapTempsPleinNoLastMateicule() throws EmployeException {
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
}