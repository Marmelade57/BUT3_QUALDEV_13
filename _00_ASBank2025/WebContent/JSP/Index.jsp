<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="fr">
<script type="text/javascript">
	function DisplayMessage() {
		alert('Ce TD a été donné pour les 3A dans le cadre du cours de Qualité de Développements');
	}
</script>
<link rel="stylesheet" href="style/style.css" />
<link href="style/favicon.ico" rel="icon"
	type="image/x-icon" />
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Application IUT Bank</title>
</head>
<body>
	<h1>Bienvenue sur l'application IUT Bank 2025</h1>
	<p>
		<img
			src="https://www.mio.osupytheas.fr/wp-content/uploads/2026/01/unys-universite-de-lorraine.png.webp"
			alt="logo unys" style="background-color: white" />
	</p>
	<input type="button" value="Information" name="info"
		onClick="DisplayMessage()" />
	<p style="font-size: 2em">
		<s:url action="redirectionLogin" var="redirectionLogin" ></s:url>
		<s:a href="%{redirectionLogin}" class="bouton">Page de Login</s:a>
	</p>
</body>
<jsp:include page="/JSP/Footer.jsp" />
</html>