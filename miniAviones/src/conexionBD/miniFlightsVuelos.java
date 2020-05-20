package conexionBD;
import java.io.FileReader;
import java.sql.SQLException;
import java.util.Properties;

import conexionBD.jdbc.Cursor;
import conexionBD.jdbc.ConectorJDBC;
import conexionBD.jdbc.ConfiguracionMySQL;
import conexionBD.jdbc.ConfiguracionOracle;

/**
 * Esta clase lee datos de la tabla "title" en MySQL, los procesa y 
 * los inserta en la tabla "peliculas_ejemplo" en BD Oracle.
 */
public class miniFlightsVuelos {

	public static void main(String args[]) {
		ConectorJDBC oracle = null;
		ConectorJDBC mysql = null;	
		Properties prop = new Properties();
		
		try {
			
			prop.load(new FileReader("Oracle.properties"));
			oracle = configureOracle(prop);
			
			prop.load(new FileReader("MySQL.properties"));
			mysql = configureMySQL(prop);
			
			// Esta llamada solo se tendria que hacer 
			// si no existe la tabla de peliculas en Oracle
			crearTablaDeVuelos(oracle);
			
			// Esta llamada solo se tendria que hacer
			// si nos interesa borrar todo el contenido
			// de la tabla de peliculas en Oracle (por ejemplo
			// queremos volver a cargar todos los datos)
			borrarContenidoTablaDeVuelos(oracle);
			
			// Seleccionamos los titulos y el ano de produccion
			// de las peliculas almacenadas en la tabla
			// "title" en la BD MySQL.
			// for(Cursor c: mysql.executeQueryAndGetCursor("SELECT title, production_year FROM title")) { // Se pega un buen rato insertando
			// mejor insertamos los 20 primeros resultados de la consulta siguiente...
			for(Cursor c: mysql.executeQueryAndGetCursor("SELECT rank,flightDate,origin,dest,carrier,tailNum,carrierDelay FROM (SELECT t.*, @rownum := @rownum + 1 AS rank FROM miniFlights.flights200810 t,(SELECT @rownum := 0) r) o")) {
				// De cada fila extraemos los datos y los procesamos. 
				// A continuacion los insertamos en la BD Oracle.
				System.out.println("Insertando "+" - "+c.getInteger("rank")+" - "+ c.getString("flightDate")+ " - "+c.getString("origin")+" - "+ c.getString("dest")+ " - "+c.getString("carrier")+" - "+ c.getString("tailNum")+" - "+ c.getInteger("carrierDelay"));
				oracle.executeSentence("INSERT INTO VUELO(IDV,FECHA,ORIGEN,DESTINO,AEROLINEA,TRANSPORTE,RETRASO) VALUES (?,?,?,?,?,?,?)", 
						c.getInteger("rank"), c.getString("flightDate"), c.getString("origin"), c.getString("dest"), c.getString("carrier"), c.getString("tailNum"), c.getInteger("carrierDelay"));
		}
			
			// Finalmente listamos el contenido resultante
			oracle.executeQuery("SELECT * FROM VUELO");
			
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		} finally {
			if (oracle != null) oracle.disconnect();
			if (mysql != null) mysql.disconnect();
		}
	}

	private static ConectorJDBC configureMySQL(Properties prop) throws Exception {
		ConectorJDBC mysql;
		mysql = new ConectorJDBC(new ConfiguracionMySQL(
				prop.getProperty("database.host"),
				prop.getProperty("database.port"),
				prop.getProperty("database.sid")),
				prop.getProperty("database.user"),
				prop.getProperty("database.password"));
		mysql.connect();
		System.out.println("Conectado a " + mysql);
		return mysql;
	}
	
	private static ConectorJDBC configureOracle(Properties prop)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException {
		ConectorJDBC oracle;
		oracle = new ConectorJDBC(new ConfiguracionOracle(
				prop.getProperty("database.host"),
				prop.getProperty("database.port"),
				prop.getProperty("database.sid")),
				prop.getProperty("database.user"),
				prop.getProperty("database.password"));
		oracle.connect();
		System.out.println("Conectado a " + oracle);
		return oracle;
	}
	
	private static void borrarContenidoTablaDeVuelos(ConectorJDBC o) {
		StringBuffer sb = new StringBuffer();
		sb.append("TRUNCATE TABLE VUELO");
		o.executeSentence(sb.toString());
	}
	
	private static void crearTablaDeVuelos(ConectorJDBC o) {
		StringBuffer sb = new StringBuffer();
		sb.append("CREATE TABLE VUELO(");
		sb.append("IDV number(11) PRIMARY KEY,");
		sb.append("FECHA DATE,");
		sb.append("ORIGEN REFERENCES AEROPUERTO(ID) ON DELETE CASCADE,");
		sb.append("DESTINO REFERENCES AEROPUERTO(ID) ON DELETE CASCADE,");
		sb.append("AEROLINEA REFERENCES AEROLINEA(ID) ON DELETE CASCADE,");
		sb.append("TRANSPORTE REFERENCES AVION(MATRICULA) ON DELETE CASCADE,");
		sb.append("RETRASO number(4)");
		sb.append(")");
		o.executeSentence(sb.toString());
	}
}