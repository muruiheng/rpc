/**
 * 定义验证用户权限的服务
 */
app.config(["$cookiesProvider",cookiesFn ])
function cookiesFn($cookiesProvider) {
    $cookiesProvider.defaults = {
        path: "/"
    };
};
app.factory('authService', function($http) {
	return {
		isUrlAccessibleForUser : function(menuList, route) {
			if(null==route){
				return true;
			}
			if (menuList) {
				for (var j = 0; j < menuList.length; j++) {
					if (menuList[j] == route)
						return true;
				}
			}
			return false;
		}
	};
});
var authMenus =[];
/**
 * 动态菜单管理逻辑
 */
app.controller('homeController', function($scope,
		$rootScope, $http, $location, $cookies,$cookieStore, authService) {
	var menuUrl =applicationContext +"/uidm001/01";
	var url_local = window.location.href;
/*	$scope.sysVar =true;
	$scope.sysShow = function(){
		$scope.sysVar=!$scope.sysVar;
	}*/
	$scope.getSys = function() {
		post($http,menuUrl,{},function(response){
			$scope.sysList = response.sysList;
			$scope.result = response.menus;
			var list = $scope.sysList;
			if(undefined==list){
				return;
			}
			for(var i=0;i<list.length;i++){
				if(-1==list[i].sysUrl.search("views/home.html")){
					list[i].sysUrl = list[i].sysUrl+"views/home.html";
				}
				if(url_local == list[i].sysUrl){
					$scope.getMenus(list[i]);
				}
			}
		},function(message){
			alert(message);
		});
	}
	$scope.getSys();
	$rootScope.currentUserName = $cookies.get("username");
	/**
	 * 判断是否跳转页面
	 */
	$scope.sysTog = false;
	$scope.getMenus = function(sys) {
		var url_local = window.location.href;
		sys.menuList =[];
		if('#' ==url_local[url_local.length-1]){
			url_local = url_local.substring(0,url_local.length-1);
		}
		if(-1==sys.sysUrl.search("views/home.html")){
			sys.sysUrl = sys.sysUrl+"views/home.html";
		}
		if(url_local == sys.sysUrl){
				$scope.sysTog = !$scope.sysTog;
				if(undefined!=$scope.result){
					for(var i=0;i<$scope.result.length;i++){
						if(sys.sysId== $scope.result[i].sysId){
							sys.menuList.push($scope.result[i]);
						}
					}
				}
				authMenus=sys.menuList;
			return false;
		}
		return true;
	};
	$scope.changeClass = function(menu){
 		$scope.selectedMenu=menu;
	};
	/**
	 * 监听路由变化，验证用户权限
	 */
	$scope.$on("$routeChangeStart", function(event, next, current) {
		var authList = [];
		if (undefined == authMenus||null==authMenus||''==authMenus) {
			return;
		}
		for (var i = 0; i < authMenus.length; i++) {
			authList[i] = authMenus[i].itemUrlId;
		}
		if (!authService.isUrlAccessibleForUser(authList, next.originalPath))
			$location.path('/Error');
	});
});