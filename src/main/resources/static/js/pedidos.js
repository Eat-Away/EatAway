// cómo pintar 1 mensaje (devuelve html que se puede insertar en un div)
function renderMsg(msg) {
    console.log("rendering: ", msg);
    return `<tr>
        <th scope="row">${msg.id}</th>
        <th scope="col">${msg.stat}</th>
        <td>${msg.cliente}</td>
        <td>${msg.dir}</td>
        <td>${msg.fecha}</td>
        <td>${msg.precio}</td>
        <td>Platos (Pendiente)</td>
        <td>Acciones (Pendiente)</td>
    </tr>`;
}

// pinta mensajes viejos al cargarse, via AJAX
let messageDiv = document.getElementById("pedidos");
go(config.rootUrl + "/pedidos?"+window.location.search.substring(1), "GET").then(ms =>
    ms.forEach(m => messageDiv.insertAdjacentHTML("beforeend", renderMsg(m)))
);

// y aquí pinta mensajes según van llegando
if (ws.receive) {
    const oldFn = ws.receive; // guarda referencia a manejador anterior
    ws.receive = (m) => {
        oldFn(m); // llama al manejador anterior
        messageDiv.insertAdjacentHTML("beforeend", renderMsg(m));
    }
}