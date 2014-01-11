<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
   <%-- 
    Author: Romano Simone 0512101343
    		Antonio De Piano
--%>
<html>
<head>
	<title>VViSeR</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<link href="/vviser/css/stile1.css" rel="stylesheet" type="text/css"/>
	<link href="/vviser/css/stile2.css" rel="stylesheet" type="text/css"/>
</head>

<body>

<header id="container-header">
<!--  contiene il logo  -->
</header>	

<nav>
		<!-- Pagina contenente i bottoni del menu -->
		<%@ include file="gpr_menu.jsp" %>
</nav>

<section id="container-section">

	<section id="section-menu"> 
		<!-- Pagina contenente le funzionalità -->
		<%@ include file="gpr_funz.jsp" %>
    </section>

    <section id="section-main">
	    	
	    	<section id="gpr_content" style="overflow: scroll;">
	    		<nav class="navigation">
					<ul>
						<li>
							<a href="/vviser/gpr/gpr.jsp">Gestione prodotti</a>
						</li>
						<li>
							>> <a href="/vviser/gpr/ituoiprodotti.jsp">I miei prodotti</a>
						</li>
						<li>
							>> Modifica
						</li>
					</ul>
				</nav>
		    	<!--  Pagina contenente il contenuto -->
		    	<table id="modifyTable"  align="center">
		    		<tr><td><%@ include file="ModificaProdottoMain.jsp" %></td></tr>
		    	</table>		    	
	    	</section>
	    	
	    	<section id="gpr_fast_menu" style="overflow:scroll;">
	    	<%@ include file="gpr_fast_menu.jsp" %>
    		</section>
    
    </section>

</section>

<footer id="container-footer">
		<!--  Pagina contenete il messaggio da inglobare nel footer -->
		<%@ include file="../layout/footer.jsp" %>
</footer>

</body>
</html>