package com.iut.banque.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.transaction.annotation.Transactional;

import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.exceptions.IllegalOperationException;
import com.iut.banque.exceptions.TechnicalException;
import com.iut.banque.interfaces.IDao;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.Compte;
import com.iut.banque.modele.CompteAvecDecouvert;
import com.iut.banque.modele.CompteSansDecouvert;
import com.iut.banque.modele.Gestionnaire;
import com.iut.banque.modele.Utilisateur;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
/**
 * Implémentation de IDao utilisant Hibernate.
 *
 * Les transactions sont gérées par Spring via le transaction manager
 * défini dans l'application context.
 */
@Transactional
public class DaoHibernate implements IDao {
    private static final Logger LOGGER = Logger.getLogger(DaoHibernate.class.getName());
    private SessionFactory sessionFactory;

    public DaoHibernate() {
        LOGGER.info("==================");
        LOGGER.info("Création de la Dao");
    }

    /**
     * Setter pour la SessionFactory.
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public CompteAvecDecouvert createCompteAvecDecouvert(double solde, String numeroCompte, double decouvertAutorise,
                                                         Client client) throws TechnicalException, IllegalFormatException, IllegalOperationException {

        Session session = sessionFactory.getCurrentSession();
        CompteAvecDecouvert compte = session.get(CompteAvecDecouvert.class, numeroCompte);

        if (compte != null) {
            throw new TechnicalException("Numéro de compte déjà utilisé.");
        }

        compte = new CompteAvecDecouvert(numeroCompte, solde, decouvertAutorise, client);
        client.addAccount(compte);
        session.save(compte);

        return compte;
    }

    @Override
    public CompteSansDecouvert createCompteSansDecouvert(double solde, String numeroCompte, Client client)
            throws TechnicalException, IllegalFormatException {

        Session session = sessionFactory.getCurrentSession();
        CompteSansDecouvert compte = session.get(CompteSansDecouvert.class, numeroCompte);

        if (compte != null) {
            throw new TechnicalException("Numéro de compte déjà utilisé.");
        }

        compte = new CompteSansDecouvert(numeroCompte, solde, client);
        client.addAccount(compte);
        session.save(compte);

        return compte;
    }

    @Override
    public void updateAccount(Compte c) {
        sessionFactory.getCurrentSession().update(c);
    }

    @Override
    public void deleteAccount(Compte c) throws TechnicalException {
        if (c == null) {
            throw new TechnicalException("Ce compte n'existe plus");
        }
        sessionFactory.getCurrentSession().delete(c);
    }

    @Override
    public Map<String, Compte> getAccountsByClientId(String id) {
        Map<String, Compte> result = new HashMap<>();
        
        if (id == null || id.trim().isEmpty()) {
            LOGGER.warning("ID client null ou vide fourni à getAccountsByClientId");
            return result;
        }
        
        try {
            // La session est déjà dans une transaction gérée par Spring (@Transactional)
            Session session = sessionFactory.getCurrentSession();
            
            // Vérifier d'abord si l'utilisateur existe
            String checkUserHql = "SELECT COUNT(u) FROM Utilisateur u WHERE u.userId = :userId";
            Long userCount = session.createQuery(checkUserHql, Long.class)
                                 .setParameter("userId", id.trim())
                                 .uniqueResult();
            
            if (userCount == 0) {
                LOGGER.info("Aucun utilisateur trouvé avec l'ID: " + id);
                return result; // Retourne une map vide si l'utilisateur n'existe pas
            }
            
            // Si l'utilisateur existe, récupérer ses comptes
            String hql = "SELECT c FROM Compte c WHERE c.owner.userId = :userId";
            List<Compte> comptes = session.createQuery(hql, Compte.class)
                                       .setParameter("userId", id.trim())
                                       .getResultList();
            
            // Créer la map de résultat
            for (Compte compte : comptes) {
                if (compte != null && compte.getNumeroCompte() != null) {
                    result.put(compte.getNumeroCompte(), compte);
                }
            }
            
            return result;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des comptes du client " + id, e);
            return result; // Retourne une map vide en cas d'erreur
        }
    }

    @Override
    public Compte getAccountById(String id) {
        return sessionFactory.getCurrentSession().get(Compte.class, id);
    }

    @Override
    public Utilisateur createUser(String nom, String prenom, String adresse, boolean male, String userId,
                                  String userPwd, boolean manager, String numClient)
            throws TechnicalException, IllegalArgumentException, IllegalFormatException {

        Session session = sessionFactory.getCurrentSession();
        Utilisateur user = session.get(Utilisateur.class, userId);

        if (user != null) {
            throw new TechnicalException("User Id déjà utilisé.");
        }

        if (manager) {
            user = new Gestionnaire(nom, prenom, adresse, male, userId, userPwd);
        } else {
            user = new Client(nom, prenom, adresse, male, userId, userPwd, numClient);
        }

        session.save(user);
        return user;
    }

    @Override
    public void deleteUser(Utilisateur u) throws TechnicalException {
        if (u == null) {
            throw new TechnicalException("Cet utilisateur n'existe plus");
        }
        sessionFactory.getCurrentSession().delete(u);
    }

    @Override
    public void updateUser(Utilisateur u) {
        sessionFactory.getCurrentSession().update(u);
    }

    @Override
    public boolean isUserAllowed(String userId, String userPwd) {
        if (userId == null || userPwd == null) {
            return false;
        }

        userId = userId.trim();
        userPwd = userPwd.trim();

        if (userId.isEmpty() || userPwd.isEmpty()) {
            return false;
        }

        Session session = sessionFactory.getCurrentSession();
        Utilisateur user = session.get(Utilisateur.class, userId);

        return user != null && userPwd.equals(user.getUserPwd());
    }

    @Override
    public Utilisateur getUserById(String id) {
        return sessionFactory.getCurrentSession().get(Utilisateur.class, id);
    }

    @Override
    public Map<String, Client> getAllClients() {
        Session session = sessionFactory.getCurrentSession();

        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Client> cq = cb.createQuery(Client.class);
        Root<Client> root = cq.from(Client.class);
        cq.select(root);

        Query<Client> query = session.createQuery(cq);
        List<Client> results = query.getResultList();

        Map<String, Client> clients = new HashMap<>();
        for (Client client : results) {
            clients.put(client.getUserId(), client);
        }
        return clients;
    }

    @Override
    public Map<String, Gestionnaire> getAllGestionnaires() {
        Session session = sessionFactory.getCurrentSession();

        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Gestionnaire> cq = cb.createQuery(Gestionnaire.class);
        Root<Gestionnaire> root = cq.from(Gestionnaire.class);
        cq.select(root);

        Query<Gestionnaire> query = session.createQuery(cq);
        List<Gestionnaire> results = query.getResultList();

        Map<String, Gestionnaire> gestionnaires = new HashMap<>();
        for (Gestionnaire g : results) {
            gestionnaires.put(g.getUserId(), g);
        }
        return gestionnaires;
    }

    @Override
    public void disconnect() {
        LOGGER.info("Déconnexion de la DAO.");
    }
}
