(function() {
    'use strict';

    angular
        .module('jhipsterApp')
        .controller('CartDetailController', CartDetailController);

    CartDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Cart', 'CartItem', 'Coupon'];

    function CartDetailController($scope, $rootScope, $stateParams, entity, Cart, CartItem, Coupon) {
        var vm = this;
        vm.cart = entity;
        
        var unsubscribe = $rootScope.$on('jhipsterApp:cartUpdate', function(event, result) {
            vm.cart = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
