package com.ipiecoles.java.java350.model;

import com.ipiecoles.java.java350.exception.EmployeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Objects;

@Entity
public class Employe {
    private static final Logger logger = LoggerFactory.getLogger(Employe.class);

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String nom;

    private String prenom;

    private String matricule;

    private LocalDate dateEmbauche;

    private Double salaire = Entreprise.SALAIRE_BASE;

    private Integer performance = Entreprise.PERFORMANCE_BASE;

    private Double tempsPartiel = 1.0;

    public Employe() {
    }

    public Employe(String nom, String prenom, String matricule, LocalDate dateEmbauche, Double salaire, Integer performance, Double tempsPartiel) {
        this.nom = nom;
        this.prenom = prenom;
        this.matricule = matricule;
        this.dateEmbauche = dateEmbauche;
        this.salaire = salaire;
        this.performance = performance;
        this.tempsPartiel = tempsPartiel;
    }

    /**
     * Méthode calculant le nombre d'années d'ancienneté à partir de la date d'embauche
     * @return
     */
    public Integer getNombreAnneeAnciennete() {
        if(dateEmbauche!= null){
            return Math.max((LocalDate.now().getYear() - dateEmbauche.getYear()),0);
        }
        return 0;
    }

    public Integer getNbConges() {
        return Entreprise.NB_CONGES_BASE + this.getNombreAnneeAnciennete();
    }

    public Integer getNbRtt(){
        return getNbRtt(LocalDate.now());
    }


    /**
     * Calcul ddu nombre de RTT selon la règle :
     * Nombre de jours dans l'année - Nombre de jours travaillés dans l'année en plein temps
     * - Nombre de samedi et dimanche dans l'année - Nombre de jours fériés ne tombant pas le week-end
     * - Nombre de congés payés
     *
     *
     * @param date la date à laquelle on souhaite connaître le nombre de jour RTT de l'année
     * @throws EmployeException si la valeur entrée n'est pas comprise entre 0 et 50, non nulle et non négative
     * @return le nombre de jour RTT sur l'année concernée;
     */
    public Integer getNbRtt(LocalDate date){
        //Calcul du nombre de jour dans l'année (selon bisextile)
        int nbJAnnee = date.isLeapYear() ? 366 : 365;

        //Calcul nombre de samedis et dimanches par année
        int nbWeekends = 104;

        //Récupération du premier jour de l'année concernée pour connaître le nombre de weekend dans l'année
        switch (LocalDate.of(date.getYear(),1,1).getDayOfWeek()){
            case THURSDAY:
                logger.debug("L'année commence par un jeudi");
                if(date.isLeapYear()){
                    nbWeekends += 1;
                }
                break;
            case FRIDAY:
                logger.debug("L'année commence par un vendredi");
                if(date.isLeapYear()) {
                    nbWeekends += 2;
                }else
                {
                    nbWeekends += 1;
                }
                break;
            case SATURDAY:
                logger.debug("L'année commence par un samedi");
                nbWeekends += 1;
                break;
        }

        //Calcul du nombre de jours feriés par année
        int nbJFeries = (int) Entreprise.joursFeries(date).stream().filter(localDate ->
                localDate.getDayOfWeek().getValue() <= DayOfWeek.FRIDAY.getValue()).count();

        //Calcul du nombre de RTT dans l'année
        int nbRTT = (int) Math.ceil((nbJAnnee - Entreprise.NB_JOURS_MAX_FORFAIT - nbWeekends - Entreprise.NB_CONGES_BASE - nbJFeries) * tempsPartiel);

        return nbRTT;
    }

