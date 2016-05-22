(function() {
    'use strict';

    angular
        .module('jhipsterApp')
        .controller('CartDialogController', CartDialogController);

    CartDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'Cart', 'CartItem', 'Coupon'];

    function CartDialogController ($timeout, $scope, $stateParams, $uibModalInstance, $q, entity, Cart, CartItem, Coupon) {
        var vm = this;
        vm.cart = entity;
        vm.cartitems = CartItem.query();
        vm.coupons = Coupon.query({filter: 'cart-is-null'});
        $q.all([vm.cart.$promise, vm.coupons.$promise]).then(function() {
            if (!vm.cart.coupon || !vm.cart.coupon.id) {
                return $q.reject();
            }
            return Coupon.get({id : vm.cart.coupon.id}).$promise;
        }).then(function(coupon) {
            vm.coupons.push(coupon);
        });

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('jhipsterApp:cartUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.cart.id !== null) {
                Cart.update(vm.cart, onSaveSuccess, onSaveError);
            } else {
                Cart.save(vm.cart, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();
