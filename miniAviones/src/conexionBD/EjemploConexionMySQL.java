package conexionBD;
import java.io.FileReader;
import java.util.Properties;

import conexionBD.jdbc.ConectorJDBC;
import conexionBD.jdbc.ConfiguracionMySQL;

/**
 * Clase de ejemplo de conexión y realización de consultas a una base de datos MySQL
 */
public class EjemploConexionMySQL {
	
	/**
	 * Método principal del programa de ejemplo. Se conecta a una BD Mysql y ejecuta una consulta
	 */
	public static void main(String args[]) {
		ConectorJDBC mysql = null;
		Properties prop = new Properties();
		try {
			prop.load(new FileReader("MySQL.properties"));
			mysql = configureMySQL(prop);
			consultaBD( mysql );			
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		} finally {
			if (mysql != null) mysql.disconnect();
		}
	}

	/**
	 * Método de conexión a la base de datos MySQL
	 */
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

	/**
	 * Método que consulta la base de datos Oracle
	 */
	private static void consultaBD(ConectorJDBC mysql){
		mysql.executeQuery("SELECT id, title, production_year FROM title WHERE 1990 <= production_year AND production_year <= 1999 ORDER BY production_year LIMIT 20");
	}
	
	
	
}