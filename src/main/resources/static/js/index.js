let $chat = $('#chat');
let $txtMessage = $('#txtMessage');

$txtMessage.on('keypress', function (e) {
    if (e.which === 13) {
        sendMessage();
    }
});

let socket = new SockJS('/ws');
let stompClient = Stomp.over(socket);

stompClient.connect({}, function (frame) {
    stompClient.subscribe('/user/message', function (data) {
        let message = data.body;

        let $li = $('<li/>');
        let $div = $('<div/>')
            .append(message);

        $chat.append($li.append($div));
    });
});

function sendMessage() {
    let message = $txtMessage.val();
    send(message);
}

function send(message) {
    $.ajax('/api/send', {
        type: 'POST',
        data: message,
        contentType: 'text/plain',
        xhrFields: {
            withCredentials: true
        },
        success: function (response) {
            let $li = $('<li/>').addClass('me');
            let $div = $('<div/>')
                .append(message);

            $chat.append($li.append($div));
            $txtMessage.val('');
        }
    });
}