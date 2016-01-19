/**
 * @author Andreas Burger & Daniel Schleußner
 */

define(['backbone', 'jquery', 'underscore'], function(Backbone, $, _) {
    var MessageView = Backbone.View.extend({

        tagName: 'li',
        className: 'message',
        template: _.template($('#message-list-template').text()),
        render: function() {

            this.$el.html(this.template(this.model.attributes));
            return this;
        },
        initialize: function() {
            this.listenTo(this.model, 'change', this.render);
        }
    });
    return MessageView;
});