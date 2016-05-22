(function() {
    'use strict';

    angular
        .module('jhipsterApp')
        .controller('CartItemController', CartItemController);

    CartItemController.$inject = ['$scope', '$state', 'CartItem'];

    function CartItemController ($scope, $state, CartItem) {
        var vm = this;
        vm.cartItems = [];
        vm.loadAll = function() {
            CartItem.query(function(result) {
                vm.cartItems = result;
            });
        };

        vm.loadAll();
        
    }
})();
