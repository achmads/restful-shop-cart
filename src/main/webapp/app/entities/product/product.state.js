(function() {
    'use strict';

    angular
        .module('jhipsterApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('product', {
            parent: 'entity',
            url: '/product',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'jhipsterApp.product.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/product/products.html',
                    controller: 'ProductController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('product');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('product-detail', {
            parent: 'entity',
            url: '/product/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'jhipsterApp.product.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/product/product-detail.html',
                    controller: 'ProductDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('product');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Product', function($stateParams, Product) {
                    return Product.get({id : $stateParams.id});
                }]
            }
        })
        .state('product.new', {
            parent: 'product',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/product/product-dialog.html',
                    controller: 'ProductDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                shortname: null,
                                name: null,
                                unit: null,
                                price: null,
                                stock: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('product', null, { reload: true });
                }, function() {
                    $state.go('product');
                });
            }]
        })
        .state('product.edit', {
            parent: 'product',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/product/product-dialog.html',
                    controller: 'ProductDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Product', function(Product) {
                            return Product.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('product', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('product.delete', {
            parent: 'product',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/product/product-delete-dialog.html',
                    controller: 'ProductDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Product', function(Product) {
                            return Product.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('product', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
