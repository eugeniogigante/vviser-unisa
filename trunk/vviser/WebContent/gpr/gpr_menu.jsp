<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
       <%-- 
    Author: Antonio De Piano
--%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>menu</title>
</head>
<body>
<input type="button" value="Profilo" class="pulsante" onclick="document.location.href='../gu/gu.jsp';">
		<input type="button" value="Prodotti" class="pulsante" onclick="document.location.href='ituoiprodotti.jsp';">
		<input type="button" value="Ricerca" class="pulsante" onclick="document.location.href='/vviser/gpr/RicercaPrivataProdotto.jsp';">
		<input type="button" value="Valutazione" class="pulsante" onclick="document.location.href='./altro.jsp';">
		<input type="button" value="Miur" class="pulsante" onclick="document.location.href='gpr_miur.jsp';">
		<input type="button" value="Guida" class="pulsante" onclick="document.location.href='./altro.jsp';">
		<input type="button" value="Logout" class="pulsante" onclick="document.location.href='../main/login.jsp';" style="background-color:tomato;">
</body>
</html>