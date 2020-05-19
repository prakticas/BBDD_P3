package conexionBD;
import java.io.FileReader;
import java.sql.SQLException;
import java.util.Properties;

import conexionBD.jdbc.ConectorJDBC;
import conexionBD.jdbc.ConfiguracionOracle;

/**
 * Clase de ejemplo de conexión y realización de consultas a una base de datos Oracle
 */
public class EjemploConexionOracle {

	/**
	 * Método principal del programa de ejemplo. Se conecta a una BD Oracle, crea una tabla, 
	 * la puebla, ejecuta una consulta y borra la tabla
	 */
	public static void main(String args[]) {
		ConectorJDBC oracle = null;
		Properties prop = new Properties();
		try {		
			prop.load(new FileReader("Oracle.properties"));
			oracle = configureOracle(prop);
			crearPoblarBD(oracle);
			consultarBD( oracle );
			borrarBD(oracle);	
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		} finally {
			if (oracle != null) oracle.disconnect();
		}
	}
	
	/**
	 * Método de conexión a la base de datos Oracle
	 */
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

	/**
	 * Método que crea y puebla la base de datos Oracle
	 */
	public static void crearPoblarBD( ConectorJDBC oracle ){
		oracle.executeSentence("CREATE TABLE Materia ("+
				"codigo NUMBER PRIMARY KEY, "+
			  	"curso NUMBER(1), "+
			  	"nombre VARCHAR(40))");
		oracle.executeSentence( "INSERT INTO Materia(codigo, curso, nombre) VALUES (9, 4, 'Vision por computador')" );
		oracle.executeSentence( "INSERT INTO Materia(codigo, curso, nombre) VALUES (10, 4, 'Metodologias agiles')" );
	}
	
	/**
	 * Método que consulta la base de datos Oracle
	 */
	public static void consultarBD( ConectorJDBC oracle ){
		oracle.executeQuery("SELECT curso, nombre from Materia");		
	}
	
	/**
	 * Método que borra base de datos Oracle
	 */
	public static void borrarBD( ConectorJDBC oracle ){
		oracle.executeSentence("DROP TABLE Materia");
	}
	
}