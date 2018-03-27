window.onload = () =>{

    //Login event listener
    document.getElementById("reset").addEventListener("click", ()=>{
        let email = document.getElementById("email").value;
        let username = document.getElementById("username").value;
        //AJAX Logic
        let xhr = new XMLHttpRequest();
        xhr.onreadystatechange = () => {
            if(xhr.readyState === XMLHttpRequest.DONE && xhr.status ===200){
                //Getting JSON from response body
                let data = JSON.parse(xhr.responseText); 
                //Call login response processing
                sendEmail(data);
            }
        };
          //Doing a HTTP to a specifc endpoint
          xhr.open("POST",`sendResetEmail.do?email=${email}&username=${username}`);
   
     //Sending our request
     xhr.send();

    })
}

function sendEmail(data) {
     
      if(data.message === "RESET EMAIL SENT"){
          console.log("Email has been sent.")
          document.getElementById("resetMessage").innerHTML = '<span class="label label-success label-center">An email has sent to your inbox.</span>';
          setTimeout(() =>{ window.location.replace("login.do");}, 3000);
           }
      else{

       document.getElementById("resetMessage").innerHTML = '<span class="label label-danger label-center">Incorrect username and email combination.</span>';
       setTimeout(() =>{ window.location.replace("resetPassword.html");}, 1000);
      }
}