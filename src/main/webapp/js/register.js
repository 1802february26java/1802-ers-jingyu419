window.onload = () =>{
    /** **/
    document.getElementById("loggedUsername").innerHTML = sessionStorage.getItem("username");
    //register event listener
    document.getElementById("submit").addEventListener("click", ()=>{
        let password = document.getElementById("password").value;
        let repeatPassword = document.getElementById("repeatPassword").value;
        if(password !== repeatPassword){
            document.getElementById("registrationMessage").innerHTML = '<span class="label label-danger label-center">Password mismatch.</span>';
            return;
        }
        
        //Get the rest fields
        let firstName = document.getElementById("firstName").value;
        let lastName = document.getElementById("lastName").value;
        let username = document.getElementById("username").value;
        console.log(username);
        let email = document.getElementById("email").value;
        //AJAX Logic
        let xhr = new XMLHttpRequest();

        xhr.onreadystatechange = () => {
            if(xhr.readyState === XMLHttpRequest.DONE && xhr.status ===200){
                //Getting JSON from response body
                let data = JSON.parse(xhr.responseText);
                console.log(data);   

                //Cal login response processing
                register(data);
            }
        };
          //Doing a HTTP to a specifc endpoint
          xhr.open("POST",`register.do?firstName=${firstName}&lastName=${lastName}&username=${username}&password=${password}&email=${email}`);
     //Sending our request
     xhr.send();

    })
}

function disableAllComponents(){
    document.getElementById("firstName").setAttribute("disabled","disabled");
    document.getElementById("lastName").setAttribute("disabled","disabled");
    document.getElementById("email").setAttribute("disabled","disabled");
    document.getElementById("username").setAttribute("disabled","disabled");
    document.getElementById("password").setAttribute("disabled","disabled");
    document.getElementById("repeatPassword").setAttribute("disabled","disabled");
    document.getElementById("submit").setAttribute("disabled","disabled");
}

function register(data) {
    
      //Confirm registratio nand redirect to login
      disableAllComponents();
  
      if(data.message === "REGISTRATION SUCCESSFUL"){
        document.getElementById("registrationMessage").innerHTML = '<span class="label label-success label-center">Registration successful.</span>';
        
        setTimeout(() =>{ window.location.replace("login.do");}, 3000);
       
      }
      else{
        document.getElementById("registrationMessage").innerHTML = '<span class="label label-danger label-center">Something went wrong.</span>';
           
      }
}