package banking;

import sqlconnection.Database;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        //Checks if arguments have been passed at launch
        Map<String, String> arguments = new HashMap<>();
        String url;
        if (args.length > 0) {
            for (int i = 0; i < args.length; i += 2) {
                arguments.put(args[i], args[i + 1]);
            }
        }
        //...Otherwise it sets the default argument to create a new database
        url = arguments.getOrDefault("-fileName", "test.db");
        Database database = new Database(url);
        database.createNewTable();

        UI ui = new UI(database);
        ui.start();
    }
}