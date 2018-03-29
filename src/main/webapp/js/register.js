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
        let email = document.getElementById("email").value;

        console.log(username);
        let checkExistedUserXhr = new XMLHttpRequest();
        checkExistedUserXhr.onreadystatechange = () => {
            if(checkExistedUserXhr.readyState === XMLHttpRequest.DONE && checkExistedUserXhr.status ===200){
                let checkExistedUserData = JSON.parse(checkExistedUserXhr.responseText);   

                checkUser(checkExistedUserData);
            }
        };
          //Doing a HTTP to a specifc endpoint
          checkExistedUserXhr.open("POST",`isUserExisted.do?username=${username}`);
     //Sending our request
     checkExistedUserXhr.send();

    })
}

function registerXhr(){
        
    let password = document.getElementById("password").value;
    let firstName = document.getElementById("firstName").value;
    let lastName = document.getElementById("lastName").value;
    let username = document.getElementById("username").value;
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
          console.log(`${firstName}&lastName=${lastName}&username=${username}&password=${password}&email=${email}`);
          
          xhr.open("POST",`register.do?firstName=${firstName}&lastName=${lastName}&username=${username}&password=${password}&email=${email}`);
     //Sending our request
     xhr.send();
}

function disableAllComponents(){
    document.getElementById("firstName").disableAllComponents = true;
    document.getElementById("lastName").disableAllComponents = true;
    document.getElementById("email").disableAllComponents = true;
    document.getElementById("username").disableAllComponents = true;
    document.getElementById("password").disableAllComponents = true;
    document.getElementById("repeatPassword").disableAllComponents = true;
    document.getElementById("submit").disableAllComponents = true;
}

function enableAllComponents(){
    document.getElementById("firstName").disableAllComponents = false;
    document.getElementById("lastName").disableAllComponents = false;
    document.getElementById("email").disableAllComponents = false;
    document.getElementById("username").disableAllComponents = false;
    document.getElementById("password").disableAllComponents = false;
    document.getElementById("repeatPassword").disableAllComponents = false;
    document.getElementById("submit").disableAllComponents = false;
}


function register(data) {
    
      //Confirm registratio nand redirect to login
      disableAllComponents();
  
      if(data.message === "REGISTRATION SUCCESSFUL"){
        document.getElementById("registrationMessage").innerHTML = '<span class="label label-success label-center">Registration successful.</span>';
        
        setTimeout(() =>{ window.location.replace("home.do");}, 3000);
       
      }
      else{
        document.getElementById("registrationMessage").innerHTML = '<span class="label label-danger label-center">Something went wrong.</span>';
         enableAllComponents();
         setTimeout(() =>{ document.getElementById("registrationMessage").innerHTML = '';}, 3000);
      }
}

function checkUser(checkExistedUserData){

    if(checkExistedUserData.message === "This username has not been taken."){
        registerXhr();
      }
      else{
        document.getElementById("registrationMessage").innerHTML = '<span class="label label-danger label-center">The username has been taken.</span>';
         setTimeout(() =>{ document.getElementById("registrationMessage").innerHTML = '';}, 3000);
      }  
}