(function() {
    'use strict';

    angular
        .module('jhipsterApp')
        .controller('CartItemDeleteController',CartItemDeleteController);

    CartItemDeleteController.$inject = ['$uibModalInstance', 'entity', 'CartItem'];

    function CartItemDeleteController($uibModalInstance, entity, CartItem) {
        var vm = this;
        vm.cartItem = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            CartItem.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
