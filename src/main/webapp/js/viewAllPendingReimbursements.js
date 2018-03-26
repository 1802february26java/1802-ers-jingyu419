window.onload = () =>{

    document.getElementById("loggedUsername").innerHTML = sessionStorage.getItem("username");
    /** **/
    //Get event listener
    document.getElementById("getPendingReimbursements").addEventListener("click", getAllPendingReimbursements);
    //Get all pending reimbursement as soon as the page loads
    getAllPendingReimbursements();
}

function getAllPendingReimbursements(){
      //AJAX Logic
      let xhr = new XMLHttpRequest();

      xhr.onreadystatechange = () => {
          if(xhr.readyState === XMLHttpRequest.DONE && xhr.status ===200){
              //Getting JSON from response body
           //   consol.log(xhr.responseText);
              let data = JSON.parse(xhr.responseText);
                console.log(data);
 
              //present the data to the user
              presentAllPendingReimbursements(data);
          }
      };
        //Doing a HTTP to a specifc endpoint
        xhr.open("GET",`multipleRequests.do?fetch=pending`);
 
   //Sending our request
   xhr.send();
}

function presentAllPendingReimbursements(data) {
     
    //If message is a member of the JSON, something went wrong
      if(data.message){
          document.getElementById("listMessage").innerHTML = '<span class="label label-danger label-center">Something went wrong.</span>';
      }
      else{
          // we present reimbursements to the user
          //Get reimbursement lsit node
      

     let reimbursementList = document.getElementById("pendingReimbursementsList");
         reimbursementList.innerHTML="";
  data.forEach((reimbursement)=>{
           
         let tr = document.createElement('tr');   

         let td1 = document.createElement('td');
         let td2 = document.createElement('td');
         let td3 = document.createElement('td');
         let td4 = document.createElement('td');
         let td5 = document.createElement('td');
         let td6 = document.createElement('td');
         //td7 is reimbursement status
         let td7 = document.createElement('td');

         let selectList = document.createElement('select');
         selectList.className = 'selectpicker';  
         selectList.id = 'selectedStatus';   
         let option1 = document.createElement('option');
         let option2 = document.createElement('option');
         let option3 = document.createElement('option');

         //td8 is for action button
         let td8 = document.createElement('td');
         let button1 = document.createElement('button'); 
         let button2 = document.createElement('button'); 

         let text1 = document.createTextNode(`${reimbursement.id}`);
         let text2 = document.createTextNode(`${reimbursement.requested.year}-${reimbursement.requested.monthValue}-${reimbursement.requested.dayOfMonth}, ${reimbursement.requested.hour}:${reimbursement.requested.minute}:${reimbursement.requested.second}`);
         console.log(reimbursement.requested);
         let text3 = document.createTextNode(`${reimbursement.amount}`);
         let text4 = document.createTextNode(`${reimbursement.description}`);
         let text5 = document.createTextNode(`${reimbursement.requester.firstName} ${reimbursement.requester.lastName}`);
         let text6 = document.createTextNode(`${reimbursement.type.type}`);
         //td 7 text
         let optionText1 = document.createTextNode(`${reimbursement.status.status}`);
         let optionText2 = document.createTextNode(`DECLINED`);
         let optionText3 = document.createTextNode(`APPROVED`); 

         //td8
        let textButton2 = document.createTextNode('SAVE');
        button2.className = 'btn btn-sm btn-success';
        button2.setAttribute('onclick','finalizedReimbursement(this)');

             td1.appendChild(text1);
             td2.appendChild(text2);
             td3.appendChild(text3);
             td4.appendChild(text4);
             td5.appendChild(text5);
             td6.appendChild(text6);

             //td7
             option1.appendChild(optionText1);
             option2.appendChild(optionText2);
             option3.appendChild(optionText3);
             selectList.appendChild(option1);
             selectList.appendChild(option2);
             selectList.appendChild(option3);
             td7.appendChild(selectList);
             //td8
             button2.appendChild(textButton2);
             td8.appendChild(button2);

             tr.appendChild(td1);
             tr.appendChild(td2);
             tr.appendChild(td3);
             tr.appendChild(td4);
             tr.appendChild(td5);
             tr.appendChild(td6);
             tr.appendChild(td7);
             tr.appendChild(td8);

             reimbursementList.appendChild(tr);
          });


      }
}



function finalizedReimbursement(obj){
   console.log(document.getElementById('selectedStatus').value);
   /*
   let newReimbursementStatus = document.getElementById('selectedStatus').value;
   //AJAX Logic
   let xhr = new XMLHttpRequest();

   xhr.onreadystatechange = () => {
       if(xhr.readyState === XMLHttpRequest.DONE && xhr.status ===200){
           //Getting JSON from response body
           let data = JSON.parse(xhr.responseText);
           console.log(data);   
           //Cal login response processing
           updateReimbursement(data);
       }
   };
     //Doing a HTTP to a specifc endpoint
     xhr.open("POST",`finalizeRequest.do?firstName=${firstName}`);
//Sending our request
xhr.send();

}

function updateReimbursement(data) {

    if(data.message === "REGISTRATION SUCCESSFUL"){
      document.getElementById("registrationMessage").innerHTML = '<span class="label label-success label-center">Update successful.</span>';
      
      setTimeout(() =>{ window.location.replace("multipleRequests.do?fetch=pending");}, 3000);
     
    }
    else{
      document.getElementById("registrationMessage").innerHTML = '<span class="label label-danger label-center">Something went wrong.</span>';
         
    }
    */
}