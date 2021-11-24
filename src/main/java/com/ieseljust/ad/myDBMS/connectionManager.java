package com.ieseljust.ad.myDBMS;

import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

class connectionManager{
    
    private String server;
    private String port;
    private String user;
    private String pass;

    private Connection laConexion;

    connectionManager(){
        // TO-DO: Inicialització dels atributs de la classe
        //       per defecte
            user = "root";
            pass = "root";
            server = "localhost";
            port = "3308";

            laConexion = null;
    }

    connectionManager(String server, String port, String user, String pass){
        // TO-DO:   Inicialització dels atributs de la classe
        //          amb els valors indicats
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.user = "root";
            this.pass = "2502";
            //pass = "root"; cuando lo pruebe el profesor
            this.server = "localhost";
            this.port = "49153";
            // port = "308"; cuando lo pruebe el profesor

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public Connection connectDBMS(){
        // TO-DO:   Crea una connexió a la base de dades, 
        //          i retorna aquesta o null, si no s'ha pogut connectar.

        // Passos:
            // 1. Carreguem el driver JDBC
        String url="jdbc:mysql://"+server+":"+port+"/MiniRol";

        //para introducir manualmente los datos

        Properties p = new Properties();

        p.put("user", user);
        p.put("password", pass);

        // 2. Crear la connexió a la BD
        // Recordeu el tractament d'errors
        try {
            laConexion=DriverManager.getConnection(url,p);
            return laConexion;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Se ha conectado");
            return null;
        }

        // 3. Retornar la connexió



    }

    public void showInfo(){
        // TO-DO: Mostra la informació del servidor a partir de les metadades
        // - Nom del SGBD
        // - Driver utilitzat
        // - URL de connexió
        // - Nom de l'usuari connectat
        connectDBMS();
        try {
            Statement st = connectDBMS().createStatement();
            ResultSet rs = st.executeQuery("select * from armas order by id");

            while (rs.next()){
                System.out.println(String.format("%-20s %-20s %-20s %-20s",
                        rs.getInt("id"),
                                rs.getString("nombre"),
                                rs.getString("descripcion"),
                                rs.getInt("danyo")));
            }
            laConexion.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void showDatabases(){
         // TO-DO: Mostrem les bases de dades del servidor, bé des del catàleg o amb una consulta

         // Recordeu el tractament d'errors
        connectDBMS();
        try {
            Statement st = laConexion.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM armas;");

            while (rs.next()){
                System.out.println(String.format("%-20s %-20s %-20s %-20s",
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getInt("danyo")));
            }
            laConexion.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startShell(){

        Scanner keyboard = new Scanner(System.in);
        String command;

        do {

            System.out.print(ConsoleColors.GREEN_BOLD_BRIGHT+"# ("+this.user+") on "+this.server+":"+this.port+"> "+ConsoleColors.RESET);
            command = keyboard.nextLine();

                        
            switch (command){
                case "sh db":
                case "show databases":
                    this.showDatabases();
                    break;
                
                case "info":
                    this.showInfo();
                    break;

                case "quit":
                    break;
                default:
                    // Com que no podem utilitzar expressions
                    // regulars en un case (per capturar un "use *")
                    // busquem aquest cas en el default:

                    String[] subcommand=command.split(" ");
                    switch (subcommand[0]){
                        case "use":
                            // TO-DO:
                                // Creem un objecte de tipus databaseManager per connectar-nos a
                                // la base de dades i iniciar una shell de manipulació de BD..

                        default:
                            System.out.println(ConsoleColors.RED+"Unknown option"+ConsoleColors.RESET);
                            break;


                    }

                    

            }
            
        } while(!command.equals("quit"));

        
        }


    public Connection getLaConexion() {
        if (laConexion==null)
            this.connectDBMS();

        return laConexion;
    }
}