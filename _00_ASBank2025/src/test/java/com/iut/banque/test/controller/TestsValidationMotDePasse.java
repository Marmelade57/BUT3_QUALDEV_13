package com.iut.banque.test.controller;

import com.iut.banque.controller.ValidationMotDePasse;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests pour la classe ValidationMotDePasse.
 * Teste toutes les méthodes de validation des mots de passe.
 */
public class TestsValidationMotDePasse {

	@Test
	public void testValiderMotDePasseValide() {
		String resultat = ValidationMotDePasse.validerMotDePasse("MotDePasse123");
		assertNull("Un mot de passe valide ne doit pas retourner d'erreur", resultat);
	}

	@Test
	public void testValiderMotDePasseNull() {
		String resultat = ValidationMotDePasse.validerMotDePasse(null);
		assertNotNull("Un mot de passe null doit retourner une erreur", resultat);
		assertEquals("Le message d'erreur doit indiquer que le mot de passe est requis",
				"Le mot de passe est requis.", resultat);
	}

	@Test
	public void testValiderMotDePasseVide() {
		String resultat = ValidationMotDePasse.validerMotDePasse("");
		assertNotNull("Un mot de passe vide doit retourner une erreur", resultat);
		assertEquals("Le message d'erreur doit indiquer que le mot de passe est requis",
				"Le mot de passe est requis.", resultat);
	}

	@Test
	public void testValiderMotDePasseAvecEspaces() {
		String resultat = ValidationMotDePasse.validerMotDePasse("   ");
		assertNotNull("Un mot de passe avec seulement des espaces doit retourner une erreur", resultat);
		assertEquals("Le message d'erreur doit indiquer que le mot de passe est requis",
				"Le mot de passe est requis.", resultat);
	}

	@Test
	public void testValiderMotDePasseTropCourt() {
		String resultat = ValidationMotDePasse.validerMotDePasse("1234567");
		assertNotNull("Un mot de passe de moins de 8 caractères doit retourner une erreur", resultat);
		assertTrue("Le message d'erreur doit mentionner la longueur minimale",
				resultat.contains("8 caractères"));
	}

	@Test
	public void testValiderMotDePasseLongueurMinimale() {
		String resultat = ValidationMotDePasse.validerMotDePasse("12345678");
		assertNull("Un mot de passe de 8 caractères doit être valide", resultat);
	}

	@Test
	public void testValiderConfirmationValide() {
		String resultat = ValidationMotDePasse.validerConfirmation("MotDePasse123", "MotDePasse123");
		assertNull("Une confirmation correcte ne doit pas retourner d'erreur", resultat);
	}

	@Test
	public void testValiderConfirmationNull() {
		String resultat = ValidationMotDePasse.validerConfirmation("MotDePasse123", null);
		assertNotNull("Une confirmation null doit retourner une erreur", resultat);
		assertEquals("Le message d'erreur doit indiquer que la confirmation est requise",
				"La confirmation du mot de passe est requise.", resultat);
	}

	@Test
	public void testValiderConfirmationVide() {
		String resultat = ValidationMotDePasse.validerConfirmation("MotDePasse123", "");
		assertNotNull("Une confirmation vide doit retourner une erreur", resultat);
		assertEquals("Le message d'erreur doit indiquer que la confirmation est requise",
				"La confirmation du mot de passe est requise.", resultat);
	}

	@Test
	public void testValiderConfirmationNonCorrespondante() {
		String resultat = ValidationMotDePasse.validerConfirmation("MotDePasse123", "AutreMotDePasse");
		assertNotNull("Une confirmation non correspondante doit retourner une erreur", resultat);
		assertEquals("Le message d'erreur doit indiquer que les mots de passe ne correspondent pas",
				"Le mot de passe et la confirmation ne correspondent pas.", resultat);
	}

	@Test
	public void testValiderDifferenceMotsDePasseDifferents() {
		String resultat = ValidationMotDePasse.validerDifference("AncienMotDePasse", "NouveauMotDePasse");
		assertNull("Des mots de passe différents ne doivent pas retourner d'erreur", resultat);
	}

	@Test
	public void testValiderDifferenceMotsDePasseIdentiques() {
		String resultat = ValidationMotDePasse.validerDifference("MemeMotDePasse", "MemeMotDePasse");
		assertNotNull("Des mots de passe identiques doivent retourner une erreur", resultat);
		assertEquals("Le message d'erreur doit indiquer que le nouveau mot de passe doit être différent",
				"Le nouveau mot de passe doit être différent de l'ancien.", resultat);
	}

	@Test
	public void testValiderDifferenceAncienMotDePasseNull() {
		String resultat = ValidationMotDePasse.validerDifference(null, "NouveauMotDePasse");
		assertNull("Si l'ancien mot de passe est null, la validation doit passer", resultat);
	}

	@Test
	public void testValiderDifferenceNouveauMotDePasseNull() {
		String resultat = ValidationMotDePasse.validerDifference("AncienMotDePasse", null);
		assertNull("Si le nouveau mot de passe est null, la validation doit passer", resultat);
	}
}

