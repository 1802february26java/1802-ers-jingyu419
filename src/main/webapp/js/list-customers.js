window.onload = () =>{

    document.getElementById("loggedUsername").innerHTML = sessionStorage.getItem("username");
    /** **/
    //Get event listener
    document.getElementById("getEmployees").addEventListener("click", getAllEmployees);
    //Get all employees as soon as the page loads
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

      let employeeList = document.getElementById("employeeList");
      employeeList.innerHTML="";

      data.forEach((employee)=>{
        
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
        
      }
}

function viewReimbursements(obj){
    let rowData = obj.parentNode;
    console.log(rowData.childNodes[0].innerHTML);
    sessionStorage.setItem("selectedEmployeeId", rowData.childNodes[0].innerHTML);
    window.location.replace("multipleRequests.do?fetch=viewSelected");
}