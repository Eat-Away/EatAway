
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
function information(id,nombre) {

    document.querySelector("#datos").innerHTML= "Nombre restaurante: " + nombre;
}
document.querySelectorAll(".info").forEach(b => {
    const id = b.dataset.id;
    const nombre = b.dataset.nombre;
    b.addEventListener('click',function(){initMap(0,0);information(id,nombre)});
});
  