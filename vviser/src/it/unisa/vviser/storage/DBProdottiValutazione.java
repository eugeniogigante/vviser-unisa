package it.unisa.vviser.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import it.unisa.vviser.entity.ListaProdottiValutazione;
import it.unisa.vviser.entity.ProdottoValutazione;


/**
 * 
 * @author Giuseppe Sabato 
 *
 */
public class DBProdottiValutazione {
	
	/**
	 * Costruttore vuoto
	 */
	public DBProdottiValutazione()
	{
		
	}
	
	/**
	 * Metodo che permette di inserire nel database una lista di prodotti per valutazione
	 * @param prodottiValutazione lista prodotti da sottomettere a valutazione
	 * @param emailUtente identificativo dell'utente
	 * @param idEvento identificativo dell'evento di valutazione
	 * @throws SQLException
	 */
	public void insertProdottiVal(ArrayList<ProdottoValutazione> prodottiValutazione,String emailUtente,int idEvento) throws SQLException
	{
		Connection conn=null;
		PreparedStatement st=null;
		String query;
		
		ListaProdottiValutazione lp=new ListaProdottiValutazione(prodottiValutazione,emailUtente, idEvento,"");
        try 
        {
            conn = DBConnectionPool.getConnection();
            
            query="INSERT INTO "+DBNames.TABLE_LISTAVALUTAZIONE+"("
            		+DBNames.ATTR_LISTAVALUTAZIONE_UTENTE_EMAIL+","
            		+DBNames.ATTR_LISTAVALUTAZIONE_EVENTOVALUTAZIONE_ID+
            		") values (?,?)";
            
            st=conn.prepareStatement(query);
            st.setString(1, lp.getEmailUtente());
            st.setInt(2, lp.getIdEventoValutazione());
            
            st.executeUpdate();
            conn.commit();
            

            for(int i=0;i<lp.getListaProdottiValutazione().size();i++)
            {
            	
	            query= "INSERT INTO "+DBNames.TABLE_PRODOTTOLISTA+"("
	            		+DBNames.ATTR_PRODOTTOLISTA_PRODOTTO_ISBN+","
	            		+DBNames.ATTR_PRODOTTOLISTA_UTENTE_EMAIL+","
	            		+DBNames.ATTR_PRODOTTOLISTA_EVENTOVALUTAZIONE_ID+","
	            		+DBNames.ATTR_PRODOTTOLISTA_PRIORITA+
	            		") values (?,?,?,?)";
	            
	            st = conn.prepareStatement(query);
	            st.setString(1, lp.getListaProdottiValutazione().get(i).getIsbn());
	            st.setString(2, lp.getEmailUtente());
	            st.setInt(3, lp.getIdEventoValutazione());
	            st.setInt(4, lp.getListaProdottiValutazione().get(i).getPriority());
	
	            st.executeUpdate();
	            conn.commit();
	            
	            //Se in conflitto, inserisco il prodotto nella tabella dei prodotti
	            //in conflitto.
	            if(isInConflitto(lp.getListaProdottiValutazione().get(i),conn))
	            {
	            	query= "INSERT INTO "+DBNames.TABLE_PRODOTTOINCONFLITTO+"("
		            		+DBNames.ATTR_PRODOTTOINCONFLITTO_PRODOTTO_ISBN+","
		            		+DBNames.ATTR_PRODOTTOINCONFLITTO_UTENTE_EMAIL+","
		            		+DBNames.ATTR_PRODOTTOINCONFLITTO_EVENTOVALUTAZIONE_ID+
		            		") values (?,?,?)";
		            
		            st = conn.prepareStatement(query);
		            st.setString(1, lp.getListaProdottiValutazione().get(i).getIsbn());
		            st.setString(2, lp.getEmailUtente());
		            st.setInt(3, lp.getIdEventoValutazione());
		
		            st.executeUpdate();
		            conn.commit();
	            }
            }
        } 
        finally 
        {
            st.close();
            DBConnectionPool.releaseConnection(conn);
        }
	}
	
