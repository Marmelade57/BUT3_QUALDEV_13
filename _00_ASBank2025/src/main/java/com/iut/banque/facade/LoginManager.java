package com.iut.banque.facade;

import com.iut.banque.constants.LoginConstants;
import com.iut.banque.controller.HashMotDePasse;
import com.iut.banque.controller.HashMotDePasse.HashResult;
import com.iut.banque.exceptions.TechnicalException;
import com.iut.banque.interfaces.IDao;
import com.iut.banque.modele.Gestionnaire;
import com.iut.banque.modele.Utilisateur;

public class LoginManager {

	private IDao dao;
	private Utilisateur user;

	/**
	 * Setter pour la DAO.
	 * 
	 * Utilisé par Spring par Injection de Dependence
	 * 
	 * @param dao
	 *            : la dao nécessaire pour le LoginManager
	 */
	public void setDao(IDao dao) {
		this.dao = dao;
	}

	/**
	 * Méthode pour permettre la connection de l'utilisateur via un login en
	 * confrontant le mdp d'un utilisateur de la base de données avec le mot de
	 * passe donné en paramètre
	 * 
	 * @param userCde
	 *            : un String correspondant au userID de l'utilisateur qui
	 *            cherche à se connecter
	 * @param userPwd
	 *            : un String correspondant au mot de passe qui doit être
	 *            confronté avec celui de la base de données
	 * @return int correspondant aux constantes LoginConstants pour inforer de
	 *         l'état du login
	 */
	public int tryLogin(String userCde, String userPwd) {
		if (dao.isUserAllowed(userCde, userPwd)) {
			user = dao.getUserById(userCde);
			if (user instanceof Gestionnaire) {
				return LoginConstants.MANAGER_IS_CONNECTED;
			} else {
				return LoginConstants.USER_IS_CONNECTED;
			}
		} else {
			return LoginConstants.LOGIN_FAILED;
		}
	}

	/**
	 * Getter pour avoir l'objet Utilisateur de celui qui est actuellement
	 * connecté à l'application
	 * 
	 * @return Utilisateur : l'objet Utilisateur de celui qui est connecté
	 */
	public Utilisateur getConnectedUser() {
		return user;
	}

	/**
	 * Setter pour changer l'utilisateur actuellement connecté à l'application
	 * 
	 * @param user
	 *            : un objet de type Utilisateur (Client ou Gestionnaire) que
	 *            l'on veut définir comme utilisateur actuellement connecté à
	 *            l'application
	 */
	public void setCurrentUser(Utilisateur user) {
		this.user = user;
	}

	/**
	 * Remet l'utilisateur à null et déconnecte la DAO.
	 */
	public void logout() {
		this.user = null;
		dao.disconnect();
	}

	/**
	 * Méthode pour changer le mot de passe de l'utilisateur connecté.
	 * Le nouveau mot de passe sera hashé avec PBKDF2 avant d'être stocké.
	 * 
	 * @param ancienMotDePasse
	 *            : String correspondant à l'ancien mot de passe pour vérification
	 * @param nouveauMotDePasse
	 *            : String correspondant au nouveau mot de passe (sera hashé)
	 * @throws TechnicalException
	 *             si l'utilisateur n'est pas connecté ou si l'ancien mot de passe est incorrect
	 */
	public void changerMotDePasse(String ancienMotDePasse, String nouveauMotDePasse) throws TechnicalException {
		if (user == null) {
			throw new TechnicalException("Aucun utilisateur connecté");
		}

		// Vérifier que l'ancien mot de passe est correct
		if (!verifierMotDePasse(ancienMotDePasse, user)) {
			throw new TechnicalException("L'ancien mot de passe est incorrect");
		}

		// Hasher le nouveau mot de passe
		HashMotDePasse hashMotDePasse = new HashMotDePasse();
		HashResult hashResult = hashMotDePasse.hashPassword(nouveauMotDePasse);

		// Stocker le sel et le hash au format "salt:hash"
		user.setHashedPassword(hashResult.saltBase64, hashResult.hashBase64);
		dao.updateUser(user);
	}

	/**
	 * Vérifie si un mot de passe correspond à celui stocké pour l'utilisateur.
	 * Gère à la fois les mots de passe hashés et en clair (rétrocompatibilité).
	 * 
	 * @param motDePasse
	 *            : le mot de passe à vérifier
	 * @param utilisateur
	 *            : l'utilisateur dont on veut vérifier le mot de passe
	 * @return true si le mot de passe est correct, false sinon
	 */
	private boolean verifierMotDePasse(String motDePasse, Utilisateur utilisateur) throws TechnicalException {
		if (utilisateur.isPasswordHashed()) {
			// Mot de passe hashé : utiliser verifyPassword
			HashMotDePasse hashMotDePasse = new HashMotDePasse();
			return hashMotDePasse.verifyPassword(motDePasse, utilisateur.getPasswordSalt(),
					utilisateur.getPasswordHash());
		} else {
			// Mot de passe en clair (ancien format) : comparaison directe
			return motDePasse.equals(utilisateur.getUserPwd());
		}
	}
}
