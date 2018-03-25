window.onload = () =>{

    document.getElementById("loggedUsername").innerHTML = sessionStorage.getItem("username");
    /** **/
    //Get event listener
    document.getElementById("editProfile").addEventListener("click", editProfile);


    document.getElementById("saveProfile").addEventListener("click", ()=>{
       
    document.getElementById("editable1").contentEditable = false;
    document.getElementById("editable2").contentEditable = false;
    document.getElementById("editable3").contentEditable = false;

       let firstName = document.getElementById("editable1").innerHTML;
       let lastName =   document.getElementById("editable2").innerHTML;
       let email = document.getElementById("editable3").innerHTML;
        //save it through http
        let xhrSave = new XMLHttpRequest();
          xhrSave.onreadystatechange = () => {
              if(xhrSave.readyState === XMLHttpRequest.DONE && xhrSave.status ===200){
    
                  let dataToSave = JSON.parse(xhrSave.responseText);
                  saveToDatabase(dataToSave);
              }
          };
            //Doing a HTTP to a specifc endpoint
            xhrSave.open("POST",`updateInformation.do?firstName=${firstName}&lastName=${lastName}&email=${email}`);
     
       //Sending our request
       xhrSave.send();
    });
    //Get profile as soon as the page loads
    getProfile();
}

function getProfile(){
      //AJAX Logic
      let xhr = new XMLHttpRequest();

      xhr.onreadystatechange = () => {
          if(xhr.readyState === XMLHttpRequest.DONE && xhr.status ===200){
              //Getting JSON from response body
              let data = JSON.parse(xhr.responseText);
 

              //present the data to the user
              presentProfile(data);
          }
      };
        //Doing a HTTP to a specifc endpoint
        xhr.open("GET",`viewInformation.do?fetch=profile`);
 
   //Sending our request
   xhr.send();
}

function editProfile(){
    document.getElementById("editable1").contentEditable = true;
    document.getElementById("editable2").contentEditable = true;
    document.getElementById("editable3").contentEditable = true;
}

function saveToDatabase(dataToSave){
    if(dataToSave.message === "UPDATE EMPLOYEE INFORMATION SUCCESSFUL"){
        document.getElementById("saveMessage").innerHTML = '<span class="label label-success label-center">Save successful.</span>';
       
      }
      else{
        document.getElementById("saveMessage").innerHTML = '<span class="label label-danger label-center">Something went wrong.</span>';        
     }
}

function presentProfile(data) {
     
    //If message is a member of the JSON, something went wrong
      if(data.message){
          document.getElementById("saveMessage").innerHTML = '<span class="label label-danger label-center">Something went wrong.</span>';
      }
      else{
          // we present reimbursements to the user
          //Get reimbursement lsit node
      

     let profile = document.getElementById("viewProfile");
         profile.innerHTML="";
           
         let tr = document.createElement('tr');   

         let td1 = document.createElement('td');
         let td2 = document.createElement('td');
         let td3 = document.createElement('td');
         let td4 = document.createElement('td');
         let td5 = document.createElement('td');

         td1.id = "editable1";
         td2.id = "editable2";
         td4.id = "editable3";
                 
         var text1 = document.createTextNode(`${data.firstName}`);
         let text2 = document.createTextNode(`${data.lastName}`);         
         let text3 = document.createTextNode(`${data.username}`);
         let text4 = document.createTextNode(`${data.email}`);         
         let text5 = document.createTextNode(`${data.employeeRole.type}`);
        
             td1.appendChild(text1);
             td2.appendChild(text2);
             td3.appendChild(text3);  
             td4.appendChild(text4);
             td5.appendChild(text5);         

             tr.appendChild(td1);
             tr.appendChild(td2);
             tr.appendChild(td3);
             tr.appendChild(td4);
             tr.appendChild(td5);
            
             profile.appendChild(tr);
      }

}