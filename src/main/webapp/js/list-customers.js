window.onload = () =>{

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
              console.log(data);   

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
          // we present all customers to the user
          //Get customer lsit node
      let employeeList = document.getElementById("employeeList");
          //Clean customer list
      employeeList.innerHTML="";
      //Iterate over all customers
      data.forEach((employee)=>{
        //creating node of <li>
        let employeeNode = document.createElement("button");

        //Add class for styling
        employeeNode.className = "list-group-item list-group-item-action text-center";

        //creating innerHml object, adding customer name to it
        //<li class = "something">[creating this object] </li>
        var employeeNodeText = document.createTextNode(`${employee.firstName} ${employee.lastName}`);

        //Append innerHTML to the customerNode
        //<li class = "something">jj,jj </li>
        employeeNode.appendChild(employeeNodeText);

        //Finally, we append the new customerNode to the customerList
        //<ul id="customerList">
        //<li class = "something">jj,jj </li>        
        //</ul>
        employeeList.appendChild(employeeNode);
      });
          
      }
}