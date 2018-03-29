window.onload = () =>{

    
    //Login event listener
    document.getElementById("reset").addEventListener("click", ()=>{
       let userId = getParameter("id"); 
       let token =getParameter("token");
       console.log(userId);
       console.log(token);
        let password = document.getElementById("password").value;
        let repeatPassword = document.getElementById("repeatPassword").value;
        if(password !== repeatPassword){
            document.getElementById("resetMessage").innerHTML = '<span class="label label-danger label-center">Password mismatch.</span>';
            return;
        }
        //AJAX Logic
        let xhr = new XMLHttpRequest();
        xhr.onreadystatechange = () => {
            if(xhr.readyState === XMLHttpRequest.DONE && xhr.status ===200){
                //Getting JSON from response body
                let data = JSON.parse(xhr.responseText); 
                //Call login response processing
                updatePassword(data);
            }
        };
          //Doing a HTTP to a specifc endpoint
          xhr.open("POST",`resetPasswordRequest.do?id=${userId}&password=${password}&token=${token}`);
   
     //Sending our request
     xhr.send();

    })
}


function updatePassword(data) {
     
    if(data.message === "VALID TOKEN"){
        console.log("Password has been updated.")
        document.getElementById("resetMessage").innerHTML = '<span class="label label-success label-center">Password has been updated successfully.</span>';
        setTimeout(() =>{ window.location.replace("login.do");}, 3000);
         }
    
    else if(data.message ==="SOMETHING WENT WRONG"){
        document.getElementById("resetMessage").innerHTML = '<span class="label label-danger label-center">EXPIRED TOKEN.</span>';
     setTimeout(() =>{ window.location.replace("login.do");}, 2000);
    }
    else{

     document.getElementById("resetMessage").innerHTML = '<span class="label label-danger label-center">EXPIRED TOKEN.</span>';
     setTimeout(() =>{ window.location.replace("login.do");}, 2000);
    }
}


function getParameter(myParameter) { 
    var params = window.location.search.substr(1).split('&');
   
    for (var i = 0; i < params.length; i++) {
      var p=params[i].split('=');
      if (p[0] == myParameter) {
        return decodeURIComponent(p[1]);
      }
    }
    return false;
  }

