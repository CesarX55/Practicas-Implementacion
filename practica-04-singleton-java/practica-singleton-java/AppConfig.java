/**
 * Configuracion global de la aplicacion.
 * Patron Singleton con inicializacion ansiosa: la unica instancia se crea
 * cuando la clase se carga y todos los clientes la obtienen con getInstance().
 */
public class AppConfig {
    private static final AppConfig instance = new AppConfig();

    private String theme;
    private String language;

    // Constructor privado: nadie fuera de la clase puede usar "new AppConfig()".
    private AppConfig() {
        this.theme = "Light";
        this.language = "EN";
    }

    public static AppConfig getInstance() {
        return instance;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void printConfig() {
        System.out.println("Theme: " + theme + ", Language: " + language);
    }
}
