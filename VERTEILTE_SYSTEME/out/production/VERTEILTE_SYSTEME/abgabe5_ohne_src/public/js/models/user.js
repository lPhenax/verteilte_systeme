/**
 * @author Andreas Burger & Daniel Schleußner
 */

define(['backbone', 'underscore'], function(Backbone, _) {
    var result = {};
    var userSchema = {
        urlRoot: '/users', // not really needed if collection exists
        idAttribute: "id",
        defaults: {
            name:       ''
        },
        initialize: function() {
            // after constructor code
        },
        validate: function(attr) {
            if (_.isEmpty(attr.name)) {
                return "Missing Name";
            }
        }

    };

    var UserModel = Backbone.Model.extend(userSchema);

    var UserCollection = Backbone.Collection.extend({
        model: UserModel,
        url: 'users',
        initialize: function() {
            this.on('add', function(user) {
                if (user.isValid() && user.isNew()) {
                    user.save();
                }
            })
        }
    });


    result.Model = UserModel;
    result.Collection = UserCollection;
    return result;
});