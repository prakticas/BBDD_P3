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
public class incidencias {

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
			crearIncidencia(oracle);
			
			// Esta llamada solo se tendria que hacer
			// si nos interesa borrar todo el contenido
			// de la tabla de peliculas en Oracle (por ejemplo
			// queremos volver a cargar todos los datos)
			borrarContenidoTablaIncidencia(oracle);
			
			// Seleccionamos los titulos y el ano de produccion
			// de las peliculas almacenadas en la tabla
			// "title" en la BD MySQL.
			// for(Cursor c: mysql.executeQueryAndGetCursor("SELECT title, production_year FROM title")) { // Se pega un buen rato insertando
			// mejor insertamos los 20 primeros resultados de la consulta siguiente...
			for(Cursor c: mysql.executeQueryAndGetCursor("\n" + 
					"SELECT t.*, \n" + 
					"       @rownum := @rownum + 1 AS rank\n" + 
					"  FROM (select * from\n" + 
					"((SELECT id,\"retrasado\" as tipo FROM (SELECT @rownum := @rownum + 1 AS id, t.* FROM miniFlights.flights200810 t,(SELECT @rownum := 0) r)a where carrierDelay is not null)\n" + 
					"UNION\n" + 
					"(SELECT id,\"cancelado\" as tipo FROM (SELECT @rownum := @rownum + 1 AS id, t.* FROM miniFlights.flights200810 t,(SELECT @rownum := 0) r)b where cancelled=1)\n" + 
					"union\n" + 
					"(SELECT id,\"desviado1\" as tipo FROM (SELECT @rownum := @rownum + 1 AS id, t.* FROM miniFlights.flights200810 t,(SELECT @rownum := 0) r)c where div1airport is not null and div1airport != \"\")\n" + 
					"union\n" + 
					"(SELECT id,\"desviado2\" as tipo FROM (SELECT @rownum := @rownum + 1 AS id, t.* FROM miniFlights.flights200810 t,(SELECT @rownum := 0) r)d where div2airport is not null and div2airport != \"\")\n" + 
					") f \n" + 
					"order by id) t, \n" + 
					"       (SELECT @rownum := 0) r;")) {
				// De cada fila extraemos los datos y los procesamos. 
				// A continuacion los insertamos en la BD Oracle.
				System.out.println("Insertando "+c.getString("rank")+" - "+(c.getString("tipo"))+" - "+(c.getInteger("id")));
				oracle.executeSentence("INSERT INTO INCIDENCIA(ID,TIPO,VUELO) VALUES (?,?,?)", 
						c.getInteger("rank"), c.getString("tipo"),c.getInteger("id"));
		}
			
			// Finalmente listamos el contenido resultante
			oracle.executeQuery("SELECT * FROM PELICULAS_EJEMPLO");
			
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		} finally {
			borrarTablaDePeliculas(oracle);
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
	
	private static void borrarContenidoTablaIncidencia(ConectorJDBC o) {
		StringBuffer sb = new StringBuffer();
		sb.append("TRUNCATE TABLE INCIDENCIA");
		o.executeSentence(sb.toString());
	}
	
	private static void borrarTablaDePeliculas(ConectorJDBC o) {
		StringBuffer sb = new StringBuffer();
		sb.append("DROP TABLE Incidencia");
		o.executeSentence(sb.toString());
	}

	private static void crearIncidencia(ConectorJDBC o) {
		StringBuffer sb = new StringBuffer();
		sb.append("CREATE TABLE Incidencia(");
		sb.append("Id Number(11) PRIMARY KEY,");
		sb.append("TIPO VARCHAR(100),");
		sb.append("VUELO REFERENCES VUELO(IDV) ON DELETE CASCADE");
		sb.append(")");
		o.executeSentence(sb.toString());
	}

	
	
}