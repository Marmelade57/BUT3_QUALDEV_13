package com.iut.banque.controller;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.iut.banque.exceptions.IllegalFormatException;
import com.iut.banque.exceptions.InsufficientFundsException;
import com.iut.banque.facade.BanqueFacade;
import com.iut.banque.modele.Client;
import com.iut.banque.modele.Compte;
import com.iut.banque.modele.Gestionnaire;
import com.opensymphony.xwork2.ActionSupport;

public class DetailCompte extends ActionSupport {

    private static final long serialVersionUID = 1L;

    // ✅ Constante pour éviter la duplication
    private static final String NEGATIVE_AMOUNT = "NEGATIVEAMOUNT";

    // ✅ Logger au lieu de System.out
    private static final Logger LOGGER = LoggerFactory.getLogger(DetailCompte.class);

    protected transient BanqueFacade banque;

    private String montant;
    private String error;
    protected Compte compte;

    public DetailCompte() {
        LOGGER.info("In Constructor from DetailCompte class");
        ApplicationContext context = WebApplicationContextUtils
                .getRequiredWebApplicationContext(ServletActionContext.getServletContext());
        this.banque = (BanqueFacade) context.getBean("banqueFacade");
    }

    public String getError() {
        switch (error) {
            case "TECHNICAL":
                return "Erreur interne. Verifiez votre saisie puis réessayer. Contactez votre conseiller si le problème persiste.";
            case "BUSINESS":
                return "Fonds insuffisants.";
            case NEGATIVE_AMOUNT:
                return "Veuillez rentrer un montant positif.";
            case "NEGATIVEOVERDRAFT":
                return "Veuillez rentrer un découvert positif.";
            case "INCOMPATIBLEOVERDRAFT":
                return "Le nouveau découvert est incompatible avec le solde actuel.";
            default:
                return "";
        }
    }

    public void setError(String error) {
        if (error == null) {
            this.error = "EMPTY";
        } else {
            this.error = error;
        }
    }

    public String getMontant() {
        return montant;
    }

    public void setMontant(String montant) {
        this.montant = montant;
    }

    public Compte getCompte() {
        Object user = banque.getConnectedUser();
        if (user instanceof Gestionnaire
                || (user instanceof Client && ((Client) user).getAccounts().containsKey(compte.getNumeroCompte()))) {
            return compte;
        }
        return null;
    }


    public void setCompte(Compte compte) {
        this.compte = compte;
    }

    // ✅ Méthode factorisée pour éviter la duplication
    private String processOperation(boolean isDebit) {
        Compte currentCompte = getCompte();
        try {
            double value = Double.parseDouble(montant.trim());
            if (isDebit) {
                banque.debiter(currentCompte, value);
            } else {
                banque.crediter(currentCompte, value);
            }
            return "SUCCESS";
        } catch (NumberFormatException e) {
            LOGGER.error("Invalid number format", e);
            return "ERROR";
        } catch (InsufficientFundsException ife) {
            LOGGER.warn("Insufficient funds", ife);
            return "NOTENOUGHFUNDS";
        } catch (IllegalFormatException e) {
            LOGGER.warn("Negative amount", e);
            return NEGATIVE_AMOUNT;
        }
    }

    public String debit() {
        return processOperation(true);
    }

    public String credit() {
        return processOperation(false);
    }
}
