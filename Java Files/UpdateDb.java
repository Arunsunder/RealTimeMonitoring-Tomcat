import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UpdateDb extends HttpServlet {
    
    final String jdbcdriver="com.mysql.cj.jdbc.Driver";
    final String url="jdbc:mysql://localhost/fileupload";
    final String user="root";
    final String pass="Arun@2001";
    
    Connection con = null;
    Statement stmt = null;
    ResultSet result = null;


    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, ClassNotFoundException {
        response.setContentType("text/html;charset=UTF-8");
        Class.forName("com.mysql.cj.jdbc.Driver");
        try{
            con=DriverManager.getConnection(url,user,pass);
            stmt=con.createStatement();
            String query="SELECT * FROM filemonitoring";
            String [] checked = request.getParameterValues("check");
            result=stmt.executeQuery(query);
            PreparedStatement ps=con.prepareStatement("update filemonitoring set status=?,event=? where filepath=? ");
            int numofrows=result.getRow();
            while(result.next()){
                    for(int i=0;i<checked.length;i++){
                        String filepath=result.getString("filepath");
                        String status=result.getString("status");
                        if(status.equals("Monitoring") && filepath.equals(checked[i])){
                                 ps.setString(1,"Monitoring Disabled");
                                 ps.setString(2,"Not Monitoring");
                                 ps.setString(3, checked[i]);
                                 ps.executeUpdate();
                        }else if(status.equals("Monitoring Disabled") && filepath.equals(checked[i])){
                                 ps.setString(1,"Monitoring");
                                 ps.setString(2,"None");
                                 ps.setString(3, checked[i]);
                                 ps.executeUpdate();
                        }
                    }
            }
            con.close();
        }catch(Exception e){
        
        }
        
        }
 
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(UpdateDb.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(UpdateDb.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
