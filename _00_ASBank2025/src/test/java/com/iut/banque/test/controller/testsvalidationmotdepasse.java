package com.iut.banque.test.controller;

import com.iut.banque.controller.ValidationMotDePasse;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests pour la classe ValidationMotDePasse.
 * Teste toutes les méthodes de validation des mots de passe.
 */
class TestsValidationMotDePasse {

    // 🔹 Test paramétré pour les cas invalides de validerMotDePasse
    @ParameterizedTest(name = "{index} ⇒ motDePasse=''{0}'', message attendu=''{1}''")
    @CsvSource({
            "null, Le mot de passe est requis.",
            "'', Le mot de passe est requis.",
            "'   ', Le mot de passe est requis.",
            "'1234567', Le mot de passe doit contenir au moins 8 caractères."
    })
    void testValiderMotDePasseInvalide(String motDePasse, String messageAttendu) {
        if ("null".equals(motDePasse)) motDePasse = null;

        String resultat = ValidationMotDePasse.validerMotDePasse(motDePasse);

        assertNotNull(resultat, "Un mot de passe invalide doit retourner une erreur");
        assertEquals(messageAttendu, resultat);
    }

    @Test
    void testValiderMotDePasseValide() {
        String resultat = ValidationMotDePasse.validerMotDePasse("MotDePasse123");
        assertNull(resultat, "Un mot de passe valide ne doit pas retourner d'erreur");
    }

    @Test
    void testValiderMotDePasseLongueurMinimale() {
        String resultat = ValidationMotDePasse.validerMotDePasse("12345678");
        assertNull(resultat, "Un mot de passe de 8 caractères doit être valide");
    }

    // 🔹 Tests pour validerConfirmation
    @Test
    void testValiderConfirmationValide() {
        String resultat = ValidationMotDePasse.validerConfirmation("MotDePasse123", "MotDePasse123");
        assertNull(resultat, "Une confirmation correcte ne doit pas retourner d'erreur");
    }

    @Test
    void testValiderConfirmationNull() {
        String resultat = ValidationMotDePasse.validerConfirmation("MotDePasse123", null);
        assertNotNull("Une confirmation null doit retourner une erreur", resultat);
        assertEquals("La confirmation du mot de passe est requise.", resultat);
    }

    @Test
    void testValiderConfirmationVide() {
        String resultat = ValidationMotDePasse.validerConfirmation("MotDePasse123", "");
        assertNotNull("Une confirmation vide doit retourner une erreur", resultat);
        assertEquals("La confirmation du mot de passe est requise.", resultat);
    }

    @Test
    void testValiderConfirmationNonCorrespondante() {
        String resultat = ValidationMotDePasse.validerConfirmation("MotDePasse123", "AutreMotDePasse");
        assertNotNull("Une confirmation non correspondante doit retourner une erreur", resultat);
        assertEquals("Le mot de passe et la confirmation ne correspondent pas.", resultat);
    }

    @Test
    void testValiderConfirmationMotDePasseNull() {
        String resultat = ValidationMotDePasse.validerConfirmation(null, "Confirmation123");
        assertNotNull("Une confirmation avec mot de passe null doit retourner une erreur", resultat);
        assertEquals("Le mot de passe est requis.", resultat);
    }

    // 🔹 Tests pour validerDifference
    @Test
    void testValiderDifferenceMotsDePasseDifferents() {
        String resultat = ValidationMotDePasse.validerDifference("AncienMotDePasse", "NouveauMotDePasse");
        assertNull(resultat, "Des mots de passe différents ne doivent pas retourner d'erreur");
    }

    @Test
    void testValiderDifferenceMotsDePasseIdentiques() {
        String resultat = ValidationMotDePasse.validerDifference("MemeMotDePasse", "MemeMotDePasse");
        assertNotNull("Des mots de passe identiques doivent retourner une erreur", resultat);
        assertEquals("Le nouveau mot de passe doit être différent de l'ancien.", resultat);
    }

    @Test
    void testValiderDifferenceAncienMotDePasseNull() {
        String resultat = ValidationMotDePasse.validerDifference(null, "NouveauMotDePasse");
        assertNull(resultat, "Si l'ancien mot de passe est null, la validation doit passer");
    }

    @Test
    void testValiderDifferenceNouveauMotDePasseNull() {
        String resultat = ValidationMotDePasse.validerDifference("AncienMotDePasse", null);
        assertNull(resultat, "Si le nouveau mot de passe est null, la validation doit passer");
    }

    @Test
    void testValiderDifferenceLesDeuxMotsDePasseNull() {
        String resultat = ValidationMotDePasse.validerDifference(null, null);
        assertNull(resultat, "Si les deux mots de passe sont null, la validation doit passer");
    }

    @Test
    void testConstructorThrowsException() {
        assertThrows(IllegalStateException.class, ValidationMotDePasse::new,
            "Le constructeur doit lancer une IllegalStateException car c'est une classe utilitaire");
    }
}
