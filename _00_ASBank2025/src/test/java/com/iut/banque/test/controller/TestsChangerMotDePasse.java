package com.iut.banque.test.controller;

import com.iut.banque.controller.ChangerMotDePasse;
import com.iut.banque.exceptions.TechnicalException;
import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.Utilisateur;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests pour la classe ChangerMotDePasse.
 * Utilise une classe mock interne pour isoler le contrôleur de ses dépendances.
 */
public class TestsChangerMotDePasse {

	private ChangerMotDePasse changerMotDePasse;
	private MockBanqueFacade banqueFacadeMock;
	private Utilisateur utilisateurMock;

	/**
	 * Classe mock interne pour BanqueFacade.
	 */
	private static class MockBanqueFacade extends BanqueFacade {
		private Utilisateur connectedUser;
		private TechnicalException exceptionToThrow;
		private boolean changerMotDePasseCalled = false;
		private String ancienMotDePasseReceived;
		private String nouveauMotDePasseReceived;

		public MockBanqueFacade() {
			super(null, null);
		}

		@Override
		public Utilisateur getConnectedUser() {
			return connectedUser;
		}

		public void setConnectedUser(Utilisateur user) {
			this.connectedUser = user;
		}

		@Override
		public void changerMotDePasse(String ancienMotDePasse, String nouveauMotDePasse) throws TechnicalException {
			changerMotDePasseCalled = true;
			ancienMotDePasseReceived = ancienMotDePasse;
			nouveauMotDePasseReceived = nouveauMotDePasse;
			if (exceptionToThrow != null) {
				throw exceptionToThrow;
			}
		}

		public void setExceptionToThrow(TechnicalException exception) {
			this.exceptionToThrow = exception;
		}

		public boolean isChangerMotDePasseCalled() {
			return changerMotDePasseCalled;
		}

		public String getAncienMotDePasseReceived() {
			return ancienMotDePasseReceived;
		}

		public String getNouveauMotDePasseReceived() {
			return nouveauMotDePasseReceived;
		}
	}

	@Before
	public void setUp() {
		changerMotDePasse = new ChangerMotDePasse();
		banqueFacadeMock = new MockBanqueFacade();
		utilisateurMock = new Client("Doe", "John", "123 rue Test", true, "test.user", "password123", "123456789");

		// Utiliser la réflexion pour injecter le mock
		try {
			java.lang.reflect.Field field = ChangerMotDePasse.class.getDeclaredField("banque");
			field.setAccessible(true);
			field.set(changerMotDePasse, banqueFacadeMock);
		} catch (Exception e) {
			fail("Impossible d'injecter le mock de BanqueFacade : " + e.getMessage());
		}
	}

	@Test
	public void testExecuteUtilisateurConnecte() {
		banqueFacadeMock.setConnectedUser(utilisateurMock);

		String resultat = changerMotDePasse.execute();

		assertEquals("L'exécution doit retourner SUCCESS si l'utilisateur est connecté", "SUCCESS", resultat);
	}

	@Test
	public void testExecuteUtilisateurNonConnecte() {
		banqueFacadeMock.setConnectedUser(null);

		String resultat = changerMotDePasse.execute();

		assertEquals("L'exécution doit retourner ERROR si l'utilisateur n'est pas connecté", "ERROR", resultat);
	}

	@Test
	public void testChangerMotDePasseUtilisateurNonConnecte() {
		banqueFacadeMock.setConnectedUser(null);

		String resultat = changerMotDePasse.changerMotDePasse();

		assertEquals("Le changement doit échouer si l'utilisateur n'est pas connecté", "ERROR", resultat);
		assertEquals("Le message doit indiquer que l'utilisateur doit être connecté",
				"Vous devez être connecté pour changer votre mot de passe.", changerMotDePasse.getMessage());
	}

