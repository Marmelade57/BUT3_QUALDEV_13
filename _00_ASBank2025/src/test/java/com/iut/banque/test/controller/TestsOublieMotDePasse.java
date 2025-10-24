package com.iut.banque.test.controller;

import com.iut.banque.controller.OublieMotDePasse;
import org.junit.Test;

import java.util.Base64;

import static org.junit.Assert.*;

public class TestsOublieMotDePasse extends OublieMotDePasse {

    @Test
    public void testGenererTokenNonVide() {
        String token = genererToken("utilisateur@example.com");
        assertNotNull("Le token ne doit pas être nul", token);
        assertFalse("Le token ne doit pas être vide", token.isEmpty());
    }

    @Test
    public void testGenererTokenEncodageBase64() {
        String email = "test@example.com";
        String token = genererToken(email);

        try {
            byte[] decode = Base64.getDecoder().decode(token);
            assertNotNull("Le token doit être un encodage Base64 valide", decode);
        } catch (IllegalArgumentException e) {
            fail("Le token ne doit pas provoquer d'erreur Base64");
        }
    }

    @Test
    public void testLienDeReinitialisationContientToken() {
        String email = "client@example.com";
        String token = genererToken(email);
        String lien = genererLienReinitialisation(token);

        assertTrue("Le lien doit contenir le token généré", lien.contains(token));
        assertTrue("Le lien doit utiliser HTTPS", lien.startsWith("https://"));
    }

    @Test
    public void testReinitialisationMotDePasseValide() {
        String email = "user@example.com";
        String token = genererToken(email);

        boolean resultat = reinitialiserMotDePasse(email, token, "NouveauMdp123!");
        assertTrue("La réinitialisation doit réussir avec un token valide", resultat);
    }


    @Test
    public void testReinitialisationMotDePasseInvalide() {
        String email = "test@example.com";
        String mauvaisToken = "tokenInvalide";

        boolean resultat = reinitialiserMotDePasse(email, mauvaisToken, "Motdepasse");
        assertFalse("La réinitialisation doit échouer avec un token erroné", resultat);
    }

    @Test
    public void testMotDePasseTropCourtRefuse() {
        String email = "short@example.com";
        String token = genererToken(email);

        boolean resultat = reinitialiserMotDePasse(email, token, "123");
        assertFalse("Le système doit refuser un mot de passe trop court", resultat);
    }
}
