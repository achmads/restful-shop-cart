(function() {
    'use strict';

    angular
        .module('jhipsterApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('coupon', {
            parent: 'entity',
            url: '/coupon',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'jhipsterApp.coupon.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/coupon/coupons.html',
                    controller: 'CouponController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('coupon');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('coupon-detail', {
            parent: 'entity',
            url: '/coupon/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'jhipsterApp.coupon.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/coupon/coupon-detail.html',
                    controller: 'CouponDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('coupon');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Coupon', function($stateParams, Coupon) {
                    return Coupon.get({id : $stateParams.id});
                }]
            }
        })
        .state('coupon.new', {
            parent: 'coupon',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/coupon/coupon-dialog.html',
                    controller: 'CouponDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                code: null,
                                discount: null,
                                percentage: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('coupon', null, { reload: true });
                }, function() {
                    $state.go('coupon');
                });
            }]
        })
        .state('coupon.edit', {
            parent: 'coupon',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/coupon/coupon-dialog.html',
                    controller: 'CouponDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Coupon', function(Coupon) {
                            return Coupon.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('coupon', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('coupon.delete', {
            parent: 'coupon',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/coupon/coupon-delete-dialog.html',
                    controller: 'CouponDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Coupon', function(Coupon) {
                            return Coupon.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('coupon', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
