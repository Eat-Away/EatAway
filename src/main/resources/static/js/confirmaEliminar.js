document.querySelectorAll(".del").forEach(b => {
     b.addEventListener("click", del => {
        if(confirm("Deseas eliminar este restaurante?") == true){
            //let form = document.getElementById("delRestaurante");
            document.forms["delRestaurante"].submit();
            //form.submit();
         }
     })
 })