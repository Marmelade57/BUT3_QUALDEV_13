package com.iut.banque.controller;

/**
 * Classe utilitaire pour la validation des mots de passe.
 * Centralise les règles de validation pour éviter la duplication de code.
 */
public class ValidationMotDePasse {

	private static final int LONGUEUR_MINIMALE = 8;

	/**
	 * Valide qu'un mot de passe respecte les critères de sécurité.
	 * 
	 * @param motDePasse
	 *            : le mot de passe à valider
	 * @return null si le mot de passe est valide, un message d'erreur sinon
	 */
	public static String validerMotDePasse(String motDePasse) {
		if (motDePasse == null || motDePasse.trim().isEmpty()) {
			return "Le mot de passe est requis.";
		}

		if (motDePasse.length() < LONGUEUR_MINIMALE) {
			return "Le mot de passe doit contenir au moins " + LONGUEUR_MINIMALE + " caractères.";
		}

		return null; // Mot de passe valide
	}

	/**
	 * Vérifie que deux mots de passe correspondent.
	 * 
	 * @param motDePasse
	 *            : le premier mot de passe
	 * @param confirmation
	 *            : la confirmation du mot de passe
	 * @return null si les mots de passe correspondent, un message d'erreur sinon
	 */
	public static String validerConfirmation(String motDePasse, String confirmation) {
		if (confirmation == null || confirmation.trim().isEmpty()) {
			return "La confirmation du mot de passe est requise.";
		}

		if (motDePasse == null) {
			return "Le mot de passe est requis.";
		}

		if (!motDePasse.equals(confirmation)) {
			return "Le mot de passe et la confirmation ne correspondent pas.";
		}

		return null; // Confirmation valide
	}

	/**
	 * Vérifie qu'un nouveau mot de passe est différent de l'ancien.
	 * 
	 * @param ancienMotDePasse
	 *            : l'ancien mot de passe
	 * @param nouveauMotDePasse
	 *            : le nouveau mot de passe
	 * @return null si les mots de passe sont différents, un message d'erreur sinon
	 */
	public static String validerDifference(String ancienMotDePasse, String nouveauMotDePasse) {
		// Si l'un des deux est null, la validation passe (pas de contrainte)
		if (ancienMotDePasse == null || nouveauMotDePasse == null) {
			return null;
		}

		if (ancienMotDePasse.equals(nouveauMotDePasse)) {
			return "Le nouveau mot de passe doit être différent de l'ancien.";
		}

		return null; // Les mots de passe sont différents
	}
}

