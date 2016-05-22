(function() {
    'use strict';

    angular
        .module('jhipsterApp')
        .controller('CartController', CartController);

    CartController.$inject = ['$scope', '$state', 'Cart'];

    function CartController ($scope, $state, Cart) {
        var vm = this;
        vm.carts = [];
        vm.loadAll = function() {
            Cart.query(function(result) {
                vm.carts = result;
            });
        };

        vm.loadAll();
        
    }
})();
