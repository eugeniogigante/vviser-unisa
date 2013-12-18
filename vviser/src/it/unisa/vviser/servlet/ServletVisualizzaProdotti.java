package it.unisa.vviser.servlet;

import it.unisa.vviser.entity.Prodotto;
import it.unisa.vviser.storage.DBGestioneProdotto;
import it.unisa.vviser.storage.DBProdottiValutazione;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ServletVisualizzaProdotti extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private DBGestioneProdotto prodottiManager;
	private DBProdottiValutazione prodottiValutazioneManager;

	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);
		prodottiManager=new DBGestioneProdotto();
		prodottiValutazioneManager=DBProdottiValutazione.getInstance();
	}
	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
    }
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
		
		
		try 
		{
			HttpSession s = request.getSession();
			String emailUtente=(String)s.getAttribute("sessEmail");
			//System.out.println(emailUtente);
			//System.out.println(prodottiValutazioneManager);
			int numeroProdottiMax=prodottiValutazioneManager.getNumeroSottomissioniMax();
			ArrayList<Prodotto> prodotti=prodottiManager.visualizzaProdotti(emailUtente);
			
			//prova
			/*System.out.println(numeroProdottiMax);
			for(int i=0;i<prodotti.size();i++)
			{
				System.out.println(prodotti.get(i).getTitolo());
			}*/
			request.setAttribute("numProdMax", numeroProdottiMax);
			request.setAttribute("prod", prodotti);
			
			ServletContext sc = getServletContext();
			RequestDispatcher rd = sc.getRequestDispatcher("/selezProdotti.jsp");
			rd.forward(request,response); 
			
		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		
	}

}