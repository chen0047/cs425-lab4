package edu.jsu.mcis.cs425.Lab4;

import com.opencsv.CSVReader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import java.sql.*;
import javax.sql.DataSource;
import javax.naming.Context;
import javax.naming.InitialContext;

public class Rates {
    
    public static final String RATE_FILENAME = "rates.csv";
    
    public static List<String[]> getRates(String path) {
        
        StringBuilder s = new StringBuilder();
        List<String[]> data = null;
        String line;
        
        try {
            
            /* Open Rates File; Attach BufferedReader */

            BufferedReader reader = new BufferedReader(new FileReader(path));
            
            /* Get File Data */
            
            while((line = reader.readLine()) != null) {
                s.append(line).append('\n');
            }
            
            reader.close();
            
            /* Attach CSVReader; Parse File Data to List */
            
            CSVReader csvreader = new CSVReader(new StringReader(s.toString()));
            data = csvreader.readAll();
            
        }
        catch (Exception e) { System.err.println( e.toString() ); }
        
        /* Return List */
        
        return data;
        
    }
    
    public static String getRatesAsTable(List<String[]> csv) {
        
        StringBuilder s = new StringBuilder();
        String[] row;
        
        try {
            
            /* Create Iterator */
            
            Iterator<String[]> iterator = csv.iterator();
            
            /* Create HTML Table */
            
            s.append("<table>");
            
            while (iterator.hasNext()) {
                
                /* Create Row */
            
                row = iterator.next();
                s.append("<tr>");
                
                for (int i = 0; i < row.length; ++i) {
                    s.append("<td>").append(row[i]).append("</td>");
                }
                
                /* Close Row */
                
                s.append("</tr>");
            
            }
            
            /* Close Table */
            
            s.append("</table>");
            
        }
        catch (Exception e) { System.err.println( e.toString() ); }
        
        /* Return Table */
        
        return (s.toString());
        
    }
    
    public static String getRatesAsJson(List<String[]> csv) {
        
        String results = "";
        String[] row;
        
        try {
            
            /* Create Iterator */
            
            Iterator<String[]> iterator = csv.iterator();
            
            /* Create JSON Containers */
            
            JSONObject json = new JSONObject();
            JSONObject rates = new JSONObject();            
            
            /* 
             * Add rate data to "rates" container and add "date" and "base"
             * values to "json" container.  See the "getRatesAsTable()" method
             * for an example of how to get the CSV data from the list, and
             * don't forget to skip the header row!
             *
             * *** INSERT YOUR CODE HERE ***
             */
            
            row = iterator.next();
            
            while(iterator.hasNext()){
                row = iterator.next();
                String code = row[1];
                double r = Double.parseDouble(row[2]);
                rates.put(code, r);
                
            }
            
            
            json.put("rates", rates);
            json.put("base", "USD");
            json.put("date", "2019-09-20");
            
            /* Parse top-level container to a JSON string */
            
            results = JSONValue.toJSONString(json);
            
            System.err.println(results);   //dignositic print
            
        }
        catch (Exception e) { System.err.println( e.toString() ); }
        
        /* Return JSON string */
        
        System.err.println(results);   //dignositic print
        
        return (results.trim());
        
    }
    
    public static String getRatesAsJson(String code){
        
        String result = "";
        PreparedStatement pstSelect = null;
        ResultSet resultset = null;
        
        String query;
        boolean hasresults;
        
        
        try{
            
            JSONObject json = new JSONObject();
            JSONObject rates = new JSONObject();
            
            Context envContext = new InitialContext();
            Context initContext = (Context)envContext.lookup("java:/comp/env");
            DataSource ds = (DataSource)initContext.lookup("jdbc/db_pool");
            Connection conn = ds.getConnection();
            
            
            if(conn.isValid(0)){
                System.err.println("Connected Successfully!");
            }
            
            if (code != null) {

                query = "SELECT * FROM rates WHERE code=?";

                pstSelect = conn.prepareStatement(query);
                pstSelect.setString(1, code);
                
            }
            
            else {
                query = "SELECT * FROM rates";

                pstSelect = conn.prepareStatement(query);
            }
            
            
            hasresults = pstSelect.execute();
            
            if(hasresults){
                resultset = pstSelect.getResultSet();
                while (!resultset.isLast()) {
                    resultset.next();
                    code = resultset.getString("code");
                    double rate = resultset.getDouble("rate");
                    rates.put(code, rate);
                }
                

            }
            
            json.put("date", "2019-09-30");
            json.put("rates", rates);
            json.put("base", "USD");
            
            
            result = JSONValue.toJSONString(json);
        
        } catch (Exception e){System.err.println(e.toString());}
        
        return (result.trim());
    }

}