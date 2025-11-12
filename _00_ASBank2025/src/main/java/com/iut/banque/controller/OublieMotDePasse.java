package com.iut.banque.controller;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Gère le processus d'oubli et de réinitialisation de mot de passe.
 * 
 * Note de sécurité : Le stockage des tokens en mémoire (HashMap) est acceptable
 * pour un prototype/développement. En production, il est recommandé d'utiliser
 * un stockage persistant avec expiration automatique (ex: Redis, base de données).
 */
public class OublieMotDePasse {
    private static final Logger LOGGER = Logger.getLogger(OublieMotDePasse.class.getName());
    // Note: En production, remplacer par un stockage persistant avec expiration
    private final Map<String, String> tokenStore = new HashMap<>();
    private static final int TOKEN_LENGTH = 32;

    /**
     * Génère un token cryptographique sécurisé à partir de l'adresse e-mail.
     * Utilise SecureRandom pour garantir l'imprévisibilité du token.
     */
    public String genererToken(String email) {
        try {
            // Utilisation de SecureRandom pour générer un nonce aléatoire sécurisé
            SecureRandom secureRandom = new SecureRandom();
            byte[] nonce = new byte[16];
            secureRandom.nextBytes(nonce);
            
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // Combinaison de l'email, du nonce aléatoire et du timestamp pour plus de sécurité
            String input = email + Base64.getEncoder().encodeToString(nonce) + System.currentTimeMillis();
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            byte[] truncated = new byte[TOKEN_LENGTH];
            System.arraycopy(hash, 0, truncated, 0, TOKEN_LENGTH);
            String token = Base64.getEncoder().encodeToString(truncated);
            tokenStore.put(email, token);
            return token;
        } catch (NoSuchAlgorithmException e) {
            // ✅ Fix for java:S112 — use a dedicated exception
            throw new TokenGenerationException("Erreur lors de la génération du token", e);
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

        // ✅ Fix for java:S2629 — use lambda to defer log message construction
        LOGGER.info(() -> String.format("Mot de passe mis à jour pour : %s", email));

        tokenStore.remove(email); // Supprimer le token après utilisation
        return true;
    }
}

/**
 * Exception dédiée pour la génération de token.
 */
class TokenGenerationException extends RuntimeException {
    public TokenGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
