//Controla el borrado de restaurantes
document.querySelectorAll(".delRest").forEach(b => {
     b.addEventListener("click", delRest => {
        if(confirm("Deseas eliminar el restaurante "+delRest.target.dataset.nombrerestaurante+"?") == true){
            document.getElementById("delRestaurante?id="+delRest.target.dataset.idrestaurante).submit();
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
 //Controla el borrado de extras
 document.querySelectorAll(".delExtra").forEach(b => {
    b.addEventListener("click", delExtra => {
       if(confirm("Deseas eliminar el extra "+delExtra.target.dataset.nombreextra+"?") == true){
           document.getElementById("delExtra?id="+delExtra.target.dataset.idextra).submit();
        }
    })
})
//Controla la actualizacion de estado de pedidos
document.querySelectorAll(".updatePedido").forEach(b => {
    b.addEventListener("click", updatePedido => {
       if(confirm("Deseas actualizar el estado del pedido "+updatePedido.target.dataset.idpedido+" al estado "+updatePedido.target.dataset.nuevoestado+"?") == true){
           document.getElementById("updatePedido?id="+updatePedido.target.dataset.idpedido).submit();
        }
    })
})