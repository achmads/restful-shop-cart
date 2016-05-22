'use strict';

describe('Controller Tests', function() {

    describe('Cart Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockCart, MockCartItem, MockCoupon;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockCart = jasmine.createSpy('MockCart');
            MockCartItem = jasmine.createSpy('MockCartItem');
            MockCoupon = jasmine.createSpy('MockCoupon');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Cart': MockCart,
                'CartItem': MockCartItem,
                'Coupon': MockCoupon
            };
            createController = function() {
                $injector.get('$controller')("CartDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'jhipsterApp:cartUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
