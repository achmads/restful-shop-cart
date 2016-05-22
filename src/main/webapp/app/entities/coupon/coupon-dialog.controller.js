(function() {
    'use strict';

    angular
        .module('jhipsterApp')
        .controller('CouponDialogController', CouponDialogController);

    CouponDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Coupon'];

    function CouponDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Coupon) {
        var vm = this;
        vm.coupon = entity;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('jhipsterApp:couponUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.coupon.id !== null) {
                Coupon.update(vm.coupon, onSaveSuccess, onSaveError);
            } else {
                Coupon.save(vm.coupon, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();
