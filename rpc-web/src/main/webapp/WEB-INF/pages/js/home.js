/**
 *首页controller
 **/
var app = angular.module('homeApp', [ 'ngRoute','ngTable','ui.bootstrap', 'ngDialog',
				'tm.pagination', 'ez.confirm', 'ez.focus', 'globalAlert',
				'ngCookies' ]);
app.config([ '$routeProvider', '$locationProvider',
		function($routeProvider, $locationProvider) {
			$routeProvider
			.when('/rpcServerList', {
				templateUrl : 'rpcServerList.html',
				controller : dynamicDemoController
			})
			.when('/aplNodeList', {
				templateUrl : 'aplNodeList.html',
				controller : aplNodeListController
			})
			.otherwise({
				redirectTo : '/'
			});
		} ]);
