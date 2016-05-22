(function() {
    'use strict';

    angular
        .module('jhipsterApp')
        .controller('CouponController', CouponController);

    CouponController.$inject = ['$scope', '$state', 'Coupon'];

    function CouponController ($scope, $state, Coupon) {
        var vm = this;
        vm.coupons = [];
        vm.loadAll = function() {
            Coupon.query(function(result) {
                vm.coupons = result;
            });
        };

        vm.loadAll();
        
    }
})();
