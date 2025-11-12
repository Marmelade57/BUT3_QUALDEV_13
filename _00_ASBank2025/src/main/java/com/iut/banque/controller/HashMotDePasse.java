package com.iut.banque.controller;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Arrays;

import com.iut.banque.exceptions.TechnicalException;

public class HashMotDePasse {

    public static class HashResult {
        public final String saltBase64;
        public final String hashBase64;

        public HashResult(byte[] salt, byte[] hash) {
            this.saltBase64 = Base64.getEncoder().encodeToString(salt);
            this.hashBase64 = Base64.getEncoder().encodeToString(hash);
        }
    }

    private byte[] genererSel() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    public HashResult hashPassword(String motDePasse) throws TechnicalException {
        if (motDePasse == null || motDePasse.isEmpty()) {
            throw new TechnicalException("Le mot de passe doit être une chaîne non vide");
        }

        byte[] sel = genererSel();
        byte[] hash = computeHash(motDePasse, sel);
        return new HashResult(sel, hash);
    }

    /**
     * Compute hash with a given salt — useful for verification or tests.
     * 
     * Note de sécurité : Utilise PBKDF2WithHmacSHA1 avec 65536 itérations.
     * Bien que SHA-256 soit préférable, SHA-1 reste acceptable pour PBKDF2
     * avec un nombre d'itérations élevé. Pour de nouveaux projets, considérer
     * PBKDF2WithHmacSHA256.
     */
    protected byte[] computeHash(String motDePasse, byte[] salt) throws TechnicalException {
        try {
            KeySpec spec = new PBEKeySpec(motDePasse.toCharArray(), salt, 65536, 128);
            // Note: PBKDF2WithHmacSHA1 est utilisé pour compatibilité avec l'existant
            // Pour améliorer la sécurité, considérer PBKDF2WithHmacSHA256
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return factory.generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new TechnicalException("Erreur lors du hashage du mot de passe", e);
        }
    }

    /**
     * Verifies whether a password matches the stored hash using the same salt.
     */
    public boolean verifyPassword(String motDePasse, String storedSaltBase64, String storedHashBase64)
            throws TechnicalException {
        if (motDePasse == null || storedSaltBase64 == null || storedHashBase64 == null) {
            return false;
        }

        byte[] salt = Base64.getDecoder().decode(storedSaltBase64);
        byte[] expectedHash = Base64.getDecoder().decode(storedHashBase64);
        byte[] actualHash = computeHash(motDePasse, salt);

        return Arrays.equals(expectedHash, actualHash);
    }
}
