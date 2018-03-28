window.onload = () =>{

    document.getElementById("loggedUsername").innerHTML = sessionStorage.getItem("username");
    /** **/
    //Get event listener
    document.getElementById("getEmployees").addEventListener("click", getAllEmployees);
    //Get all employees as soon as the page loads

    //filter
    document.getElementById("filter").addEventListener("keyup",filterTable);

    getAllEmployees();
}

function getAllEmployees(){
      //AJAX Logic
      let xhr = new XMLHttpRequest();

      xhr.onreadystatechange = () => {
          if(xhr.readyState === XMLHttpRequest.DONE && xhr.status ===200){
              //Getting JSON from response body
              let data = JSON.parse(xhr.responseText);
 

              //present the data to the user
              presentEmployees(data);
          }
      };
        //Doing a HTTP to a specifc endpoint
        xhr.open("GET",`viewAllEmployee.do?fetch=LIST`);
 
   //Sending our request
   xhr.send();
}

function presentEmployees(data) {
     
    //If message is a member of the JSON, something went wrong
      if(data.message){
          document.getElementById("listMessage").innerHTML = '<span class="label label-danger label-center">Something went wrong.</span>';
      }
      else{
 //count how many reimbursements we have
    let counter = 0; 
      let employeeList = document.getElementById("employeeList");
      employeeList.innerHTML="";

      data.forEach((employee)=>{
        counter = counter + 1;
        let tr = document.createElement('tr');  
        let td1 = document.createElement('td'); 
        let td2 = document.createElement('td'); 
        let td3 = document.createElement('td'); 
        let td4 = document.createElement('td'); 
        let td5 = document.createElement('td'); 
        let button = document.createElement('button'); 
        
        let text1 = document.createTextNode(`${employee.id}`);
        let text2 = document.createTextNode(`${employee.firstName}`);
        let text3 = document.createTextNode(`${employee.lastName}`);
        let text4 = document.createTextNode(`${employee.username}`);
        let text5 = document.createTextNode(`${employee.email}`);
        let textButton = document.createTextNode('view Reimbursements');
        button.className = 'btn btn-md btn-primary';
        button.setAttribute('onclick','viewReimbursements(this)');

        td1.appendChild(text1);
        td2.appendChild(text2);
        td3.appendChild(text3);
        td4.appendChild(text4);
        td5.appendChild(text5);
        button.appendChild(textButton);

        tr.appendChild(td1);
        tr.appendChild(td2);
        tr.appendChild(td3);
        tr.appendChild(td4);
        tr.appendChild(td5);
        tr.appendChild(button);
        employeeList.appendChild(tr);
      });
      document.getElementById("counter").innerHTML =counter;   
      }
}

function viewReimbursements(obj){
    let rowData = obj.parentNode;
    console.log(rowData.childNodes[0].innerHTML);
    sessionStorage.setItem("selectedEmployeeId", rowData.childNodes[0].innerHTML);
    window.location.replace("multipleRequests.do?fetch=viewSelected");
}

function filterTable(){

    // Get variables 
  let filter = document.getElementById("filter").value.toUpperCase();
  let table = document.getElementById("employeeList");
  let tr = table.getElementsByTagName("tr");
  let i, j;
  // Loop through all rows, hide those do not fit
  for (i = 0; i < tr.length; i++) {

  loop:  for(j = 0;j<5;j++){
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