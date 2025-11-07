package com.iut.banque.test.modele;

import static org.junit.jupiter.api.Assertions.*;

import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.Compte;
import com.iut.banque.modele.CompteSansDecouvert;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TestsCompte {

    private Compte compte;

    @BeforeEach
    void setUp() throws IllegalFormatException {
        compte = new CompteSansDecouvert("WU1234567890", 0, new Client());
    }

    // ============================
    // Tests créditer
    // ============================
    @Test
    void testCrediterCompte() throws IllegalFormatException {
        compte.crediter(100);
        assertEquals(100.0, compte.getSolde(), 0.001);
    }

    @Test
    void testCrediterCompteMontantNegatif() {
        assertThrows(IllegalFormatException.class, () -> {
            compte.crediter(-100);
        }, "Un crédit négatif doit lever IllegalFormatException");
    }

    // ============================
    // Tests constructeur avec numéro de compte incorrect
    // ============================
    @Test
    void testConstruireCompteAvecFormatNumeroCompteIncorrect() {
        assertThrows(IllegalFormatException.class, () -> {
            new CompteSansDecouvert("&éþ_ëü¤", 0, new Client());
        }, "Le constructeur doit lever IllegalFormatException pour un numéro incorrect");
    }

    // ============================
    // Tests paramétrés checkFormatNumeroCompte
    // ============================
    @ParameterizedTest(name = "{index} ⇒ numeroCompte=''{0}'', attendu={1}")
    @CsvSource({
            // Cas valides
            "'FR0123456789', true",
            // Cas invalides
            "'F0123456789', false",        // une seule lettre au début
            "'0123456789', false",         // aucune lettre au début
            "'FRA0123456789', false",      // trois lettres au début
            "'FR0123A456789', false",      // lettre au milieu
            "'FR00123456789', false",      // plus de chiffres que prévu
            "'FR123456789', false",        // moins de chiffres que prévu
            "'FR0123456789A', false"      // lettre à la fin
    })
    void testCheckFormatNumeroCompte(String numeroCompte, boolean attendu) {
        boolean resultat = Compte.checkFormatNumeroCompte(numeroCompte);
        assertEquals(attendu, resultat,
                () -> "Numéro de compte " + numeroCompte + (attendu ? " devrait être valide" : " ne devrait pas être valide"));
    }
}
