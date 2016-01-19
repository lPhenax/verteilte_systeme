/**
 * @author Andreas Burger & Daniel Schleußner
 */

define(['backbone', 'jquery', 'underscore'], function(Backbone, $, _) {
    const ENTER_KEY = 13;
    var UserCreateView = Backbone.View.extend({
        el: '#new-user',
        //template: _.template($('#user_login-template').text()),
        events: {
            'click button': 'createUser',
            'keypress input': 'createOnEnter'
        },
        initialize: function(options) {
            this.app = options.app;  // expects a Backbone Router instance as option
        },
        createUser: function() {
            console.log("create user");
            //var name    = $('#username');
            var input   = this.$el.find('input');
            if (input.val().trim()) {
                if( (input.val() == "")){
                    alert("Wrong name-declaration...!");
                } else {
                    console.log(input.val() + " hat sich angemeldet!");
                    var user = this.collection.add({
                        name : input.val()
                    });
                    input.val('');
                }
            }
            $('#new-user').hide();
            $('#chat-app').show();
        },
        createOnEnter: function(event) {
            // check for key = ENTER and then call createTweet

            if ( event.which === ENTER_KEY) {
                this.createUser();
            }
        }
    });
    return UserCreateView;
});