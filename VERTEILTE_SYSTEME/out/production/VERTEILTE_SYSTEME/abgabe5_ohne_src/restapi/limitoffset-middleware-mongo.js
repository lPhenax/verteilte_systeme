/** This module defines a express.Router() instance
 * - supporting offset=<number> and limit=<number>*
 * - calls next with error if a impossible offset and/or limit value is given
 *
 *  Note: it expects to be called BEFORE any data fetched from DB
 *  Note: it sets an object { limit: 0, skip: 0 } with the proper number values in req.locals.limitskip
 *  Note: it sets an Error-Object to next with error.status set to HTTP status code 400
 *
 * @author Johannes Konert
 * @licence CC BY-SA 4.0
 *
 * @module restapi/limitoffset-middleware-mongo
 * @type {Router}
 */

// remember: in modules you have 3 variables given by CommonJS
// 1.) require() function
// 2.) module.exports
// 3.) exports (which is module.exports)
"use strict";

var router = require('express').Router();
var logger = require('debug')('me2:offsetlimit');


// the exported router with handler
router.use(function(req, res, next) {
    var offset = undefined;
    var limit = undefined;
    var offsetString = req.query.offset;
    var limitString = req.query.limit;
    var err = null;


    if (offsetString) {
        if (!isNaN(offsetString)) {
            offset = parseInt(offsetString);
            if (offset < 0) { err = new Error('offset is negative')}
        }
        else {
            err = new Error('given offset is not a valid number '+ offsetString);
        }
    }
    if (limitString) {
        if (!isNaN(limitString)) {
            limit = parseInt(limitString);
            if (limit < 1 ) { err = new Error('limit is zero or negative')}
        }
        else {
            err = new Error('given limit is not a valid number ' + limitString);
        }
    }
    if (err) {
        logger('problem occurred with limit/offset values');
        err.status = 400;
        next(err)
    } else {
        res.locals.limitskip = { }; // mongoDB uses parameter object for skip/limit
        if (limit) res.locals.limitskip.limit = limit;
        if (offset) res.locals.limitskip.skip = offset;
        next()
    }
});

module.exports = router;