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
public class desvio {

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
			crearDesvio(oracle);
			
			// Esta llamada solo se tendria que hacer
			// si nos interesa borrar todo el contenido
			// de la tabla de peliculas en Oracle (por ejemplo
			// queremos volver a cargar todos los datos)
			borrarContenidoTablaDesvio(oracle);
			
			// Seleccionamos los titulos y el ano de produccion
			// de las peliculas almacenadas en la tabla
			// "title" en la BD MySQL.
			// for(Cursor c: mysql.executeQueryAndGetCursor("SELECT title, production_year FROM title")) { // Se pega un buen rato insertando
			// mejor insertamos los 20 primeros resultados de la consulta siguiente...
			poblarDesvio1(oracle);
			poblarDesvio2(oracle);
			
			// Finalmente listamos el contenido resultante
			
			
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
	
	private static void borrarContenidoTablaDesvio(ConectorJDBC o) {
		StringBuffer sb = new StringBuffer();
		sb.append("TRUNCATE TABLE Desvio");
		o.executeSentence(sb.toString());
	}
	

	private static void poblarDesvio1(ConectorJDBC o) {
		
		o.executeSentence("insert into DESVIO(ID,NEWAEREOPUERTO,NEWAVION)\n" + 
				"select incidencia.id, auxiliar.DIV1AIRPORT,auxiliar.div1TailNum\n" + 
				"from incidencia \n" + 
				"inner join \n" + 
				"auxiliar\n" + 
				"ON incidencia.vuelo=auxiliar.id\n" + 
				"where incidencia.tipo='desviado1'");
	}
private static void poblarDesvio2(ConectorJDBC o) {
		
		o.executeSentence("insert into DESVIO(ID,NEWAEREOPUERTO,NEWAVION)\n" + 
				"select incidencia.id, auxiliar.div2airport,auxiliar.div2TailNum\n" + 
				"from incidencia \n" + 
				"inner join \n" + 
				"auxiliar\n" + 
				"ON incidencia.vuelo=auxiliar.id\n" + 
				"where incidencia.tipo='desviado2'");
	}

	private static void crearDesvio(ConectorJDBC o) {

		o.executeSentence("CREATE TABLE DESVIO(\n" + 
				"ID number(11) PRIMARY KEY,\n" + 
				"FOREIGN KEY(ID) REFERENCES INCIDENCIA(ID) ON DELETE CASCADE,\n" + 
				"NEWAVION REFERENCES avion(matricula) ON DELETE CASCADE,\n" + 
				"NEWAEREOPUERTO REFERENCES aeropuerto(ID) ON DELETE CASCADE\n" + 
				")");
	}

	
	
}