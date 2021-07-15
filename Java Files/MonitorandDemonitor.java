
import java.io.IOException;
import java.io.PrintWriter;
import org.json.*;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet(urlPatterns = {"/MonitorandDemonitor"})
public class MonitorandDemonitor extends HttpServlet {
    final String jdbcdriver="com.mysql.cj.jdbc.Driver";
    final String url="jdbc:mysql://localhost/fileupload";
    final String user="root";
    final String pass="Arun@2001";
    
    Connection con = null;
    Statement stmt = null;
    ResultSet result = null;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
                       try{
        Class.forName("com.mysql.cj.jdbc.Driver");
        con = DriverManager.getConnection(url,user,pass);
    }catch(ClassNotFoundException e){
    }catch(SQLException e){
        out.println(e.toString());
    }
    
    try{
        String query;
        stmt = con.createStatement();
        query = "SELECT * FROM filemonitoring";
        result = stmt.executeQuery(query);
        if(!result.next()){
            out.print("0");
        }else{
            JSONArray array=new JSONArray();
            do{
                JSONObject obj = new JSONObject();
                obj.put("FilePath",result.getString(1));
                obj.put("Filename",result.getString(2));
                obj.put("Status",result.getString(3));
                obj.put("Event",result.getString(4));
                array.put(obj.toString());
            }while(result.next());
            out.print(array);
        }
        con.close();
    }catch(SQLException e){
        out.print("Exception: "+e.toString());
    }
        }
    }

    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}

