window.onload = () =>{
    /** **/
    document.getElementById("loggedUsername").innerHTML = sessionStorage.getItem("username");



    //register event listener
    document.getElementById("createReimbursementButton").addEventListener("click", ()=>{
        let amount = document.getElementById("amount").value;
        if(amount <0){
            document.getElementById("createReimbursementMessage").innerHTML = '<span class="label label-danger label-center">NO NEGATIVE AMOUNT.</span>';
            setTimeout(() =>{ document.getElementById("createReimbursementMessage").innerHTML = '';}, 3000);
            return;
        }
        let description = document.getElementById("description").value;
       // let reimbursementTypeId = document.getElementById("reimbursementTypeId").value;
        let reimbursementTypeName = document.getElementById("reimbursementTypeName").value;
        let reimbursementTypeId;
        if(reimbursementTypeName==='COURSE'){
            reimbursementTypeId = 2;
        }
        else if(reimbursementTypeName==='CERTIFICATION'){
            reimbursementTypeId = 3;
        }
        else if(reimbursementTypeName==='TRAVELING'){
            reimbursementTypeId = 4;
        }
        else{
            reimbursementTypeId = 1;
        }

        let reimbursementImage = document.getElementById("imgInp").files[0];
        console.log(reimbursementImage);

        //create form
        let formdata = new FormData();
        formdata.append('amount',amount);
        formdata.append('description',description);
        formdata.append('reimbursementTypeName',reimbursementTypeName);
        formdata.append('reimbursementTypeId',reimbursementTypeId);
        formdata.append('reimbursementImage', reimbursementImage);

        //AJAX Logic
        let xhr = new XMLHttpRequest();

        xhr.onreadystatechange = () => {
            if(xhr.readyState === XMLHttpRequest.DONE && xhr.status ===200){
                //Getting JSON from response body
                let data = JSON.parse(xhr.responseText);

                //Cal login response processing
                createReimbursement(data);
            }
        };

  
          //Doing a HTTP to a specifc endpoint
        xhr.open("POST",`submitRequest.do?amount=${amount}&description=${description}&reimbursementTypeId=${reimbursementTypeId}&reimbursementTypeName=${reimbursementTypeName}&reimbursementImage=${reimbursementImage}`);
       
        //Sending our request
        //Below line causes me lots of issue.
      //  xhr.setRequestHeader('Content-Type', 'multipart/form-data');
        xhr.send(formdata);
      //  xhr.send();

    })
}

function disableAllComponents(){
    document.getElementById("amount").setAttribute("disabled","disabled");
    document.getElementById("description").setAttribute("disabled","disabled");
}

function createReimbursement(data) {
    
      //Confirm registratio nand redirect to login
     disableAllComponents();
  
      if(data.message === "A REIMBURSEMENT HAS BEEN CREATED SUCCESSFULLY"){
        document.getElementById("createReimbursementMessage").innerHTML = '<span class="label label-success label-center">CREATED SUCCESSFULLY.</span>';
        
      setTimeout(() =>{ window.location.replace("home.do");}, 3000);
       
      }
      else{
        document.getElementById("createReimbursementMessage").innerHTML = '<span class="label label-danger label-center">Something went wrong.</span>';
           
      }
}
