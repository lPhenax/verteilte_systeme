/** Main application file to start the client side single page app for the chat app
 *
 * @author Andreas Burger & Daniel Schleußner
 */

requirejs.config({
    baseUrl: "/js",
    paths: {
        jquery: './_lib/jquery-1.11.3',
        underscore: './_lib/underscore-1.8.3',
        backbone: './_lib/backbone-1.2.3'
        //client: '../client.js',
        //socketIO: 'socket.io/socket.io.js'

    },
    shim: {
        underscore: {
            exports: "_"
        },
        backbone: {
            deps: ['underscore', 'jquery'],
            exports: 'Backbone'
        }
    }
});

// AMD conform require as provided by require.js
//require(['jquery','backbone', 'models/user', 'views/video-one', 'views/user-list', 'views/user-create', 'models/user', 'models/message'],
require(['jquery', 'backbone', 'models/user', 'views/user-list', 'views/user-create',
    'models/message', 'views/message-list', 'views/message-create'],
    //function($, Backbone, User, VideoOneView, UserListView, UserCreateView) {
    function ($, Backbone, User, UserListView, UserCreateView, Message, MessageListView, MessageCreateView) {

        var AppRouter = Backbone.Router.extend({
            routes: {
                '': 'main',
                '*whatever': 'main'
            },
            main: function () {
                $('body').prepend('<h1>Chat Application</h1><br>Übung 5');
                $('#chat-app').hide();


                var userCollection = new User.Collection();
                var userListView = new UserListView({collection: userCollection});
                userCollection.fetch({
                    //error: console.error("Usersammlung ist leer."),
                    //success: console.log("Usersammlung ist nicht leer.")
                });
                var userCreateView = new UserCreateView({
                    collection: userCollection, app: this
                });

                var messageCollection = new Message.Collection();
                var messageListView = new MessageListView({collection: messageCollection});
                messageCollection.fetch();
                var messageCreateView = new MessageCreateView({
                    collection: messageCollection, app: this
                });
            }
        });

        var myRouter = new AppRouter();

        // finally start tracking URLs to make it a SinglePageApp (not really needed at the moment)
        Backbone.history.start({pushState: true}); // use new fancy URL Route mapping without #
    });
