package com.iut.banque.test.modele;

import static org.junit.jupiter.api.Assertions.*;

import com.iut.banque.modele.Client;
import com.iut.banque.modele.CompteAvecDecouvert;
import com.iut.banque.modele.CompteSansDecouvert;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;

import java.util.Map;

class TestsClient {

    // ============================
    // Tests paramétrés pour userId
    // ============================
    @ParameterizedTest(name = "{index} ⇒ userId=''{0}'', attendu={1}")
    @CsvSource({
            "'a.utilisateur928', true",
            "'32a.abc1', false",
            "'aaa.abc1', false",
            "'abc1', false",
            "'', false",
            "'a.138', false",
            "'a.a1', true",
            "'a.bcdé1', false",
            "'a.abc01', false",
            "'a.ab.c1', false"
    })
    void testCheckFormatUserIdClient(String userId, boolean attendu) {
        boolean resultat = Client.checkFormatUserIdClient(userId);
        assertEquals(attendu, resultat,
                () -> "UserId " + userId + (attendu ? " devrait être valide" : " ne devrait pas être valide"));
    }

    // ============================
    // Tests paramétrés pour numeroClient
    // ============================
    @ParameterizedTest(name = "{index} ⇒ numeroClient=''{0}'', attendu={1}")
    @CsvSource({
            "'1234567890', true",
            "'12a456789', false",
            "'12#456789', false",
            "'12345678', false",
            "'12345678901', false"
    })
    void testCheckFormatNumeroClient(String numeroClient, boolean attendu) {
        boolean resultat = Client.checkFormatNumeroClient(numeroClient);
        assertEquals(attendu, resultat,
                () -> "NumeroClient " + numeroClient + (attendu ? " devrait être valide" : " ne devrait pas être valide"));
    }

    // ============================
    // Tests possedeComptesADecouvert
    // ============================
    @Test
    void testPossedeComptesADecouvertSansComptes() throws Exception {
        Client c = new Client("John", "Doe", "20 rue Bouvier", true, "j.doe1", "password", "1234567890");
        assertFalse(c.possedeComptesADecouvert(), "Aucun compte : la méthode doit retourner false");
    }

    @Test
    void testPossedeComptesADecouvertAvecSeulsComptesSansDecouvert() throws Exception {
        Client c = new Client("John", "Doe", "20 rue Bouvier", true, "j.doe1", "password", "1234567890");
        c.addAccount(new CompteSansDecouvert("FR1234567890", 42, c));
        c.addAccount(new CompteSansDecouvert("FR1234567891", 0, c));
        assertFalse(c.possedeComptesADecouvert(), "Tous comptes sans découvert : la méthode doit retourner false");
    }

    @Test
    void testPossedeComptesADecouvertAvecUnCompteADecouvert() throws Exception {
        Client c = new Client("John", "Doe", "20 rue Bouvier", true, "j.doe1", "password", "1234567890");
        c.addAccount(new CompteAvecDecouvert("FR1234567890", -42, 100, c));
        assertTrue(c.possedeComptesADecouvert(), "Compte à découvert : la méthode doit retourner true");
    }

    @Test
    void testPossedeComptesADecouvertAvecPlusieursComptesADecouvert() throws Exception {
        Client c = new Client("John", "Doe", "20 rue Bouvier", true, "j.doe1", "password", "1234567890");
        c.addAccount(new CompteSansDecouvert("FR1234567890", 42, c));
        c.addAccount(new CompteAvecDecouvert("FR1234567891", -42, 100, c));
        c.addAccount(new CompteAvecDecouvert("FR1234567892", 1000, 100, c));
        c.addAccount(new CompteAvecDecouvert("FR1234567893", -4242, 5000, c));
        assertTrue(c.possedeComptesADecouvert(), "Au moins un compte à découvert : la méthode doit retourner true");
    }

    // ============================
    // Tests getComptesAvecSoldeNonNul
    // ============================
    @Test
    void testGetComptesAvecSoldeNonNulAucunSoldeNonNul() throws Exception {
        Client c = new Client("John", "Doe", "20 rue Bouvier", true, "j.doe1", "password", "1234567890");
        c.addAccount(new CompteAvecDecouvert("FR1234567890", 0, 42, c));
        c.addAccount(new CompteSansDecouvert("FR1234567891", 0, c));
        Map<String, ?> comptes = c.getComptesAvecSoldeNonNul();
        assertTrue(comptes.isEmpty(), "Aucun compte avec solde non nul attendu");
    }

    @Test
    void testGetComptesAvecSoldeNonNulCompteSansDecouvertNonNul() throws Exception {
        Client c = new Client("John", "Doe", "20 rue Bouvier", true, "j.doe1", "password", "1234567890");
        c.addAccount(new CompteAvecDecouvert("FR1234567890", 0, 42, c));
        c.addAccount(new CompteSansDecouvert("FR1234567891", 1, c));
        Map<String, ?> comptes = c.getComptesAvecSoldeNonNul();
        assertTrue(comptes.containsKey("FR1234567891"), "Compte avec solde non nul attendu dans la map");
    }

    @Test
    void testGetComptesAvecSoldeNonNulCompteAvecDecouvertNonNul() throws Exception {
        Client c = new Client("John", "Doe", "20 rue Bouvier", true, "j.doe1", "password", "1234567890");
        c.addAccount(new CompteAvecDecouvert("FR1234567890", 1, 42, c));
        c.addAccount(new CompteSansDecouvert("FR1234567891", 0, c));
        Map<String, ?> comptes = c.getComptesAvecSoldeNonNul();
        assertTrue(comptes.containsKey("FR1234567890"), "Compte à découvert non nul attendu dans la map");
    }

}
