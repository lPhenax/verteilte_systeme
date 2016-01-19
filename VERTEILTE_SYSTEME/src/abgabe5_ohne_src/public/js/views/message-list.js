/**
 * @author Andreas Burger & Daniel Schleußner
 */

define(['backbone', 'jquery', 'underscore', 'views/message'],
    function(Backbone, $, _, MessageView) {
        var MessageListView = Backbone.View.extend({
            el: '#message-list',
            template: undefined,
            render: function() {
                this.$el.empty();
                this.collection.each(function(message) {
                    var messageView = new MessageView({model: message});
                    this.$el.prepend(messageView.render().el);

                }, this);
                return this;
            },
            initialize: function() {
                // this.collection is a Backbone Collection
                this.listenTo(this.collection,'add', this.render);
            }
        });
        return MessageListView;
    });