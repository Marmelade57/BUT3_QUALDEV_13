package com.iut.banque.controller;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class OublieMotDePasse {

    private final Map<String, String> tokenStore = new HashMap<>();
    private static final int TOKEN_LENGTH = 32;

    /**
     * Génère un token cryptographique à partir de l'adresse e-mail.
     */
    public String genererToken(String email) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest((email + System.currentTimeMillis())
                    .getBytes(StandardCharsets.UTF_8));
            byte[] truncated = new byte[TOKEN_LENGTH];
            System.arraycopy(hash, 0, truncated, 0, TOKEN_LENGTH);
            String token = Base64.getEncoder().encodeToString(truncated);
            tokenStore.put(email, token);
            return token;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur lors de la génération du token", e);
        }
    }

    /**
     * Génère un lien public de réinitialisation avec le token.
     */
    public String genererLienReinitialisation(String token) {
        return "https://app.banque.com/reset?token=" + token;
    }

    /**
     * Réinitialise le mot de passe si le token est valide et le mot de passe conforme.
     */
    public boolean reinitialiserMotDePasse(String email, String token, String nouveauMotDePasse) {
        String storedToken = tokenStore.get(email);

        if (storedToken == null || !storedToken.equals(token)) {
            return false; // Token invalide ou inexistant
        }

        // Utiliser la classe utilitaire pour la validation
        String erreurValidation = ValidationMotDePasse.validerMotDePasse(nouveauMotDePasse);
        if (erreurValidation != null) {
            return false; // Mot de passe invalide
        }

        // Simuler la mise à jour du mot de passe (à connecter à la base plus tard)
        System.out.println("Mot de passe mis à jour pour : " + email);
        tokenStore.remove(email); // Jeter le token après utilisation
        return true;
    }
}
