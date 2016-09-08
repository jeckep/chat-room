//Establish the WebSocket connection and set up event handlers
var webSocket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/chat/");
webSocket.onmessage = function (msg) { updateChat(msg); };
webSocket.onclose = function () {
// This some times causes problems if user click links, if this executes before redirect
// TODO fix it
//    window.location.reload(true);
};

//Send message to load old messages
webSocket.onopen = function() {
    sendMessage("LOAD_OLD_MESSAGES");
}

//Send message if "Send" is clicked
id("send").addEventListener("click", function () {
    sendMessage(id("message").value);
});

//Send message if enter is pressed in the input field
id("message").addEventListener("keypress", function (e) {
    if (e.keyCode === 13) { sendMessage(e.target.value); }
});



//Send a message if it's not empty, then clear the input field
function sendMessage(message) {
    if (message !== "") {
        var toUserId = window.location.href.split('/').pop();
        var msg = {
            "to": toUserId,
             "message": message
        };
        webSocket.send(JSON.stringify(msg));
        id("message").value = "";
    }
}

//Update the chat-panel, and the list of connected users
function updateChat(msg) {
    var data = JSON.parse(msg.data);
    insert("messages", data.userMessage);
}

//Helper function for inserting HTML as the first child of an element
function insert(targetId, message) {
//    id(targetId).insertAdjacentHTML("afterbegin", message);
    id(targetId).insertAdjacentHTML("beforeEnd", message);
}

//Helper function for selecting element by id
function id(id) {
    return document.getElementById(id);
}