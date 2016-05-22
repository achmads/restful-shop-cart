(function() {
    'use strict';

    angular
        .module('jhipsterApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('cart', {
            parent: 'entity',
            url: '/cart',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'jhipsterApp.cart.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/cart/carts.html',
                    controller: 'CartController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('cart');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('cart-detail', {
            parent: 'entity',
            url: '/cart/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'jhipsterApp.cart.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/cart/cart-detail.html',
                    controller: 'CartDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('cart');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Cart', function($stateParams, Cart) {
                    return Cart.get({id : $stateParams.id});
                }]
            }
        })
        .state('cart.new', {
            parent: 'cart',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/cart/cart-dialog.html',
                    controller: 'CartDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                amount: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('cart', null, { reload: true });
                }, function() {
                    $state.go('cart');
                });
            }]
        })
        .state('cart.edit', {
            parent: 'cart',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/cart/cart-dialog.html',
                    controller: 'CartDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Cart', function(Cart) {
                            return Cart.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('cart', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('cart.delete', {
            parent: 'cart',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/cart/cart-delete-dialog.html',
                    controller: 'CartDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Cart', function(Cart) {
                            return Cart.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('cart', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
