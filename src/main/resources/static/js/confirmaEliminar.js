//Controla el borrado de restaurantes
/* Agregar un detector de eventos al botón con la clase delRest. Cuando se hace clic en el botón, se le
preguntará al usuario si desea eliminar el restaurante. Si hacen clic en sí, se enviará el
formulario con el id delRestaurante?id=<id del restaurante>. */
document.querySelectorAll(".delRest").forEach(b => {
     b.addEventListener("click", delRest => {
        if(confirm("Deseas eliminar el restaurante "+delRest.target.dataset.nombrerestaurante+"?") == true){
            document.getElementById("delRestaurante?id="+delRest.target.dataset.idrestaurante).submit();
         }
     })
})
 //Controla el borrado de platos
/* Agregando un detector de eventos al botón con la clase delPlato. Cuando se hace clic en el botón, se
le preguntará al usuario si desea eliminar el plato. Si hacen clic en sí, se enviará
el formulario con el id delPlato?id=<id del plato>. */
document.querySelectorAll(".delPlato").forEach(b => {
    b.addEventListener("click", delPlato => {
       if(confirm("Deseas eliminar el plato "+delPlato.target.dataset.nombreplato+"?") == true){
           document.getElementById("delPlato?id="+delPlato.target.dataset.idplato).submit();
        }
    })
})
 //Controla el borrado de extras
/* Agregar un detector de eventos al botón con la clase delExtra. Cuando se hace clic en el botón, se
le preguntará al usuario si desea eliminar el extra. Si hacen clic en sí, se enviará
el formulario con el id delExtra?id=<id del extra>. */
document.querySelectorAll(".delExtra").forEach(b => {
    b.addEventListener("click", delExtra => {
       if(confirm("Deseas eliminar el extra "+delExtra.target.dataset.nombreextra+"?") == true){
           document.getElementById("delExtra?id="+delExtra.target.dataset.idextra).submit();
        }
    })
})
//Controla la actualizacion de estado de pedidos
/* Agregar un detector de eventos al botón con la clase updatePedido. Cuando se hace clic en el botón,
se le preguntará al usuario si desea actualizar el estado del pedido al nuevo estado. Si hacen clic en
sí, se enviará el formulario con el id updatePedido?id=<id del pedido>. */
document.querySelectorAll(".updatePedido").forEach(b => {
    b.addEventListener("click", updatePedido => {
       if(confirm("Deseas actualizar el estado del pedido "+updatePedido.target.dataset.idpedido+" al estado "+updatePedido.target.dataset.nuevoestado+"?") == true){
           document.getElementById("updatePedido?id="+updatePedido.target.dataset.idpedido).submit();
        }
    })
})
//Controla el bloqueo/desbloqueo de usuarios del sistema
/* Agregar un detector de eventos al botón con la clase banUser. Cuando se hace clic en el botón, se le
preguntará al usuario si desea bloquear la cuenta de usuario. Si hacen clic en sí, se enviará el
formulario con el id banUser?id=<id del usuario>. */
document.querySelectorAll(".banUser").forEach(b => {
    b.addEventListener("click", updatePedido => {
       if(confirm("Deseas bloquear la cuenta del usuario "+updatePedido.target.dataset.nombreusuario+"?") == true){
           document.getElementById("banUser?id="+updatePedido.target.dataset.idusr).submit();
        }
    })
})
/* Agregar un detector de eventos al botón con la clase unbanUser. Cuando se hace clic en el botón, se
le preguntará al usuario si desea desbloquear la cuenta de usuario. Si hacen clic en sí, se enviará
el formulario con el id unbanUser?id=<id del usuario>. */
document.querySelectorAll(".unbanUser").forEach(b => {
    b.addEventListener("click", updatePedido => {
       if(confirm("Deseas desbloquear la cuenta del usuario "+updatePedido.target.dataset.nombreusuario+"?") == true){
           document.getElementById("unbanUser?id="+updatePedido.target.dataset.idusr).submit();
        }
    })
})
//Controla el borrado de la imagen del usuario desde el admin
document.querySelectorAll(".delPic").forEach(b => {
    b.addEventListener("click", updatePedido => {
       if(confirm("Deseas borrar la foto del usuario "+updatePedido.target.dataset.nombreusuario+"?") == true){
           document.getElementById("delPic?id="+updatePedido.target.dataset.idusr).submit();
        }
    })
})