	/**
	 *Metodo che mostra i prodotti sottomessi a valutazione dell'utente in input
	 * @param emailUtente identificativo dell'utente
	 * @param idEvento identificativo dell'evento di valutazione
	 * @return listaProdottiValutazione lista prodotti sottomessi a valutazione dall'utente
	 * @throws SQLException
	 */
	public ListaProdottiValutazione showProdottiVal(String emailUtente,int idEvento) throws SQLException
	{
		Connection conn=null;
		Statement st=null;
		ResultSet ris=null;
		String query;
		ListaProdottiValutazione listaProdottiValutazione=new ListaProdottiValutazione(new ArrayList<ProdottoValutazione>(),emailUtente,idEvento,"");
		try
		{
			conn=DBConnectionPool.getConnection();
			query="SELECT "+DBNames.ATTR_PRODOTTO_ISBN+","
					+DBNames.ATTR_PRODOTTO_TITOLO+","
					+DBNames.ATTR_PRODOTTOLISTA_PRIORITA
					+ " FROM " +DBNames.TABLE_PRODOTTO+" "+DBNames.TABLE_PRODOTTOLISTA
					+ " WHERE "+DBNames.ATTR_PRODOTTO_ISBN+"="+DBNames.ATTR_PRODOTTOLISTA_PRODOTTO_ISBN
					+" and "+DBNames.ATTR_PRODOTTOLISTA_UTENTE_EMAIL+"="+listaProdottiValutazione.getEmailUtente()
					+" and "+DBNames.ATTR_PRODOTTOLISTA_EVENTOVALUTAZIONE_ID+"="+listaProdottiValutazione.getIdEventoValutazione();
			
			st=conn.createStatement();
			ris=st.executeQuery(query);
			while(ris.next())
			{
				String isbn=ris.getString(DBNames.ATTR_PRODOTTOLISTA_PRODOTTO_ISBN);
				String title=ris.getString(DBNames.ATTR_PRODOTTO_TITOLO);
				int priority=ris.getInt(DBNames.ATTR_PRODOTTOLISTA_PRIORITA);
				
				ProdottoValutazione p=new ProdottoValutazione(isbn,title,priority);
				listaProdottiValutazione.addProdottoValutazione(p);
			}
		}
		finally
		{
			st.close();
			DBConnectionPool.releaseConnection(conn);
		}
		
		return listaProdottiValutazione;
	}
	
	/**
	 *Metodo che permette di sostituire nel database un prodotto sottomesso a valutazione
	 *in conflitto con un altro prodotto
	 * @param lp lista prodotti sottomessi a valutazione che contiene il prodotto in conflitto
	 * @param pv prodotto sottomesso a valutazione in conflitto
	 * @param p prodotto per sostituire quello in conflitto
	 * @throws SQLException
	 */
	public void modifyProdVal(ListaProdottiValutazione lp, ProdottoValutazione pv/*, Prodotto p*/) throws SQLException
	{
		Connection conn=null;
		PreparedStatement st=null;
		String query;
		
		if(!lp.getBloccato())
		{
			try
			{
				conn=DBConnectionPool.getConnection();
				
				query="UPDATE "+DBNames.TABLE_PRODOTTOLISTA
						+" SET "+DBNames.ATTR_PRODOTTOLISTA_PRODOTTO_ISBN+"=?"
						+ " WHERE "+DBNames.ATTR_PRODOTTOLISTA_PRODOTTO_ISBN+"="+pv.getIsbn()
						+" and "+DBNames.ATTR_PRODOTTOLISTA_UTENTE_EMAIL+"="+lp.getEmailUtente()
						+" and "+DBNames.ATTR_PRODOTTOLISTA_EVENTOVALUTAZIONE_ID+"="+lp.getIdEventoValutazione();
				
				st=conn.prepareStatement(query);
				//st.setString(1, p.getIsbn()); //non ho ancora il beans Prodotto
				st.executeUpdate();
				conn.commit();
				
				//Cancello ex prodotto valutazione dalla tabella dei conflitti
				query="DELETE FROM "+DBNames.TABLE_PRODOTTOINCONFLITTO
						+" WHERE "+DBNames.ATTR_PRODOTTOINCONFLITTO_PRODOTTO_ISBN+"="+pv.getIsbn()
						+" and "+DBNames.ATTR_PRODOTTOLISTA_UTENTE_EMAIL+"="+lp.getEmailUtente()
						+" and "+DBNames.ATTR_PRODOTTOLISTA_EVENTOVALUTAZIONE_ID+"="+lp.getIdEventoValutazione();
				
				st=conn.prepareStatement(query);
				st.executeUpdate();
				conn.commit();
			}
			finally
			{
				st.close();
				DBConnectionPool.releaseConnection(conn);
			}
		}
		else
		{
			//Gestisci eccezione
		}
	}
	
	/**
	 *Metodo che controlla se il prodotto sottomesso a valutazione e' in conflitto
	 *con qualche altro prodotto, di un alto utente, sottomesso in precedenza
	 * @param p prodotto sottomesso a valutazione
	 * @param conn connessiona al database
	 * @return true se il prodotto e' in conflitto, false altrimenti
	 * @throws SQLException
	 */
	private boolean isInConflitto(ProdottoValutazione p, Connection conn) throws SQLException
	{
		Statement st1=null;
		ResultSet ris1=null;
		String query1;
		
		query1="SELECT count(*) as numeroConflitti"
				+" FROM "+DBNames.TABLE_PRODOTTOINCONFLITTO
				+" WHERE "+DBNames.ATTR_PRODOTTOINCONFLITTO_PRODOTTO_ISBN+"="
				+p.getIsbn();
		
		st1=conn.createStatement();
		ris1=st1.executeQuery(query1);
		ris1.next();
		if(ris1.getInt("numeroConflitti")==0)
		{
			st1.close();
			return false;
		}
		else
		{
			st1.close();
			return true;
		}
	}
	
	

}