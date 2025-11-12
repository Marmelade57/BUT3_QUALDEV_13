package com.iut.banque.modele;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import com.iut.banque.exceptions.IllegalFormatException;

import java.io.Serializable;

/**
 * Classe représentant un utilisateur quelconque.
 * 
 * La stratégie d'héritage choisie est SINGLE_TABLE. Cela signifie que tous les
 * objets de cette classe et des classes filles sont enregistrés dans une seule
 * table dans la base de donnée. Les champs non utilisés par la classe sont
 * NULL.
 * 
 * Lors d'un chargement d'un objet appartenant à une des classes filles, le type
 * de l'objet est choisi grâce à la colonne "type" (c'est une colonne de
 * discrimination).
 */
@Entity
@Table(name = "Utilisateur")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING, length = 15)
public abstract class Utilisateur implements Serializable {
    private static final long serialVersionUID = -3339760784363529058L;
	/**
	 * L'identifiant (unique) de l'utilisateur.
	 * 
	 * Correspond à la clé primaire de l'utilisateur dans la BDD.
	 */
	@Id
	@Column(name = "userId")
	private String userId;

	/**
	 * Le mot de passe de l'utilisateur.
	 * 
	 */
	@Column(name = "userPwd")
	private String userPwd;

	/**
	 * Le nom de l'utilisateur.
	 */
	@Column(name = "nom")
	private String nom;

	/**
	 * Le prénom de l'utilisateur.
	 */
	@Column(name = "prenom")
	private String prenom;

	/**
	 * L'adresse physique de l'utilisateur.
	 */
	@Column(name = "adresse")
	private String adresse;

	/**
	 * Le sexe de l'utilisateur. Vrai si c'est un homme faux sinon.
	 */
	@Column(name = "male")
	private boolean male;

	/**
	 * @return String, le nom de l'utilisateur.
	 */
	public String getNom() {
		return nom;
	}

	/**
	 * @param nom
	 *            : le nom de l'utilisateur
	 */
	protected void setNom(String nom) {
		this.nom = nom;
	}

	/**
	 * @return String, le prénom de l'utilisateur
	 */
	public String getPrenom() {
		return prenom;
	}

	/**
	 * @param prenom
	 *            : le prénom de l'utilisateur
	 */
	protected void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	/**
	 * @return String, l'adresse physique de l'utilisateur
	 */
	public String getAdresse() {
		return adresse;
	}

	/**
	 * @param adresse
	 *            : l'adresse physique de l'utilisateur
	 */
	protected void setAdresse(String adresse) {
		this.adresse = adresse;
	}

	/**
	 * @return male : vrai si l'utilisateur est un homme, faux sinon
	 */
	public boolean isMale() {
		return male;
	}

	/**
	 * @param male
	 *            : vrai si l'utilisateur est un homme, faux sinon
	 */
	protected void setMale(boolean male) {
		this.male = male;
	}

	/**
	 * @return userId : l'identifiant de l'utilisateur
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            : l'identifiant de l'utilisateur
	 * @throws IllegalFormatException
	 */
    protected void setUserId(String userId) throws IllegalFormatException {
        if (!userId.matches("^[a-z]\\.[a-z]+\\d+$")) {
            throw new IllegalFormatException("Invalid userId format: " + userId);
        }
        this.userId = userId;
    }

	/**
	 * @return userPwd : le mot de passe de l'utilisateur
	 */
	public String getUserPwd() {
		return userPwd;
	}

	/**
	 * @param userPwd
	 *            : le mot de passe de l'utilisateur
	 */
	protected void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

	/**
	 * Méthode publique pour changer le mot de passe de l'utilisateur.
	 * 
	 * @param nouveauMotDePasse
	 *            : le nouveau mot de passe (sera hashé)
	 */
	public void changerMotDePasse(String nouveauMotDePasse) {
		this.userPwd = nouveauMotDePasse;
	}

	/**
	 * Détermine si le mot de passe stocké est au format hashé (salt:hash).
	 * 
	 * @return true si le format est hashé, false sinon (mot de passe en clair)
	 */
	public boolean isPasswordHashed() {
		if (userPwd == null) {
			return false;
		}
		// Format hashé : "saltBase64:hashBase64"
		return userPwd.contains(":") && userPwd.split(":").length == 2;
	}

	/**
	 * Récupère le sel du mot de passe hashé.
	 * 
	 * @return le sel en Base64, ou null si le mot de passe n'est pas hashé
	 */
	public String getPasswordSalt() {
		if (!isPasswordHashed()) {
			return null;
		}
		return userPwd.split(":")[0];
	}

	/**
	 * Récupère le hash du mot de passe.
	 * 
	 * @return le hash en Base64, ou null si le mot de passe n'est pas hashé
	 */
	public String getPasswordHash() {
		if (!isPasswordHashed()) {
			return null;
		}
		return userPwd.split(":")[1];
	}

	/**
	 * Définit le mot de passe au format hashé (salt:hash).
	 * 
	 * @param saltBase64
	 *            : le sel en Base64
	 * @param hashBase64
	 *            : le hash en Base64
	 */
	public void setHashedPassword(String saltBase64, String hashBase64) {
		this.userPwd = saltBase64 + ":" + hashBase64;
	}

	/**
	 * Constructeur de Utilisateur avec tous les champs de la classe comme
	 * paramètres.
	 * 
	 * Il est préférable d'utiliser une classe implémentant IDao pour créer un
	 * objet au lieu d'appeler ce constructeur.
	 * 
	 * @param nom
	 * @param prenom
	 * @param adresse
	 * @param male
	 * @param userId
	 * @param userPwd
	 */
	protected Utilisateur(String nom, String prenom, String adresse, boolean male, String userId, String userPwd) {
		super();
		this.nom = nom;
		this.prenom = prenom;
		this.adresse = adresse;
		this.male = male;
		this.userId = userId;
		this.userPwd = userPwd;
	}

	/**
	 * Constructeur sans paramètre de Utilisateur.
	 * 
	 * Nécessaire pour Hibernate.
	 *
	 * Il est préférable d'utiliser une classe implémentant IDao pour créer un
	 * objet au lieu d'appeler ce constructeur.
	 */
	protected Utilisateur() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Utilisateur [userId=" + userId + ", nom=" + nom + ", prenom=" + prenom + ", adresse=" + adresse
				+ ", male=" + male + ", userPwd=" + userPwd + "]";
	}

}
