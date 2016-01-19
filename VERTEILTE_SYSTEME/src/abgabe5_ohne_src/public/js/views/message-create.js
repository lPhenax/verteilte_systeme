/**
 * @author Andreas Burger & Daniel Schleußner
 */

define(['backbone', 'jquery', 'underscore'], function(Backbone, $, _) {
    const ENTER_KEY = 13;
    var MessageCreateView = Backbone.View.extend({
        el: '#chat-app',
        //template: _.template($('#user_login-template').text()),
        events: {
            'click button': 'createMessage',
            'keypress input': 'createOnEnter'
        },
        initialize: function(options) {
            this.app = options.app;  // expects a Backbone Router instance as option
        },
        createMessage: function() {
            var input   = this.$el.find('input');
            if (input.val().trim()) {
                if( (input.val() == "")){
                    alert("Wrong name-declaration...!");
                } else {
                    var message = this.collection.add({
                        command : input.val(),
                        timestamp: new Date().getHours() + ":" + new Date().getMinutes(),
                        creator: this.creator
                    });
                    input.val('');
                    console.log(JSON.stringify(message));
                    //var socket = io.connect();
                    //console.log("socket: " + socket);
                }
            }
        },
        createOnEnter: function(event) {
            // check for key = ENTER and then call createTweet

            if ( event.which === ENTER_KEY) {
                this.createMessage();
            }
        }
    });
    return MessageCreateView;
});