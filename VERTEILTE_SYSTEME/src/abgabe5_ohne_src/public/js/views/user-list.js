/**
 * @author Andreas Burger & Daniel Schleußner
 */

define(['backbone', 'jquery', 'underscore', 'views/user'],
    function(Backbone, $, _, UserView) {
        var UserListView = Backbone.View.extend({
            el: '#user-list',
            template: undefined,
            render: function() {
                this.$el.empty();
                this.collection.each(function(user) {
                    var userView = new UserView({model: user});
                    this.$el.prepend(userView.render().el);

                }, this);
                return this;
            },
            initialize: function() {
                // this.collection is a Backbone Collection
                this.listenTo(this.collection,'add', this.render);
            }
        });
        return UserListView;
    });