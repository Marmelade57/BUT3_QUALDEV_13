<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="s" uri="/struts-tags"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Changer le mot de passe</title>
<link rel="stylesheet" href="/_00_ASBank2025/style/style.css" />
</head>
<body>
	<div class="btnLogout">
		<s:form name="myForm" action="logout" method="POST">
			<s:submit name="Retour" value="Logout" />
		</s:form>
	</div>
	<h1>Changer le mot de passe</h1>
	
	<s:if test="message != null && !message.isEmpty()">
		<div class="message">
			<s:property value="message" />
		</div>
	</s:if>
	
	<p>
		Bienvenue <b><s:property value="connectedUser.prenom" /> <s:property
				value="connectedUser.nom" /></b> !
	</p>
	
	<s:form action="changerMotDePasseAction" method="POST">
		<s:password name="ancienMotDePasse" label="Ancien mot de passe" required="true" />
		<s:password name="nouveauMotDePasse" label="Nouveau mot de passe" required="true" />
		<s:password name="confirmationMotDePasse" label="Confirmation du nouveau mot de passe" required="true" />
		<s:submit value="Changer le mot de passe" />
	</s:form>
	
	<p>
		<s:if test="connectedUser instanceof com.iut.banque.modele.Gestionnaire">
			<s:url action="retourTableauDeBordManager" var="urlRetour" />
		</s:if>
		<s:else>
			<s:url action="retourTableauDeBordClient" var="urlRetour" />
		</s:else>
		<s:a href="%{urlRetour}">Retour au tableau de bord</s:a>
	</p>
</body>
<jsp:include page="/JSP/Footer.jsp" />
</html>

