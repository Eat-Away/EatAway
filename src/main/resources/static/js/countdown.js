let table = document.getElementById('tablaPedidos');

for(let i = 1, row; row = table.rows[i]; i++){


  let caducidad = new Date(row.cells[2].textContent);
  let mes = caducidad.getDate();
  caducidad.setDate(caducidad.getMonth() +1);
  caducidad.setMonth(mes - 1);

  let interval = window.setInterval(function(){
    let today = new Date();

    let tiempoRestante = Math.round((caducidad - today) /  60000);

    row.cells[5].textContent = tiempoRestante + ' minutos';
  }, 1000);
}
