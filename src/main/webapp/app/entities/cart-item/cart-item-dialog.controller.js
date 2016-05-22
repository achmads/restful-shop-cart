(function() {
    'use strict';

    angular
        .module('jhipsterApp')
        .controller('CartItemDialogController', CartItemDialogController);

    CartItemDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'CartItem', 'Product', 'Cart'];

    function CartItemDialogController ($timeout, $scope, $stateParams, $uibModalInstance, $q, entity, CartItem, Product, Cart) {
        var vm = this;
        vm.cartItem = entity;
        vm.products = Product.query({filter: 'cartitem-is-null'});
        $q.all([vm.cartItem.$promise, vm.products.$promise]).then(function() {
            if (!vm.cartItem.productId) {
                return $q.reject();
            }
            return Product.get({id : vm.cartItem.productId}).$promise;
        }).then(function(product) {
            vm.products.push(product);
        });
        vm.carts = Cart.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('jhipsterApp:cartItemUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.cartItem.id !== null) {
                CartItem.update(vm.cartItem, onSaveSuccess, onSaveError);
            } else {
                CartItem.save(vm.cartItem, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();
