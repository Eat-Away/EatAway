document.querySelectorAll(".del").foreach(b => {
     b.addEventListener("click", del => {
         console.log(confirm("Deseas eliminar " + del.target.dataset.idRestaurante + "?"))
     })
 })