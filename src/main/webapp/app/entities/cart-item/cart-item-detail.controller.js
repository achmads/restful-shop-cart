(function() {
    'use strict';

    angular
        .module('jhipsterApp')
        .controller('CartItemDetailController', CartItemDetailController);

    CartItemDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'CartItem', 'Product', 'Cart'];

    function CartItemDetailController($scope, $rootScope, $stateParams, entity, CartItem, Product, Cart) {
        var vm = this;
        vm.cartItem = entity;
        
        var unsubscribe = $rootScope.$on('jhipsterApp:cartItemUpdate', function(event, result) {
            vm.cartItem = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
