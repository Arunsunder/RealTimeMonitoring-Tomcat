import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.System.out;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;


public class Fileupload extends HttpServlet {
    static final String DB_URL = "jdbc:mysql://localhost/fileupload";
   static final String USER = "root";
   static final String PASS = "Arun@2001";
   String status="Monitoring";
   String event="None";
   String[] Validfiles={".c",".java",".py",".json",".xml",".cpp",".txt",".css",".js"};
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, ClassNotFoundException {
        response.setContentType("text/html;charset=UTF-8");
       try (PrintWriter out = response.getWriter()) {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try(Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);Statement stmt = conn.createStatement();) {
                       String text=request.getParameter("files");
                       String[] filename = text.split(";");
                       for(int i=0;i<filename.length;i++){
                           System.out.println(filename[i]);
                       }
                        for (int i=0;i<filename.length;i++) {
                            int flag=0;
                            for(int j=0;j<Validfiles.length;j++){
                                if(filename[i].contains(Validfiles[j])){
                                    InsertintoDb(filename[i]);
                                    break;
                                }
                            }
                         }
                        conn.close();
                  } catch (SQLException e) {
                  } 
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Fileupload.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Fileupload.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
     
    protected void InsertintoDb(String filepath) throws IOException{
                     try( Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);Statement stmt = conn.createStatement();) 
                          {		     
                             String sql = "INSERT INTO  filemonitoring (filepath,filename,status,event) VALUES  (?,?,?,?)";
                             try{
                                 PreparedStatement ps=conn.prepareStatement(sql);
                                 if(isPathValid(filepath)){
                                    ps.setString(1,filepath);
                                    String filename=filepath.substring(filepath.lastIndexOf("\\")+1, filepath.length());
                                    ps.setString(2,filename);
                                    ps.setString(3,status);
                                    ps.setString(4,event);
                                    ps.executeUpdate();
                                 }
                             }catch(SQLException e){
                               out.println(e.toString());
                             }
                             conn.close();
               } catch (SQLException e) {       
               } 
        }
    
    
    public static boolean isPathValid(String pathname) throws IOException {
                try(WatchService service=FileSystems.getDefault().newWatchService()){
                                     Map<WatchKey,Path> keyMap=new HashMap<>();
                                         File file=new File(pathname);
                                         if(file.exists()){
                                             Path path=Paths.get(pathname);
                                             Path dir=path.getParent();
                                             keyMap.put(dir.register(service,StandardWatchEventKinds.ENTRY_MODIFY), dir);
                                         }else{
                                             return false;
                                         }
                                 }catch(Exception e){
                                     System.out.println(e.toString());
                                     return false;
                                 }
                return true;
        }
}