	@Test
	public void testChangerMotDePasseAncienMotDePasseNull() {
		banqueFacadeMock.setConnectedUser(utilisateurMock);
		changerMotDePasse.setAncienMotDePasse(null);
		changerMotDePasse.setNouveauMotDePasse("NouveauMotDePasse123");
		changerMotDePasse.setConfirmationMotDePasse("NouveauMotDePasse123");

		String resultat = changerMotDePasse.changerMotDePasse();

		assertEquals("Le changement doit échouer si l'ancien mot de passe est null", "ERROR", resultat);
		assertEquals("Le message doit indiquer que l'ancien mot de passe est requis",
				"L'ancien mot de passe est requis.", changerMotDePasse.getMessage());
	}

	@Test
	public void testChangerMotDePasseAncienMotDePasseVide() {
		banqueFacadeMock.setConnectedUser(utilisateurMock);
		changerMotDePasse.setAncienMotDePasse("   ");
		changerMotDePasse.setNouveauMotDePasse("NouveauMotDePasse123");
		changerMotDePasse.setConfirmationMotDePasse("NouveauMotDePasse123");

		String resultat = changerMotDePasse.changerMotDePasse();

		assertEquals("Le changement doit échouer si l'ancien mot de passe est vide", "ERROR", resultat);
		assertEquals("Le message doit indiquer que l'ancien mot de passe est requis",
				"L'ancien mot de passe est requis.", changerMotDePasse.getMessage());
	}

	@Test
	public void testChangerMotDePasseNouveauMotDePasseTropCourt() {
		banqueFacadeMock.setConnectedUser(utilisateurMock);
		changerMotDePasse.setAncienMotDePasse("AncienMotDePasse");
		changerMotDePasse.setNouveauMotDePasse("1234567");
		changerMotDePasse.setConfirmationMotDePasse("1234567");

		String resultat = changerMotDePasse.changerMotDePasse();

		assertEquals("Le changement doit échouer si le nouveau mot de passe est trop court", "ERROR", resultat);
		assertTrue("Le message doit mentionner la longueur minimale",
				changerMotDePasse.getMessage().contains("8 caractères"));
	}

	@Test
	public void testChangerMotDePasseConfirmationNonCorrespondante() {
		banqueFacadeMock.setConnectedUser(utilisateurMock);
		changerMotDePasse.setAncienMotDePasse("AncienMotDePasse");
		changerMotDePasse.setNouveauMotDePasse("NouveauMotDePasse123");
		changerMotDePasse.setConfirmationMotDePasse("AutreMotDePasse123");

		String resultat = changerMotDePasse.changerMotDePasse();

		assertEquals("Le changement doit échouer si la confirmation ne correspond pas", "ERROR", resultat);
		assertTrue("Le message doit indiquer que les mots de passe ne correspondent pas",
				changerMotDePasse.getMessage().contains("ne correspondent pas"));
	}

	@Test
	public void testChangerMotDePasseMemeMotDePasse() {
		banqueFacadeMock.setConnectedUser(utilisateurMock);
		changerMotDePasse.setAncienMotDePasse("MemeMotDePasse123");
		changerMotDePasse.setNouveauMotDePasse("MemeMotDePasse123");
		changerMotDePasse.setConfirmationMotDePasse("MemeMotDePasse123");

		String resultat = changerMotDePasse.changerMotDePasse();

		assertEquals("Le changement doit échouer si le nouveau mot de passe est identique à l'ancien", "ERROR",
				resultat);
		assertTrue("Le message doit indiquer que le nouveau mot de passe doit être différent",
				changerMotDePasse.getMessage().contains("différent"));
	}

	@Test
	public void testChangerMotDePasseSucces() {
		banqueFacadeMock.setConnectedUser(utilisateurMock);
		changerMotDePasse.setAncienMotDePasse("AncienMotDePasse");
		changerMotDePasse.setNouveauMotDePasse("NouveauMotDePasse123");
		changerMotDePasse.setConfirmationMotDePasse("NouveauMotDePasse123");

		String resultat = changerMotDePasse.changerMotDePasse();

		assertEquals("Le changement doit réussir avec des données valides", "SUCCESS", resultat);
		assertEquals("Le message doit indiquer le succès",
				"Votre mot de passe a été modifié avec succès.", changerMotDePasse.getMessage());
		assertTrue("changerMotDePasse doit avoir été appelé", banqueFacadeMock.isChangerMotDePasseCalled());
		assertEquals("L'ancien mot de passe doit être correctement passé", "AncienMotDePasse",
				banqueFacadeMock.getAncienMotDePasseReceived());
		assertEquals("Le nouveau mot de passe doit être correctement passé", "NouveauMotDePasse123",
				banqueFacadeMock.getNouveauMotDePasseReceived());
	}

