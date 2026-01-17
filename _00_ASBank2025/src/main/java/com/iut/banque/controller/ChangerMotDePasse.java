package com.iut.banque.controller;

import org.apache.struts2.ServletActionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.opensymphony.xwork2.ActionSupport;
import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.modele.Utilisateur;
import com.iut.banque.modele.Gestionnaire;

import java.util.logging.Logger;

/**
 * Contrôleur pour gérer le changement de mot de passe d'un utilisateur connecté.
 */
public class ChangerMotDePasse extends ActionSupport {
    private static final Logger LOGGER = Logger.getLogger(ChangerMotDePasse.class.getName());
    private static final String ERREUR = "ERROR";

	private static final long serialVersionUID = -5053207241589481509L;
	private String ancienMotDePasse;
	private String nouveauMotDePasse;
	private String confirmationMotDePasse;
	private String message;
	private transient BanqueFacade banque;

	/**
	 * Constructeur de la classe ChangerMotDePasse
	 */
	public ChangerMotDePasse() {
		LOGGER.info("In Constructor from ChangerMotDePasse class");
		try {
			ApplicationContext context = WebApplicationContextUtils
					.getRequiredWebApplicationContext(ServletActionContext.getServletContext());
			this.banque = (BanqueFacade) context.getBean("banqueFacade");
		} catch (Exception e) {
			// Environnements de test ou hors conteneur : laisser la facade à null
			LOGGER.warning("Impossible d'initialiser la facade Banque depuis le contexte web : " + e.getMessage());
			this.banque = null;
		}
	}
	
	/**
	 * Constructeur alternatif pour les tests unitaires
	 * @param banque La façade à utiliser pour ce contrôleur
	 */
	public ChangerMotDePasse(BanqueFacade banque) {
		this.banque = banque;
	}

	/**
	 * Méthode pour afficher la page de changement de mot de passe
	 * 
	 * @return String, "SUCCESS" pour afficher la page
	 */
    @Override
	public String execute() {
		// Vérifier que l'utilisateur est connecté
		if (getConnectedUser() == null) {
			return ERREUR;
		}
		return "SUCCESS";
	}

	/**
	 * Méthode pour effectuer le changement de mot de passe.
	 * Délègue la validation et le hashage à LoginManager pour éviter la duplication.
	 * 
	 * @return String, "SUCCESS" si le changement est réussi, "ERROR" (ERREUR) sinon
	 */
	public String changerMotDePasse() {
		Utilisateur utilisateurConnecte = getConnectedUser();

		// Vérifier que l'utilisateur est connecté
		if (utilisateurConnecte == null) {
			message = "Vous devez être connecté pour changer votre mot de passe.";
			return ERREUR;
		}

		// Validation de l'ancien mot de passe
		if (ancienMotDePasse == null || ancienMotDePasse.trim().isEmpty()) {
			message = "L'ancien mot de passe est requis.";
			return ERREUR;
		}

		// Validation du nouveau mot de passe avec la classe utilitaire
		String erreurValidation = ValidationMotDePasse.validerMotDePasse(nouveauMotDePasse);
		if (erreurValidation != null) {
			message = erreurValidation;
			return ERREUR;
		}

		// Validation de la confirmation
		String erreurConfirmation = ValidationMotDePasse.validerConfirmation(nouveauMotDePasse, confirmationMotDePasse);
		if (erreurConfirmation != null) {
			message = erreurConfirmation;
			return ERREUR;
		}

		// Vérifier que le nouveau mot de passe est différent de l'ancien
		String erreurDifference = ValidationMotDePasse.validerDifference(ancienMotDePasse, nouveauMotDePasse);
		if (erreurDifference != null) {
			message = erreurDifference;
			return ERREUR;
		}

		// Effectuer le changement de mot de passe
		// LoginManager se charge de :
		// - Vérifier l'ancien mot de passe (avec gestion du hash)
		// - Hasher le nouveau mot de passe
		// - Mettre à jour la base de données
			try {
				if (banque == null) {
					message = "Impossible de changer le mot de passe : service indisponible.";
					return ERREUR;
				}
				banque.changerMotDePasse(ancienMotDePasse, nouveauMotDePasse);
				message = "Votre mot de passe a été modifié avec succès.";
				
				// Vérifier si l'utilisateur est un gestionnaire
				Utilisateur utilisateur = banque.getConnectedUser();
				if (utilisateur instanceof Gestionnaire) {
					return "SUCCESSMANAGER";
				}
				return "SUCCESS";
		} catch (com.iut.banque.exceptions.TechnicalException e) {
			message = e.getMessage();
			return ERREUR;
		} catch (Exception e) {
			// Log l'exception complète pour le diagnostic, et retourne un message plus explicite
			LOGGER.severe("Erreur lors du changement de mot de passe : " + e.getMessage());
			// Inclure la cause si disponible
			if (e.getCause() != null) {
				LOGGER.severe("Cause: " + e.getCause());
			}
			// Conserver le message générique attendu par les tests/UI
			message = "Une erreur est survenue lors du changement de mot de passe.";
			return ERREUR;
		}
	}

	/**
	 * Getter pour l'ancien mot de passe
	 * 
	 * @return String, l'ancien mot de passe
	 */
	public String getAncienMotDePasse() {
		return ancienMotDePasse;
	}

	/**
	 * Setter pour l'ancien mot de passe
	 * 
	 * @param ancienMotDePasse
	 *            : String correspondant à l'ancien mot de passe
	 */
	public void setAncienMotDePasse(String ancienMotDePasse) {
		this.ancienMotDePasse = ancienMotDePasse;
	}

	/**
	 * Getter pour le nouveau mot de passe
	 * 
	 * @return String, le nouveau mot de passe
	 */
	public String getNouveauMotDePasse() {
		return nouveauMotDePasse;
	}

	/**
	 * Setter pour le nouveau mot de passe
	 * 
	 * @param nouveauMotDePasse
	 *            : String correspondant au nouveau mot de passe
	 */
	public void setNouveauMotDePasse(String nouveauMotDePasse) {
		this.nouveauMotDePasse = nouveauMotDePasse;
	}

	/**
	 * Getter pour la confirmation du mot de passe
	 * 
	 * @return String, la confirmation du mot de passe
	 */
	public String getConfirmationMotDePasse() {
		return confirmationMotDePasse;
	}

	/**
	 * Setter pour la confirmation du mot de passe
	 * 
	 * @param confirmationMotDePasse
	 *            : String correspondant à la confirmation du mot de passe
	 */
	public void setConfirmationMotDePasse(String confirmationMotDePasse) {
		this.confirmationMotDePasse = confirmationMotDePasse;
	}

	/**
	 * Getter pour le message
	 * 
	 * @return String, le message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Setter pour le message
	 * 
	 * @param message
	 *            : String correspondant au message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Getter pour l'utilisateur connecté
	 * 
	 * @return Utilisateur, l'utilisateur connecté
	 */
	public Utilisateur getConnectedUser() {
		if (banque == null) {
			return null;
		}
		return banque.getConnectedUser();
	}

	/**
	 * Setter pour injecter la facade (utile pour les tests ou l'injection Spring)
	 *
	 * @param banque la facade BanqueFacade
	 */
	public void setBanque(BanqueFacade banque) {
		this.banque = banque;
	}
}

