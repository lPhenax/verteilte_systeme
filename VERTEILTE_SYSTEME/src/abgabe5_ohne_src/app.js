/** Main app for server to start a small chat application
 *  STATUS: in work
 *
 * Note: set your environment variables
 * NODE_ENV=development
 * DEBUG=me2*
 *
 * @author Andreas Burger & Daniel Schleußner
 * @licence CC BY-SA 4.0
 *
 */
"use strict";

var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var bodyParser = require('body-parser');

// own modules imports
var restAPIchecks = require('./restapi/request-checks.js');
var errorResponseWare = require('./restapi/error-response');
var store = require('./blackbox/store.js');
var messagesRoutes = require('./routes/messages.js');
var usersRoutes = require('./routes/users.js');

// app creation
var app = express();
var server = require('http').createServer(app);
var io = require('socket.io').listen(server);

// Middlewares *************************************************
app.use(favicon(path.join(__dirname, 'public', 'img/faviconbeuth.ico')));
app.use(express.static(path.join(__dirname, 'public')));
app.use(bodyParser.json());

// logging
app.use(function(req, res, next) {
    console.log('Request of type '+req.method + ' to URL ' + req.originalUrl);
    next();
});

// API request checks for API-version and JSON etc.
app.use(restAPIchecks);

// Routes ************************************************
app.use('/messages', messagesRoutes); // see ./routes/messages which contains all lines that were here in CodePack before
app.use('/users', usersRoutes);

// **********************************************************
// CatchAll for the rest (not found routes/resources) ********

// catch 404 and forward to error handler
app.use(function(req, res, next) {
    console.log("req: " + req.originalUrl + ", " +  req.protocol + '://' + req.get('host'));
    console.log("res: " + res.url);
    var err = new Error('Not Found');
    err.status = 404;
    next(err);
});



// Start server ****************************
server.listen(4000);

//var server = app.listen(4000, function(err) {
//    if (err !== undefined) {
//        console.log('Error on startup, ',err);
//    }
//    else {
//        //var net = require('net');
//
//        //var HOST = 'localhost';
//        //var PORT = '8090';
//        //
//        //var client = new net.Socket();
//        //var loggedIn = false;
//        //
//        //var input = {"sequence":1,"command": "login", "params":["andi"]};
//        //
//        //client.connect(PORT, HOST, function(){
//        //    console.log('connected');
//        //    client.write(JSON.stringify(input));
//        //});
//        //
//        //
//        //client.on('data', function (data) {
//        //    console.log(data.toString());
//        //});
//        //
//        //client.on('end', function(){
//        //    console.log('disconnected');
//        //});
//        console.log('Listening on port 4000');
//    }
//});


//// Websocket
io.sockets.on('connection', function (socket) {
    // der Client ist verbunden
    socket.emit('chat', { zeit: new Date(), text: 'Du bist nun mit dem Server verbunden!' });
    // wenn ein Benutzer einen Text senden
    socket.on('chat', function (data) {
        console.log("hallo user");
        // so wird dieser Text an alle anderen Benutzer gesendet
        io.sockets.emit('chat', { zeit: new Date(), name: data.name || 'Anonym', text: data.text });
    });
});

// Portnummer in die Konsole schreiben
console.log('Der Server läuft nun unter http://127.0.0.1:4000/');