
// envio de mensajes con AJAX
let b = document.getElementById("sendmsg");
b.onclick = (e) => {
    e.preventDefault();
    go(b.parentNode.action, 'POST', {
        message: document.getElementById("message").value
    })
    .then(printSent())
    .catch(e => console.log("sad", e));
    document.getElementById("message").value = "";
}

// cómo pintar 1 mensaje (devuelve html que se puede insertar en un div)
function renderMsg(msg) {
    console.log("rendering: ", msg);
    return `<div>${msg.from} @${msg.sent}: ${msg.text}</div>`;
}

// pinta mensajes viejos al cargarse, via AJAX
let messageDiv = document.getElementById("mensajes");
go(config.rootUrl + "/user/received", "GET").then(ms =>
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
//Pinta el mensaje que se haya enviado
function printSent(){
    document.getElementById("mensajes").innerHTML = "";
    go(config.rootUrl + "/user/received", "GET");
    go(config.rootUrl + "/user/received", "GET").then(ms =>
        ms.forEach(m => messageDiv.insertAdjacentHTML("beforeend", renderMsg(m)))
    );
}