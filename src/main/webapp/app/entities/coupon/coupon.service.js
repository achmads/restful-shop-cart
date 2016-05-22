(function() {
    'use strict';
    angular
        .module('jhipsterApp')
        .factory('Coupon', Coupon);

    Coupon.$inject = ['$resource'];

    function Coupon ($resource) {
        var resourceUrl =  'api/coupons/:id';

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
