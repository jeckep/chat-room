var ws;

function setupWebSocket(){
    clear();
    ws = new WebSocket("ws://" + location.hostname + ":" + location.port + "/chat/");
    ws.onmessage = function (msg) { updateChat(msg); };
    ws.onopen = function() {
        //Send message to load old messages
        sendMessage("LOAD_OLD_MESSAGES");
    };
    ws.onclose = function () {
        setTimeout(setupWebSocket, 500);
    };
}

setupWebSocket();


//Send message if "Send" is clicked
$("#send").click(function () {
    sendMessage($("#message").val());
});

//Send message if enter is pressed in the input field
$("#message").keypress(function (e) {
    if (e.keyCode === 13) { sendMessage($("#message").val()); }
});

//Send a message if it's not empty, then clear the input field
function sendMessage(message) {
    if (message !== "") {
        var toUserId = window.location.href.split('/').pop();
        var msg = {
            "to": toUserId,
             "message": message
        };
        ws.send(JSON.stringify(msg));
        $("#message").val("");
    }
}

function clear(){
    $("#messages").empty();
}

//Update the chat-panel, and the list of connected users
function updateChat(msg) {
    var data = JSON.parse(msg.data);
    $("#messages").append($(data.userMessage));
    setTimeout(scrollDown, 500)
}

function scrollDown(){
    var totalHeight = 0;

    $("#messages").children().each(function(){
        totalHeight = totalHeight + $(this).outerHeight(true);
    });

    $('#nc-messages').getNiceScroll(0).doScrollTop(totalHeight);
}
