document.querySelectorAll(".del").forEach(b => {
     b.addEventListener("click", del => {
        if(confirm("Deseas eliminar este restaurante?") == false){
            location.href="perfilRestaurante?id="+del.target.dataset.idUsuario;
         }
     })
 })