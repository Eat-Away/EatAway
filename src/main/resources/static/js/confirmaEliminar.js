document.querySelectorAll(".del").forEach(b => {
     b.addEventListener("click", del => {
        if(confirm("Deseas eliminar " + del.target.dataset.idRestaurante + "?") == true){
            console.log("Va a eliminar el restaurante")
         }else{
             console.log("No va a eliminar nada")
         }
     })
 })