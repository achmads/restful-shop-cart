(function() {
    'use strict';
    angular
        .module('jhipsterApp')
        .factory('CartItem', CartItem);

    CartItem.$inject = ['$resource'];

    function CartItem ($resource) {
        var resourceUrl =  'api/cart-items/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
