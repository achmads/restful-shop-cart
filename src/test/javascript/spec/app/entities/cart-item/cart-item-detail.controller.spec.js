'use strict';

describe('Controller Tests', function() {

    describe('CartItem Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockCartItem, MockProduct, MockCart;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockCartItem = jasmine.createSpy('MockCartItem');
            MockProduct = jasmine.createSpy('MockProduct');
            MockCart = jasmine.createSpy('MockCart');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'CartItem': MockCartItem,
                'Product': MockProduct,
                'Cart': MockCart
            };
            createController = function() {
                $injector.get('$controller')("CartItemDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'jhipsterApp:cartItemUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
