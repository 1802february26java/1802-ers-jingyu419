window.onload = () =>{

    document.getElementById("loggedUsername").innerHTML = sessionStorage.getItem("username");
    /** **/
    //Get event listener
    document.getElementById("getPendingReimbursements").addEventListener("click", getAllPendingReimbursements);
    //Get all pending reimbursement as soon as the page loads

    //filter
    document.getElementById("filter").addEventListener("keyup",filterTable);

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

          //count how many reimbursements we have
        let counter = 0;      

     let reimbursementList = document.getElementById("pendingReimbursementsList");
         reimbursementList.innerHTML="";
  data.forEach((reimbursement)=>{
         counter = counter + 1;  
         let tr = document.createElement('tr');   

         let td1 = document.createElement('td');
         let td2 = document.createElement('td');
         let td3 = document.createElement('td');
         let td4 = document.createElement('td');
         let td5 = document.createElement('td');
         let td6 = document.createElement('td');
         let td7 = document.createElement('td');
         
         let text1 = document.createTextNode(`${reimbursement.id}`);
         let text2 = document.createTextNode(`${reimbursement.requested.year}-${reimbursement.requested.monthValue}-${reimbursement.requested.dayOfMonth}, ${reimbursement.requested.hour}:${reimbursement.requested.minute}:${reimbursement.requested.second}`);
         console.log(reimbursement.requested);
         let text3 = document.createTextNode(`${reimbursement.amount}`);
         let text4 = document.createTextNode(`${reimbursement.description}`);
         let text5 = document.createTextNode(`${reimbursement.approver.firstName} ${reimbursement.approver.lastName}`);
         let text6 = document.createTextNode(`${reimbursement.type.type}`);
         let text7 = document.createTextNode(`${reimbursement.status.status}`);

             td1.appendChild(text1);
             td2.appendChild(text2);
             td3.appendChild(text3);
             td4.appendChild(text4);
             td5.appendChild(text5);
             td6.appendChild(text6);
             td7.appendChild(text7);

             tr.appendChild(td1);
             tr.appendChild(td2);
             tr.appendChild(td3);
             tr.appendChild(td4);
             tr.appendChild(td5);
             tr.appendChild(td6);
             tr.appendChild(td7);
             reimbursementList.appendChild(tr);
          });

          document.getElementById("counter").innerHTML =counter;

      }
}

function filterTable(){

    // Get variables 
  let filter = document.getElementById("filter").value.toUpperCase();
  let table = document.getElementById("pendingReimbursementsList");
  let tr = table.getElementsByTagName("tr");
  let i, j;
  // Loop through all rows, hide those do not fit
  for (i = 0; i < tr.length; i++) {

    loop:  for(j = 0;j<7;j++){
        td = tr[i].getElementsByTagName("td")[j];
          if (td) {
             if (td.innerHTML.toUpperCase().indexOf(filter) > -1) {
                tr[i].style.display = "";
                break loop;
           } else {
                tr[i].style.display = "none";
                
           }
        } 
        
      }
  
    }
}