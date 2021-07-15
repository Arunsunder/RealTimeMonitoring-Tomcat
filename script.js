document.addEventListener("DOMContentLoaded", function() {
    var elements = document.getElementsByTagName("textarea");
    for (var i = 0; i < elements.length; i++) {
        elements[i].oninvalid = function(e) {
            e.target.setCustomValidity("");
            if (!e.target.validity.valid) {
                e.target.setCustomValidity("Enter The absolute file Path");
            }
        };
        elements[i].oninput = function(e) {
            e.target.setCustomValidity("");
        };
    }
})
    //submit the uploaded file into servlet
        $(function() {
            $('#file-upload').ajaxForm({
                success: function(msg) {
                    alert("Files has been uploaded successfully");
                    $('#upload-section').css('display','none');
                    $('#content').css('display','block');
                    fetch();
                },
                error: function(msg) {
                    $("#upload-section").text("Couldn't upload file");
                }
            });
        });
    
    //display the result Table
    function fetch() {
                $.ajax({
                       url:"MonitorandDemonitor",
                       dataType:"json",
                       success:function(res){
                           var data="";
                           for(i=0;i<res.length;i++){
                              var p=JSON.parse(res[i]);
                              if(String(p.Status)==="Monitoring"){
                                    data+="<tr><td><input type=checkbox name=check value='"+p.FilePath+"'></td><td>"+p.FilePath+"</td><td>"+p.Filename+"</td><td style=color:green>"+p.Status+"</td><td>"+p.Event+"</td></tr>";
                              }
                              else{
                                   data+="<tr><td><input type=checkbox name=check value='"+p.FilePath+"'></td><td>"+p.FilePath+"</td><td>"+p.Filename+"</td><td style=color:red>"+p.Status+"</td><td>"+p.Event+"</td></tr>";
                              }
                           }
                           data+="<br><br>";
                           $('#displaytable').html(data);
                       },
                       error:function() {
                           alert("error occured");
                       }
                  });
          }
          
          //Display the table based on monitoring or not-monitoring file
          $(function() {
            $('#eord').ajaxForm({
                success: function(msg) {
                    //Monitor();
                    fetch();
                },
                error: function(msg) {
                    $("#upload-section").text("Couldn't upload file");
                }
            });
        });
        
        //filemonitoring and reloading
        
        function Monitor(url){
                $.ajax({
                          type: "POST",
                          url:url,
                          success: function (msg) {
                              fetch();
                              },
                          error: function (data) {
                              alert("function failed");
                          }
                      });     
         }
        


