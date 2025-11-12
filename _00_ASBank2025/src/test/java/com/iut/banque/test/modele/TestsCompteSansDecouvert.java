package com.iut.banque.test.modele;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.exceptions.InsufficientFundsException;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.CompteSansDecouvert;

import java.util.Objects;

public class TestsCompteSansDecouvert {

    private CompteSansDecouvert compte;
    private Client client;

    @Before
    public void setUp() throws IllegalFormatException {
        try {
            client = new Client("Doe", "John", "123 Street", false, "j.doe1", "password123", "1234567890");
            compte = new CompteSansDecouvert("FR0123456789", 100.0, client);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Test du constructeur avec paramètres
     */
    @Test
    public void testConstructeurAvecParametres() {
        assertEquals("FR0123456789", compte.getNumeroCompte());
        assertEquals(100.0, compte.getSolde(), 0.001);
        assertEquals(client, compte.getOwner());
    }

    /**
     * Test du constructeur sans paramètres
     */
    @Test
    public void testConstructeurSansParametres() {
        CompteSansDecouvert compteVide = new CompteSansDecouvert();
        assertNull("Le numéro de compte devrait être null", compteVide.getNumeroCompte());
        assertEquals("Le solde devrait être à 0", 0.0, compteVide.getSolde(), 0.001);
        assertNull("Le propriétaire devrait être null", compteVide.getOwner());
    }

    /**
     * Test de la méthode getClassName()
     */
    @Test
    public void testGetClassName() {
        assertEquals("Le nom de la classe devrait être 'CompteSansDecouvert'", 
                    "CompteSansDecouvert", compte.getClassName());
    }

    /**
     * Test de la classe getClassName() pour les CompteSansDecouvert
     */
    @Test
    public void testGetClassNameSansDecouvert() {
        assertEquals("CompteSansDecouvert", compte.getClassName());
    }

    /**
     * Test de la méthode debiter avec un montant négatif
     */
    @Test
    public void testDebiterMontantNegatif() {
        double soldeInitial = compte.getSolde();
        try {
            compte.debiter(-50.0);
            fail("Devrait lancer une IllegalFormatException pour un montant négatif");
        } catch (IllegalFormatException e) {
            assertEquals("Le message d'erreur devrait correspondre", 
                        "Le montant ne peux être négatif", e.getMessage());
            assertEquals("Le solde ne devrait pas changer", 
                        soldeInitial, compte.getSolde(), 0.001);
        } catch (Exception e) {
            fail("Exception de type " + e.getClass().getSimpleName()
                    + " récupérée alors qu'une IllegalFormatException était attendue");
        }
    }

    /**
     * Test de la méthode debiter avec un montant nul
     */
    @Test
    public void testDebiterMontantNul() {
        double soldeInitial = compte.getSolde();
        try {
            compte.debiter(0.0);
            assertEquals("Le solde ne devrait pas changer", 
                        soldeInitial, compte.getSolde(), 0.001);
        } catch (Exception e) {
            fail("Aucune exception ne devrait être levée pour un montant nul");
        }
    }

    /**
     * Test de la méthode debiter avec un montant valide
     */
    @Test
    public void testDebiterMontantValide() {
        double montant = 50.0;
        double soldeInitial = compte.getSolde();
        try {
            compte.debiter(montant);
            assertEquals("Le solde devrait être décrémenté du montant", 
                        soldeInitial - montant, compte.getSolde(), 0.001);
        } catch (Exception e) {
            fail("Aucune exception ne devrait être levée pour un retrait valide");
        }
    }

    /**
     * Test de la méthode debiter avec un montant supérieur au solde
     */
    @Test
    public void testDebiterMontantSuperieurAuSolde() {
        double montant = 150.0;
        double soldeInitial = compte.getSolde();
        try {
            compte.debiter(montant);
            fail("Devrait lancer une InsufficientFundsException pour un montant supérieur au solde");
        } catch (InsufficientFundsException e) {
            assertEquals("Le message d'erreur devrait correspondre", 
                        "Le solde du compte " + compte.getNumeroCompte() + " est insuffisant.", 
                        e.getMessage());
            assertEquals("Le solde ne devrait pas changer", 
                        soldeInitial, compte.getSolde(), 0.001);
        } catch (Exception e) {
            fail("Exception de type " + e.getClass().getSimpleName() +
                    " récupérée alors qu'une InsufficientFundsException était attendue");
        }
    }

    /**
     * Test de la méthode debiter avec un montant égal au solde
     */
    @Test
    public void testDebiterMontantEgalAuSolde() {
        double montant = 100.0;
        try {
            compte.debiter(montant);
            assertEquals("Le solde devrait être à zéro", 0.0, compte.getSolde(), 0.001);
        } catch (Exception e) {
            fail("Aucune exception ne devrait être levée pour un retrait égal au solde");
        }
    }

    /**
     * Test de la métode debiter avec un montant négatif
     */
    @Test
    public void testCrediterCompteMontantNegatif() {
        /*
         * Méthode qui va tester la méthode debiter avec un montant négatif,
         * auquel cas il devrait attraper un IllegalFormatExcepion
         */
        try {
            compte.debiter(-100);
            fail("La méthode n'a pas renvoyé d'exception !");
        } catch (IllegalFormatException ife) {
            // Succès attendu
            assertTrue(true);
        } catch (Exception e) {
            fail("Exception de type " + e.getClass().getSimpleName()
                    + " récupérée alors qu'une IllegalFormatException était attendue");
        }
    }

    /**
     * Tests en rapport avec la méthode "Debiter" de la classe
     * CompteAvecDecouvert
     * 
     * @throws IllegalFormatException
     */
    @Test
    public void testDebiterCompteAvecDecouvertValeurPossible() throws IllegalFormatException {
        /*
         * Méthode qui va tester la méthode debiter pour un compte sans
         * découvert avec un montant réalisable (en fonction du montant retiré
         * et du seuil maximal du compte avec découvert)
         */
        try {
            compte.debiter(50);
            assertEquals(50.0, compte.getSolde(), 0.001);
        } catch (InsufficientFundsException e) {
            fail("Il ne devrait pas avoir d'exception ici.");
        }
    }

    @Test
    public void testDebiterCompteAvecDecouvertValeurImpossible() throws IllegalFormatException {
        /*
         * Méthode qui va tester la méthode retrait pour un compte sans
         * découvert avec un montant irréalisable (un retrait qui irait au delà
         * du seuil pour le compte avec découvert). La fonction devrait renvoyer
         * une exception en cas de problême
         */
        try {
            compte.debiter(200);
            fail("Il aurait dû y avoir une InsufficientFundsException !");
        } catch (InsufficientFundsException e) {
            // Succès attendu
            assertTrue(true);
        } catch (Exception e) {
            fail("Exception de type " + e.getClass().getSimpleName() +
                    " récupérée alors qu'une InsufficientFundsException était attendue");
        }
    }

}
