
function initMap(latitud, longitud) {
    var map = new google.maps.Map(document.getElementById('map'), {
      center: {lat: latitud, lng: longitud},
      zoom: 13,
    });
    var marker = new google.maps.Marker({
      position: {lat: latitud, lng: longitud},
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
    const lat = parseFloat(b.dataset.lat);
    const lng = parseFloat(b.dataset.lng);
    const dirEntrega = b.dataset.dir;
    const nombre = b.dataset.nombre;
    const direccion = b.dataset.direccion;
    const firstName = b.dataset.nom;
    const lastName = b.dataset.apellido;
    
    b.addEventListener('click', function () {
      console.log(lat + " " + lng);
      initMap(lat,lng);
      information(id,nombre,dirEntrega,direccion,firstName,lastName);
    });
});

  