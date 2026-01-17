package com.iut.banque.test.facade;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.iut.banque.controller.HashMotDePasse;
import com.iut.banque.controller.HashMotDePasse.HashResult;
import com.iut.banque.exceptions.TechnicalException;
import com.iut.banque.facade.LoginManager;
import com.iut.banque.interfaces.IDao;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.Utilisateur;

public class TestsLoginManager {

    private LoginManager loginManager;
    private IDao daoMock;

    @Before
    public void setUp() throws Exception {
        loginManager = new LoginManager();
        daoMock = mock(IDao.class);
        loginManager.setDao(daoMock);
    }

    @Test(expected = TechnicalException.class)
    public void testChangerMotDePasseNoUser() throws TechnicalException {
        // Aucun utilisateur courant -> exception
        loginManager.setCurrentUser(null);
        loginManager.changerMotDePasse("old", "newPass123");
    }

    @Test(expected = TechnicalException.class)
    public void testChangerMotDePasseWrongOldPassword() throws Exception {
        // Utilisateur avec mot de passe en clair
        Client c = new Client("Nom","Prenom","Adresse",true,"a.test1","monAncien","0123456789");
        loginManager.setCurrentUser(c);
        // appel avec mauvais ancien mot de passe
        loginManager.changerMotDePasse("badOld", "NouveauMot123");
    }

    @Test
    public void testChangerMotDePasseSuccessPlainPassword() throws Exception {
        Client c = new Client("N","P","A",true,"a.test2","ancienPWD","0123456789");
        loginManager.setCurrentUser(c);
        // dao mock doit accepter updateUser
        doNothing().when(daoMock).updateUser(any(Utilisateur.class));

        loginManager.changerMotDePasse("ancienPWD", "NouveauMotDePasse1");

        // Vérifier que updateUser a été appelé
        verify(daoMock, times(1)).updateUser(any(Utilisateur.class));
        // L'utilisateur doit maintenant avoir un mot de passe hashé
        assertTrue(loginManager.getConnectedUser().isPasswordHashed());
        assertNotNull(loginManager.getConnectedUser().getPasswordSalt());
        assertNotNull(loginManager.getConnectedUser().getPasswordHash());
    }

    @Test
    public void testChangerMotDePasseSuccessHashedPassword() throws Exception {
        // Créer un utilisateur et stocker un mot de passe hashé
        Client c = new Client("N","P","A",true,"a.test3","oldClear","0123456789");
        // Générer un hash pour 'ancien'
        HashMotDePasse hasher = new HashMotDePasse();
        HashResult hr = hasher.hashPassword("ancien");
        c.setHashedPassword(hr.saltBase64, hr.hashBase64);

        loginManager.setCurrentUser(c);
        doNothing().when(daoMock).updateUser(any(Utilisateur.class));

        // appeler avec l'ancien mot de passe en clair
        loginManager.changerMotDePasse("ancien", "NouveauMotDePasse1");

        verify(daoMock, times(1)).updateUser(any(Utilisateur.class));
        assertTrue(loginManager.getConnectedUser().isPasswordHashed());
        assertNotNull(loginManager.getConnectedUser().getPasswordSalt());
        assertNotNull(loginManager.getConnectedUser().getPasswordHash());
    }

    @Test
    public void testHashVerify() throws Exception {
        HashMotDePasse hasher = new HashMotDePasse();
        HashResult hr = hasher.hashPassword("monSecret123");
        assertTrue(hasher.verifyPassword("monSecret123", hr.saltBase64, hr.hashBase64));
        // Vérifier que la validation échoue si le mot de passe fourni est incorrect
        assertFalse(hasher.verifyPassword("autre", hr.saltBase64, hr.hashBase64));
    }
}
