
var stompClient = null;
var notificationCount = 0;

function connect() {
    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/messages', function (message) {
            showMessage(JSON.parse(message.body).content);
        });
    });
}

function showMessage(message) {
    $("#messages").append("<tr><td>" + message + "</td></tr>");
}

function sendMessage() {
    console.log("sending message");
    stompClient.send("/webs/message", {}, JSON.stringify({'messageContent': $("#message").val()}));
}

document.querySelector(".send").addEventListener('click', function () {
    sendMessage();
});

$(document).ready(connect);