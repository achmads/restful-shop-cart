(function() {
    'use strict';

    angular
        .module('jhipsterApp')
        .controller('CouponDeleteController',CouponDeleteController);

    CouponDeleteController.$inject = ['$uibModalInstance', 'entity', 'Coupon'];

    function CouponDeleteController($uibModalInstance, entity, Coupon) {
        var vm = this;
        vm.coupon = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            Coupon.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
