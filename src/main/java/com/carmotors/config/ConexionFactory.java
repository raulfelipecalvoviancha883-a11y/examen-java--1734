package com.carmotors.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class ConexionFactory {

    private static final String DEFAULT_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/carmotors_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "Campus2023!";

    private static volatile ConexionFactory instance;

    private final String url;
    private final String user;
    private final String password;

    private ConexionFactory() {
        Properties props = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (is != null) {
                props.load(is);
            }
        } catch (IOException e) {
            System.err.println("[ConexionFactory] Advertencia: No se pudo leer db.properties: " + e.getMessage());
        }

        String envUrl = System.getenv("DB_URL");
        String envUser = System.getenv("DB_USER");
        String envPassword = System.getenv("DB_PASSWORD");

        this.url = envUrl != null ? envUrl : props.getProperty("db.url", DEFAULT_URL);
        this.user = envUser != null ? envUser : props.getProperty("db.user", DEFAULT_USER);
        this.password = envPassword != null ? envPassword : props.getProperty("db.password", DEFAULT_PASSWORD);

        String driver = props.getProperty("db.driver", DEFAULT_DRIVER);
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Error: No se encontró el driver JDBC de MySQL (" + driver + ")", e);
        }
    }

    /**
     * Obtiene la instancia única de ConexionFactory (patrón Singleton).
     *
     * @return Instancia única de ConexionFactory
     */
    public static ConexionFactory getInstance() {
        if (instance == null) {
            synchronized (ConexionFactory.class) {
                if (instance == null) {
                    instance = new ConexionFactory();
                }
            }
        }
        return instance;
    }

    /**
     * Retorna una nueva conexión JDBC a la base de datos.
     * Compatible con el consumo directo en DAOs: try (Connection conn = ConexionFactory.getConnection())
     *
     * @return Connection conexión activa
     * @throws SQLException si ocurre un error al conectar con la base de datos
     */
    public static Connection getConnection() throws SQLException {
        return getInstance().crearConexion();
    }

    /**
     * Crea y devuelve una nueva conexión con los parámetros configurados.
     *
     * @return Connection conexión activa
     * @throws SQLException si ocurre un error al conectar con la base de datos
     */
    public Connection crearConexion() throws SQLException {
        return DriverManager.getConnection(this.url, this.user, this.password);
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }
}
