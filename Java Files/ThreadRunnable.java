
import java.sql.*;
import java.nio.file.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class ThreadRunnable extends HttpServlet implements Runnable {
    
   static final String DB_URL = "jdbc:mysql://localhost/fileupload";
   static final String USER = "root";
   static final String PASS = "Arun@2001";
   String status="Monitoring";
   String[] Filepath=new String[1000];
   int length=0;
   
   public void init() throws ServletException {
     Thread runner = new Thread(this);
      runner.setPriority(Thread.MAX_PRIORITY);
      runner.start();
  }
   
   public void run() {
        try(Connection con=DriverManager.getConnection(DB_URL,USER,PASS);Statement stmt=con.createStatement();){
             Class.forName("com.mysql.cj.jdbc.Driver");
             String query="SELECT * FROM filemonitoring";
             ResultSet result;
             result=stmt.executeQuery(query);
               while(result.next()){
                   Filepath[length++]=result.getString("filepath");
                   //System.out.println(Filepath[length-1]);
               }
               PreparedStatement ps=con.prepareStatement("update filemonitoring set event=? where filepath=? and status=? ");
               try(WatchService service=FileSystems.getDefault().newWatchService()){
                //System.out.println(FileSystems.getDefault());
                   Map<WatchKey,Path> keyMap=new HashMap<>();
                   for(int i=0;i<length;i++){
                       Path path=Paths.get(Filepath[i]);
                       Path root=path.getParent();
                       keyMap.put(root.register(service,StandardWatchEventKinds.ENTRY_MODIFY), root);
                   }
                   WatchKey watchkey;
                   do{
                        watchkey=service.take();
                        Path ed=keyMap.get(watchkey);
                        for(WatchEvent<?> event:watchkey.pollEvents()){
                            WatchEvent.Kind<?> k=event.kind();
                            Path ep=(Path)event.context();
                            for(int i=0;i<length;i++){
                                String file=Filepath[i];
                                String temp=file.substring(file.lastIndexOf("\\")+1, file.length());
                                result=stmt.executeQuery(query);
                                while(result.next()){
                                    String filepath=result.getString("filepath");
                                    String st=result.getString("status");
                                    if(ep.endsWith(temp) && filepath.equals(Filepath[i]) && st.equals(status)){
                                        System.out.println("-----------------");
                                        System.out.println(temp+"          "+Filepath[i]+"         ");
                                        System.out.println("-----------------");
                                        System.out.println(k);
                                     ps.setString(1, k.toString());
                                     ps.setString(2, Filepath[i]);
                                     ps.setString(3, status);
                                     ps.executeUpdate();
                                     break;
                                   } 
                                 }
                           }
                        } 
           }while(watchkey.reset());
              } catch (InterruptedException ex) {
                    Logger.getLogger(ThreadRunnable.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {        
                Logger.getLogger(ThreadRunnable.class.getName()).log(Level.SEVERE, null, ex);
            }        
        }catch (SQLException ex) {
             Logger.getLogger(ThreadRunnable.class.getName()).log(Level.SEVERE, null, ex);
         } catch (ClassNotFoundException ex) {
           Logger.getLogger(ThreadRunnable.class.getName()).log(Level.SEVERE, null, ex);
       }
        
        
    }
 
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");                       
        }
}
