
function initMap(lat, lng) {
    var map = new google.maps.Map(document.getElementById('map'), {
      center: {lat, lng},
      zoom: 13,
    });
    var marker = new google.maps.Marker({
      position: {lat, lng},
      map: map
    });
}
function information(id,nombre,dirEntrega,direccion,firstName,lastName) {

    document.querySelector("#datos").innerHTML= 
    "Nombre restaurante: " + nombre +"<br />" +
    "Direccion del restaurante: " + direccion + "<br />" +
    "<br />" +
    "Nombre Cliente: " + firstName +" "+ lastName+"<br />" +
    "Direccion de la entrega: " + dirEntrega + "<br />" ;
    ;
}
document.querySelectorAll(".info").forEach(b => {
    const id = b.dataset.id;
    const lat = b.dataset.lat;
    const lng = b.dataset.lng;
    const dirEntrega = b.dataset.dir;
    const nombre = b.dataset.nombre;
    const direccion = b.dataset.direccion;
    const firstName = b.dataset.nom;
    const lastName = b.dataset.apellido;
    
    b.addEventListener('click',function(){alert(lat);initMap(lat,lng);information(id,nombre,dirEntrega,direccion,firstName,lastName)});
});
  