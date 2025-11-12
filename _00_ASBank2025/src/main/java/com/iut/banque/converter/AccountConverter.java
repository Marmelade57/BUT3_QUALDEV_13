package com.iut.banque.converter;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import org.apache.struts2.util.StrutsTypeConverter;

import com.opensymphony.xwork2.conversion.TypeConversionException;

import com.iut.banque.interfaces.IDao;
import com.iut.banque.modele.Compte;

/**
 * Cette classe contient des méthodes permettant de convertir un compte en
 * string et vice-versa. Elle est déclarée dans
 * «src/main/webapp/WEB-INF/classes/xwork-conversion.properties.
 * 
 * Grâce à cette classe il est possible de passer en paramêtre d'une action
 * Struts le numéro d'un compte (une string) et le contrôleur qui va
 * recevoir le paramêtre n'a besoin que d'un setter prenant un objet de type
 * Compte.
 */
public class AccountConverter extends StrutsTypeConverter {
    private static final Logger LOGGER = Logger.getLogger(AccountConverter.class.getName());

	/**
	 * DAO utilisée pour récuperer les objets correspondants à l'id passé en
	 * paramêtre de convertFromString.
	 * 
	 * Note : Ce champ est static car pour une raison qui nous échappe, le scope
	 * « singleton » du bean Spring utilisé pour l'injection n'est pas respecté.
	 * Ainsi, au chargement de l'application, trois objets de cette classe sont
	 * instanciés et seulement le premier a une DAO injectée correctement.
	 */
	private static final AtomicReference<IDao> DAO = new AtomicReference<>();

	/**
	 * Constructeur avec paramêtre pour le AccountConverter.
	 * 
	 * Utilisé pour l'injection de dépendance.
	 * 
	 * @param dao = AccountConverter.dao
	 */
	public AccountConverter(IDao dao) {
        DAO.compareAndSet(null, dao);
        LOGGER.info("=========================");
        LOGGER.info("Création du convertisseur de compte");
	}

	/**
	 * 	Constructeur sans paramêtre pour le AccountConverter
	 */
	public AccountConverter() {
		LOGGER.info("=========================");
		LOGGER.info("Création du convertisseur de compte");
	}

	/**
	 * Permet la conversion automatique par Struts d'un tableau de chaine vers
	 * un Compte
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Object convertFromString(Map context, String[] values, Class classe) {
        IDao localDao = DAO.get();
        if (localDao == null) {
            throw new IllegalStateException("DAO non initialisée pour AccountConverter");
        }
		Compte compte = localDao.getAccountById(values[0]);
		if (compte == null) {
			throw new TypeConversionException("Impossible de convertir la chaine suivante : " + values[0]);
		}
		return compte;
	}

	/**
	 * Permet la conversion automatique par Struts d'un compte vers une chaine.
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public String convertToString(Map context, Object value) {
		Compte compte = (Compte) value;
		return compte == null ? null : compte.getNumeroCompte();
	}

}
