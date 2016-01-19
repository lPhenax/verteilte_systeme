/** This file contains the module exports for
 * the routes of REST-API to handle requests for
 * resource type "messages"
 * Supports: GET, POST, PUT, DELETE
 *
 * @author Andreas Burger & Daniel Schleußner
 * @licence  CC BY-SA 4.0
 *
 * @throws  Error if a method is not supported or parameter missing or id not found
 *
 * @module routes/tweets
 * @type {Router}
 */
"use strict";

// Import needed modules
var express = require('express');

// own modules imports
var store = require('../blackbox/store.js');
var hrefDecorator = require('../hrefid-decorator.js'); // task 2.a


// ** configure routes
var messages = express.Router();

messages.get('/', function(req, res, next) {
    //
    res.json(hrefDecorator(req, '/messages', store.select('messages')));
});

messages.post('/', function(req, res, next) {
    var id = store.insert('messages', req.body); // TODO check that the element is really a tweet!
    // set code 201 "created" and send the item back
    res.status(201).json(hrefDecorator(req, 'messages', store.select('messages', id)));
});


messages.get('/:id', function(req, res, next) {
    res.json(hrefDecorator(req, '/messages', store.select('messages', req.params.id)));
});

messages.delete('/:id', function(req, res, next) {
    store.remove('messages', req.params.id);
    res.status(200).end();
});

messages.put('/:id', function(req, res, next) {
    var err = undefined;
    if (req.params.id == req.body.id) {
        store.replace('messages', req.params.id, req.body);
        res.status(200).end();
    } else {
        err = new Error("cannot replace element of id "+req.params.id+" with given element.id "+req.body.id);
        err.status = 400; // bad request
        next(err);
    }
});

module.exports = messages;
