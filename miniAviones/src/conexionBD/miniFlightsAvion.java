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
public class miniFlightsAvion {

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
			crearTablaDeAvion(oracle);
			
			// Esta llamada solo se tendria que hacer
			// si nos interesa borrar todo el contenido
			// de la tabla de peliculas en Oracle (por ejemplo
			// queremos volver a cargar todos los datos)
			borrarContenidoTablaDeAvion(oracle);
			
			// Seleccionamos los titulos y el ano de produccion
			// de las peliculas almacenadas en la tabla
			// "title" en la BD MySQL.
			// for(Cursor c: mysql.executeQueryAndGetCursor("SELECT title, production_year FROM title")) { // Se pega un buen rato insertando
			// mejor insertamos los 20 primeros resultados de la consulta siguiente...
			for(Cursor c: mysql.executeQueryAndGetCursor("SELECT tailnum, model, year, manufacturer from planes ")) {
				// De cada fila extraemos los datos y los procesamos. 
				// A continuacion los insertamos en la BD Oracle.
				System.out.println("Insertando "+c.getString("tailnum")+" - "+c.getString("model")+c.getInteger("year")+" - "+c.getString("manufacturer"));
				oracle.executeSentence("INSERT INTO AVION(MATRICULA,MODELO,AÑO,FABRICANTE) VALUES (?,?,?,?)", 
						c.getString("tailnum"), c.getString("model"), c.getInteger("year"), c.getString("manufacturer"));
		}
			
			// Finalmente listamos el contenido resultante
			oracle.executeQuery("SELECT * FROM AVION");
			
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
	
	private static void borrarContenidoTablaDeAvion(ConectorJDBC o) {
		StringBuffer sb = new StringBuffer();
		sb.append("TRUNCATE TABLE AVION");
		o.executeSentence(sb.toString());
	}
	
	private static void crearTablaDeAvion(ConectorJDBC o) {
		StringBuffer sb = new StringBuffer();
		sb.append("CREATE TABLE AVION(");
		sb.append("MATRICULA VARCHAR(10) PRIMARY KEY,");
		sb.append("MODELO VARCHAR(20),");
		sb.append("AÑO NUMBER(5),");
		sb.append("FABRICANTE VARCHAR(100)");
		sb.append(")");
		o.executeSentence(sb.toString());
	}
	
}