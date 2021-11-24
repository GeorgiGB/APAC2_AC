package com.ieseljust.ad.myDBMS;

import java.sql.*;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

class databaseManager{
    
    private String server;
    private String port;
    private String user;
    private String pass;
    private String dbname;

    private Connection laConexio;

    databaseManager(){
        // TO-DO: Inicialització dels atributs de la classe
        //       per defecte
        user = "root";
        pass = "root";
        server = "localhost";
        port = "3308";
        dbname = "bdjocs";

        laConexio = null;
    }

    databaseManager(String server, String port, String user, String pass, String dbname){
        // TO-DO:   Inicialització dels atributs de la classe
        //          amb els valors indicats

        this.user = "root";
        this.pass = "root";
        this.server = "localhost";
        this.port = "3308";
        this.dbname = "bdjocs";

        laConexio = null;

    }

    public Connection connectDatabase(){
        // TO-DO:   Crea una connexió a la base de dades, 
        //          i retorna aquesta o null, si no s'ha pogut connectar.

        // Passos:
            // 1. Carreguem el driver JDBC
            // 2. Crear la connexió a la BD
            // 3. Retornar la connexió
        if (this.laConexio == null){
            try {
                this.laConexio = DriverManager.getConnection("jdbc:mysql://"+this.server+":"+this.port+"/"+this.dbname+"?"+
                        "useUnicode=true&characterEncoding=UTF-8&user="+this.user+"&"+
                        "password="+this.pass+"&allowMultipleQueries=true");
            } catch (SQLException e) {
                Logger.getLogger(connectionManager.class.getName()).log(Level.SEVERE, null, e);
            }
            return this.laConexio;
        }

        // Recordeu el tractament d'errors

        return null;
    }

    public void showTables(){
        // TO-DO: Mostra un llistat amb les taules de la base de dades
        
        // Passos:
        // 1. Establir la connexió a la BD
        // 2. Obtenir les metadades
        // 3. Recórrer el resultset resultant mostrant els resultats
        // 4. Tancar la connexió

        String sentSQL = "SHOW TABLES";
        try {
            Statement st = connectDatabase().createStatement();
            ResultSet rs = st.executeQuery("SHOW TABLES");

            while(rs.next()){
                System.out.println(rs.getString("Tables_in_"+this.dbname));
            }
            laConexio.close();
        } catch (SQLException ex) {
            Logger.getLogger(connectionManager.class.getName()).log(Level.SEVERE, null, ex);
        }


        
         // Recordeu el tractament d'errors
    }



    public void insertIntoTable(String table){
        // TO-DO: Afig informació a la taula indicada

        // Passos
        // 1. Estableix la connexió amb la BD
        // 2. Obtenim les columnes que formen la taula (ens interessa el nom de la columna i el tipus de dada)
        // 3. Demanem a l'usuari el valor per a cada columna de la taula
        // 4. Construim la sentència d'inserció a partir de les dades obtingudes
        //    i els valors proporcionats per l'usuari
        
        // Caldrà tenir en compte:
        // - Els tipus de dada de cada camp
        // - Si es tracta de columnes generades automàticament per la BD (Autoincrement)
        //   i no demanar-les
        // - Gestionar els diferents errors
        // - Si la clau primària de la taula és autoincremental, que ens mostre el valor d'aquesta quan acabe.

        try {
            Scanner keyboard = new Scanner(System.in);

            DatabaseMetaData dbmd = laConexio.getMetaData();
            ArrayList<String> colum = new ArrayList<>();
            ArrayList<String> type = new ArrayList<>();

            ResultSet rs = dbmd.getColumns(dbname, null, table, null);
            //Comprobante de las columnas
            while (rs.next()) {
                String columnName = rs.getString(4);
                String tipus = rs.getString(6);
                System.out.print(columnName + "(" + tipus + "):");
                String valor = keyboard.nextLine();
                colum.add(valor);
                type.add(tipus);

            }

            String insertar = "INSERT INTO " + table + " VALUES(";
            String espacio = "";
            for (int i = 0; i < colum.size(); i++) {
                espacio += "?,";
            }
            espacio = espacio.substring(0, espacio.length() - 1);
            espacio += ")";
            insertar += espacio;

            PreparedStatement pstm = laConexio.prepareStatement(insertar);
            for (int i = 0; i < colum.size(); i++) {
                if (type.get(i).equals("INT")) {
                    pstm.setInt(i+1, Integer.valueOf(colum.get(i)));
                }
                if (type.get(i).equals("FLOAT")) {
                    pstm.setFloat(i+1, Float.valueOf(colum.get(i)));
                }
                if (type.get(i).equals("DATE")) {
                    pstm.setDate(i+1, Date.valueOf(colum.get(i)));
                }
                if (type.get(i).equals("VARCHAR")) {
                    pstm.setString(i+1,colum.get(i));
                }
                if (type.get(i).equals("CHAR")) {
                    pstm.setString(i+1,colum.get(i));
                }
            }
            pstm.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(databaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }



    public void showDescTable(String table){
        // TO-DO: Mostra la descripció de la taula indicada, 
        //        mostrant: nom, tipus de dada i si pot tindre valor no nul
        //        Informeu també de les Claus Primàries i externes

        try {

            DatabaseMetaData dbmd = laConexio.getMetaData();

            ResultSet rspk = dbmd.getPrimaryKeys(dbname, null, table);

            ArrayList<String> pks = new ArrayList<>();
            while (rspk.next()) {
                pks.add(rspk.getString(4));
            }
            rspk.close();

            ResultSet rsfk = dbmd.getImportedKeys(dbname, null, table);

            ArrayList<String> fks = new ArrayList<>();
            ArrayList<String> fksExt = new ArrayList<>();

            while (rsfk.next()) {
                fks.add(rsfk.getString(8));
                fksExt.add(rsfk.getString(3));
            }

            rsfk.close();

            System.out.println("");
            System.out.println("\t TABLA " + table + ConsoleColors.RESET);
            System.out.println(String.format("%-20s %-20s %-20s", "Atributo o Clave", "Tipo", "Null?"));

            ResultSet columnes = dbmd.getColumns(dbname, null, table, null);

            while (columnes.next()) {
                String columnName = columnes.getString(4);

                if (pks.contains(columnName)) {
                    columnName = columnName + "(PK)";
                }


                if (fks.contains(columnName)) {
                    columnName = columnName + "(FK) " + fksExt.get(fks.indexOf(columnName));
                }

                String tipus = columnes.getString(6);
                String nullable = columnes.getString(18);
                //separacion de las tablas
                System.out.println(String.format("%-20s %-20s %-20s", columnName, tipus, nullable));
            }
        } catch (SQLException ex) {

            System.out.println("Nombre Incorrecto");
        }

    }

    public void startShell(){

        // TO-DO: Inicia la shell del mode base de dades

        }


}