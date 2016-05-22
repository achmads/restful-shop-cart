(function() {
    'use strict';

    angular
        .module('jhipsterApp')
        .controller('CartDeleteController',CartDeleteController);

    CartDeleteController.$inject = ['$uibModalInstance', 'entity', 'Cart'];

    function CartDeleteController($uibModalInstance, entity, Cart) {
        var vm = this;
        vm.cart = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            Cart.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