    /**
     * Calcul de la prime annuelle selon la règle :
     * Pour les managers : Prime annuelle de base bonnifiée par l'indice prime manager
     * Pour les autres employés, la prime de base plus éventuellement la prime de performance calculée si l'employé
     * n'a pas la performance de base, en multipliant la prime de base par un l'indice de performance
     * (égal à la performance à laquelle on ajoute l'indice de prime de base)
     *
     * Pour tous les employés, une prime supplémentaire d'ancienneté est ajoutée en multipliant le nombre d'année
     * d'ancienneté avec la prime d'ancienneté. La prime est calculée au pro rata du temps de travail de l'employé
     *
     * @return la prime annuelle de l'employé en Euros et cents
     */
    //Matricule, performance, date d'embauche, temps partiel, prime
    public Double getPrimeAnnuelle(){
        //Calcule de la prime d'ancienneté
        Double primeAnciennete = Entreprise.PRIME_ANCIENNETE * this.getNombreAnneeAnciennete();
        Double prime;
        //Prime du manager (matricule commençant par M) : Prime annuelle de base multipliée par l'indice prime manager
        //plus la prime d'anciennté.
        if(matricule != null && matricule.startsWith("M")) {
            prime = Entreprise.primeAnnuelleBase() * Entreprise.INDICE_PRIME_MANAGER + primeAnciennete;
        }
        //Pour les autres employés en performance de base, uniquement la prime annuelle plus la prime d'ancienneté.
        else if (this.performance == null || Entreprise.PERFORMANCE_BASE.equals(this.performance)){
            prime = Entreprise.primeAnnuelleBase() + primeAnciennete;
        }
        //Pour les employés plus performance, on bonnifie la prime de base en multipliant par la performance de l'employé
        // et l'indice de prime de base.
        else {
            prime = Entreprise.primeAnnuelleBase() * (this.performance + Entreprise.INDICE_PRIME_BASE) + primeAnciennete;
        }
        //Au pro rata du temps partiel.
        return prime * this.tempsPartiel;
    }

    /**
     * Calcul de l'augmentation salaire selon la règle :
     * Un chiffre entre 0 et 50 est entré en paramètre et correspond à l'augmentation en % du salaire
     * Aucune augmentation ne peut-être supérieure à 50%, nulle ou négative
     *
     * Cette fonction renvoie le multiplicateur du salaire qui doit correspondre à l'augmentation
     * Ex : si pourcentage = 30, alors la fonction reverra (100+30)/100 = 1,3
     *
     * @param pourcentage le pourcentage duquel on souhaite augmenter l'employé
     * @throws EmployeException si la valeur entrée n'est pas comprise entre 0 et 50, non nulle et non négative
     * @return le mutipliateur d'augmentation;
     */
    //Augmenter salaire
    public double augmenterSalaire(double pourcentage) throws EmployeException {
        //Vérification de la validité de pourcentage
        if(pourcentage<=0D){
            logger.error("Le pourcentage d'augmentation doit être supérieur à 0 et ne peut-être négatif!");
            throw new EmployeException("Le pourcentage d'augmentation doit être supérieur à 0 et ne peut-être négatif!");
        }else if(50D<pourcentage){
            logger.error("Le pourcentage d'augmentation doit être inférieur ou égal à 50!");
            throw new EmployeException("Le pourcentage d'augmentation doit être inférieur ou égal à 50!");
        }

        //Calcul du multiplicateur d'augmentation
        pourcentage = (100+pourcentage)/100;

        return pourcentage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the nom
     */
    public String getNom() {
        return nom;
    }

    /**
     * @param nom the nom to set
     */
    public Employe setNom(String nom) {
        this.nom = nom;
        return this;
    }

    /**
     * @return the prenom
     */
    public String getPrenom() {
        return prenom;
    }

    /**
     * @param prenom the prenom to set
     */
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    /**
     * @return the matricule
     */
    public String getMatricule() {
        return matricule;
    }

    /**
     * @param matricule the matricule to set
     */
    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

    /**
     * @return the dateEmbauche
     */
    public LocalDate getDateEmbauche() {
        return dateEmbauche;
    }

    /**
     * @param dateEmbauche the dateEmbauche to set
     */
    public void setDateEmbauche(LocalDate dateEmbauche) {
        this.dateEmbauche = dateEmbauche;
    }

    /**
     * @return the salaire
     */
    public Double getSalaire() {
        return salaire;
    }

    /**
     * @param salaire the salaire to set
     */
    public void setSalaire(Double salaire) {
        this.salaire = salaire;
    }

    public Integer getPerformance() {
        return performance;
    }

    public void setPerformance(Integer performance) {
        this.performance = performance;
    }

    public Double getTempsPartiel() {
        return tempsPartiel;
    }

    public void setTempsPartiel(Double tempsPartiel) {
        this.tempsPartiel = tempsPartiel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employe)) return false;
        Employe employe = (Employe) o;
        return Objects.equals(id, employe.id) &&
                Objects.equals(nom, employe.nom) &&
                Objects.equals(prenom, employe.prenom) &&
                Objects.equals(matricule, employe.matricule) &&
                Objects.equals(dateEmbauche, employe.dateEmbauche) &&
                Objects.equals(salaire, employe.salaire) &&
                Objects.equals(performance, employe.performance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nom, prenom, matricule, dateEmbauche, salaire, performance);
    }
}
