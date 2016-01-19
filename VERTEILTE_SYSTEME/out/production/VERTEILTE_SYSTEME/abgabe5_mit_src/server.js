var express = require('express')
    , app = express()
    , server = require('http').createServer(app)
    , io = require('socket.io').listen(server)
    , conf = require('./config.json')
    , net = require('net')
    , HOST = 'localhost'
    , PORT = 8090
    , client
    , json
    ;

// Webserver
// auf den Port x schalten
server.listen(conf.port);

// statische Dateien ausliefern
app.configure(function() {
    app.use(express.static(__dirname + '/public'));
});

//// wenn der Pfad / aufgerufen wird
app.get('/', function (req, res) {
    //console.log(req);
    //so wird die Datei index.html ausgegeben
    res.sendfile(__dirname + '/public/index.html');
});

// Websocket
io.sockets.on('connection', function (socket) {

    try {
        //client = net.connect(PORT, HOST, function() {
        //    console.log("connected on "+HOST+":"+PORT);
        //});
        client = new net.Socket();
        client.connect(PORT, HOST, function () {
            console.log("connected on " + HOST + ":" + PORT);
        })
    } catch (err) {
        console.log("Hey Fehler...");
        console.log(err);
    }

    //var input = {"sequence": 1, "command": "login", "params": ["Daniel"]};

    client.on('data', function(data){
        json = JSON.parse(data.toString());
        client.emit('chat', {
            zeit: new Date(),
            text: json.response[0]
        });
    });

    socket.on('chat', function(data){
        var i = data.text;
        console.log(i);
        var cmd = i.split(" ");
        i = i.replace(cmd + " ", "");
        console.log(cmd[0] + ", " + i);
        var input = {"sequence": Math.random() * 1000, "command": cmd[0], "params": [cmd[1]]};
        console.log(JSON.stringify(input));
        client.write(JSON.stringify(input));
    });

    client.on('data', function(data){
        json = JSON.parse(data.toString());
        socket.emit('chat', {
            zeit: new Date(),
            text: json.response[0]
        });
    });

    //socket.emit('chat', {
    //        zeit: new Date(),
    //        text: 'Du bist nun mit dem Server verbunden!'
    //    }
    //);










    client.on('data', function(data){
        json = JSON.parse(data.toString());
        socket.emit('chat', {
            zeit: new Date(),
            text: json.response[0]
        });
    });


    // wenn ein Benutzer einen Text senden
    ////socket.on('chat', function (data) {
    ////
    ////    console.log(data.text);
    ////    client.write(JSON.stringify(data.text));
    ////    /////////////////////////////
    ////    //data an TCP-Server schicken
    ////    /////////////////////////////
    ////    client.on('data', function(data){
    ////        var json = JSON.parse(data.toString());
    ////        client.emit('chat', {
    ////            zeit: new Date(),
    ////            text: json.response[0]
    ////        })
    ////    });
    ////
    ////
    ////    // so wird dieser Text an alle anderen Benutzer gesendet
    ////    io.sockets.emit('chat', {
    ////        zeit: new Date(),
    ////        text: data.text
    ////    });
    //});
});

// Portnummer in die Konsole schreiben
console.log('Der Server läuft nun unter http://127.0.0.1:' + conf.port + '/');