	@Test
	public void testChangerMotDePasseTechnicalException() {
		banqueFacadeMock.setConnectedUser(utilisateurMock);
		changerMotDePasse.setAncienMotDePasse("AncienMotDePasse");
		changerMotDePasse.setNouveauMotDePasse("NouveauMotDePasse123");
		changerMotDePasse.setConfirmationMotDePasse("NouveauMotDePasse123");

		// Simuler une TechnicalException
		banqueFacadeMock.setExceptionToThrow(new TechnicalException("L'ancien mot de passe est incorrect"));

		String resultat = changerMotDePasse.changerMotDePasse();

		assertEquals("Le changement doit échouer si une TechnicalException est levée", "ERROR", resultat);
		assertEquals("Le message doit contenir le message de l'exception",
				"L'ancien mot de passe est incorrect", changerMotDePasse.getMessage());
	}

	@Test
	public void testChangerMotDePasseExceptionGenerique() {
		banqueFacadeMock.setConnectedUser(utilisateurMock);
		changerMotDePasse.setAncienMotDePasse("AncienMotDePasse");
		changerMotDePasse.setNouveauMotDePasse("NouveauMotDePasse123");
		changerMotDePasse.setConfirmationMotDePasse("NouveauMotDePasse123");

		// Créer un mock qui lève une RuntimeException (exception générique)
		MockBanqueFacade mockWithRuntimeException = new MockBanqueFacade() {
			@Override
			public void changerMotDePasse(String ancienMotDePasse, String nouveauMotDePasse)
					throws TechnicalException {
				// Lever une RuntimeException qui sera catchée comme Exception générique
				throw new RuntimeException("Erreur inattendue");
			}
		};
		mockWithRuntimeException.setConnectedUser(utilisateurMock);

		// Injecter le nouveau mock
		try {
			java.lang.reflect.Field field = ChangerMotDePasse.class.getDeclaredField("banque");
			field.setAccessible(true);
			field.set(changerMotDePasse, mockWithRuntimeException);
		} catch (Exception e) {
			fail("Impossible d'injecter le mock : " + e.getMessage());
		}

		String resultat = changerMotDePasse.changerMotDePasse();

		assertEquals("Le changement doit échouer si une exception est levée", "ERROR", resultat);
		assertEquals("Le message doit indiquer une erreur générique",
				"Une erreur est survenue lors du changement de mot de passe.", changerMotDePasse.getMessage());
	}

	@Test
	public void testGetConnectedUser() {
		banqueFacadeMock.setConnectedUser(utilisateurMock);

		Utilisateur resultat = changerMotDePasse.getConnectedUser();

		assertEquals("getConnectedUser doit retourner l'utilisateur de la facade", utilisateurMock, resultat);
	}

	@Test
	public void testGettersSetters() {
		changerMotDePasse.setAncienMotDePasse("Ancien");
		changerMotDePasse.setNouveauMotDePasse("Nouveau");
		changerMotDePasse.setConfirmationMotDePasse("Confirmation");
		changerMotDePasse.setMessage("Message");

		assertEquals("Le getter doit retourner la valeur du setter pour ancienMotDePasse", "Ancien",
				changerMotDePasse.getAncienMotDePasse());
		assertEquals("Le getter doit retourner la valeur du setter pour nouveauMotDePasse", "Nouveau",
				changerMotDePasse.getNouveauMotDePasse());
		assertEquals("Le getter doit retourner la valeur du setter pour confirmationMotDePasse", "Confirmation",
				changerMotDePasse.getConfirmationMotDePasse());
		assertEquals("Le getter doit retourner la valeur du setter pour message", "Message",
				changerMotDePasse.getMessage());
	}
}

