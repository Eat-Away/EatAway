//Controla el borrado de restaurantes
document.querySelectorAll(".delRest").forEach(b => {
     b.addEventListener("click", delRest => {
        if(confirm("Deseas eliminar el restaurante "+delRest.target.dataset.nombrerestaurante+"?") == true){
            document.getElementById("delRestaurante?id="+delPlato.target.dataset.idrestaurante).submit();
         }
     })
 })
 //Controla el borrado de platos
document.querySelectorAll(".delPlato").forEach(b => {
    b.addEventListener("click", delPlato => {
       if(confirm("Deseas eliminar el plato "+delPlato.target.dataset.nombreplato+"?") == true){
           document.getElementById("delPlato?id="+delPlato.target.dataset.idplato).submit();
        }
    })
})