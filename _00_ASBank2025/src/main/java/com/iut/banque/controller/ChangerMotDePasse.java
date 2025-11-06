package com.iut.banque.controller;

import org.apache.struts2.ServletActionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.opensymphony.xwork2.ActionSupport;
import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.modele.Utilisateur;

/**
 * Contrôleur pour gérer le changement de mot de passe d'un utilisateur connecté.
 */
public class ChangerMotDePasse extends ActionSupport {

	private static final long serialVersionUID = 1L;
	private String ancienMotDePasse;
	private String nouveauMotDePasse;
	private String confirmationMotDePasse;
	private String message;
	private BanqueFacade banque;

	/**
	 * Constructeur de la classe ChangerMotDePasse
	 */
	public ChangerMotDePasse() {
		System.out.println("In Constructor from ChangerMotDePasse class");
		ApplicationContext context = WebApplicationContextUtils
				.getRequiredWebApplicationContext(ServletActionContext.getServletContext());
		this.banque = (BanqueFacade) context.getBean("banqueFacade");
	}

	/**
	 * Méthode pour afficher la page de changement de mot de passe
	 * 
	 * @return String, "SUCCESS" pour afficher la page
	 */
	public String execute() {
		// Vérifier que l'utilisateur est connecté
		if (banque.getConnectedUser() == null) {
			return "ERROR";
		}
		return "SUCCESS";
	}

	/**
	 * Méthode pour effectuer le changement de mot de passe.
	 * Délègue la validation et le hashage à LoginManager pour éviter la duplication.
	 * 
	 * @return String, "SUCCESS" si le changement est réussi, "ERROR" sinon
	 */
	public String changerMotDePasse() {
		Utilisateur utilisateurConnecte = banque.getConnectedUser();

		// Vérifier que l'utilisateur est connecté
		if (utilisateurConnecte == null) {
			message = "Vous devez être connecté pour changer votre mot de passe.";
			return "ERROR";
		}

		// Validation de l'ancien mot de passe
		if (ancienMotDePasse == null || ancienMotDePasse.trim().isEmpty()) {
			message = "L'ancien mot de passe est requis.";
			return "ERROR";
		}

		// Validation du nouveau mot de passe avec la classe utilitaire
		String erreurValidation = ValidationMotDePasse.validerMotDePasse(nouveauMotDePasse);
		if (erreurValidation != null) {
			message = erreurValidation;
			return "ERROR";
		}

		// Validation de la confirmation
		String erreurConfirmation = ValidationMotDePasse.validerConfirmation(nouveauMotDePasse, confirmationMotDePasse);
		if (erreurConfirmation != null) {
			message = erreurConfirmation;
			return "ERROR";
		}

		// Vérifier que le nouveau mot de passe est différent de l'ancien
		String erreurDifference = ValidationMotDePasse.validerDifference(ancienMotDePasse, nouveauMotDePasse);
		if (erreurDifference != null) {
			message = erreurDifference;
			return "ERROR";
		}

		// Effectuer le changement de mot de passe
		// LoginManager se charge de :
		// - Vérifier l'ancien mot de passe (avec gestion du hash)
		// - Hasher le nouveau mot de passe
		// - Mettre à jour la base de données
		try {
			banque.changerMotDePasse(ancienMotDePasse, nouveauMotDePasse);
			message = "Votre mot de passe a été modifié avec succès.";
			return "SUCCESS";
		} catch (com.iut.banque.exceptions.TechnicalException e) {
			message = e.getMessage();
			return "ERROR";
		} catch (Exception e) {
			e.printStackTrace();
			message = "Une erreur est survenue lors du changement de mot de passe.";
			return "ERROR";
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
		return banque.getConnectedUser();
	}
}

