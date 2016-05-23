(function() {
    'use strict';

    angular
        .module('jhipsterApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('cart-item', {
            parent: 'entity',
            url: '/cart-item',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'jhipsterApp.cartItem.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/cart-item/cart-items.html',
                    controller: 'CartItemController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('cartItem');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('cart-item-detail', {
            parent: 'entity',
            url: '/cart-item/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'jhipsterApp.cartItem.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/cart-item/cart-item-detail.html',
                    controller: 'CartItemDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('cartItem');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'CartItem', function($stateParams, CartItem) {
                    return CartItem.get({id : $stateParams.id});
                }]
            }
        })
        .state('cart-item.new', {
            parent: 'cart-item',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/cart-item/cart-item-dialog.html',
                    controller: 'CartItemDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                price: null,
                                quantity: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('cart-item', null, { reload: true });
                }, function() {
                    $state.go('cart-item');
                });
            }]
        })
        .state('cart-item.edit', {
            parent: 'cart-item',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/cart-item/cart-item-dialog.html',
                    controller: 'CartItemDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['CartItem', function(CartItem) {
                            return CartItem.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('cart-item', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('cart-item.delete', {
            parent: 'cart-item',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/cart-item/cart-item-delete-dialog.html',
                    controller: 'CartItemDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['CartItem', function(CartItem) {
                            return CartItem.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('cart-item', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
