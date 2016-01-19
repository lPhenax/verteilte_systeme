/**
 * @author Andreas Burger & Daniel Schleußner
 */

define(['backbone', 'jquery', 'underscore'], function(Backbone, $, _) {
    var UserView = Backbone.View.extend({

        tagName: 'li',
        className: 'user',
        template: _.template($('#user-template').text()),
        events: {
            //'click #btn2_play_pause' : 'playPause',
            //'click #btn2_reset'      : 'reset'
        },
        render: function() {
            //this.model.save();

            this.$el.html(this.template(this.model.attributes));
            return this;
        },
        initialize: function() {
            this.listenTo(this.model, 'change', this.render);
        }
    });
    return UserView;
});