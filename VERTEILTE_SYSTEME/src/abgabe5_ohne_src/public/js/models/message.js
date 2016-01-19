/**
 * @author Andreas Burger & Daniel Schleußner
 */

define(['backbone', 'underscore'], function(Backbone, _) {
    var result = {};
    var messageSchema = {
        urlRoot: '/messages', // not really needed if collection exists
        idAttribute: "id",
        defaults: {
            command:    '',
            timestamp:  ''
        },
        initialize: function() {
            // after constructor code
        },
        validate: function(attr) {
            if (_.isEmpty(attr.command)) {
                return "Missing Command";
            }
        }

    };

    var MessageModel = Backbone.Model.extend(messageSchema);

    var MessageCollection = Backbone.Collection.extend({
        model: MessageModel,
        url: 'messages',
        initialize: function() {
            this.on('add', function(message) {
                if (message.isValid() && message.isNew()) {
                    message.save();
                }
            })
        }
    });


    result.Model = MessageModel;
    result.Collection = MessageCollection;
    return result;
});