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
		$rootScope, $http, $location,ngDialog, $cookies,$cookieStore, authService,commAlertService) {
	var menuUrl =applicationContext +"/uidm001/01";
	var url_local = window.location.href;
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
					$scope.isSelectSys = i;
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
	$scope.changeClose = false;
	$scope.changeOpen = false;
	$scope.openMenus = function(aa,sys){
		aa.changeClose = !aa.changeClose;
		aa.changeOpen = !aa.changeOpen;
		$scope.getMenus(sys);
	}
	$scope.sysTog = false;
	$scope.getMenus = function(sys) {
		var url_local = window.location.href;
		if(undefined!=sys.menuList){
			$scope.sysTog = !$scope.sysTog;
			authMenus=sys.menuList;
			if(7==sys.sysId){
				sys.sysUrl = sys.sysUrl+"#/userFrist"	;
			}
			return false;
		}
		if(-1==sys.sysUrl.search("views/home.html")){
			sys.sysUrl = sys.sysUrl+"views/home.html";
		}
		if(-1!=url_local.search("#")){
			var i = url_local.indexOf("#");
			url_local = url_local.substring(0,i);
		}
		sys.menuList =[];
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
	//修改密码功能
	$scope.updPwd = function(){
		$scope.oldPwd='';
		$scope.newPwd='';
		$scope.conNewPwd='';
		var dialog = ngDialog.open({
			template : './pwdUpd.html',
			className : 'ngdialog-theme-dialog ngdialog-theme-custom',
			controller : 'pwdController',
			disableAnimation : true,
			name : 'pwdUpd',
			scope : $scope,

		});
	}
	
	
	
	$scope.userInfo = function(){
		var dialog = ngDialog.open({
			template : './userInfo.html',
			className : 'ngdialog-theme-window ngdialog-theme-custom ',
			controller : 'usersController',
			disableAnimation : true,
			name : 'userInfo',
			scope : $scope,

		});

	}
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
		if (next && !authService.isUrlAccessibleForUser(authList, next.originalPath))
			$location.path('/Error');
	});
});
app.controller('usersController', [
                              		'$scope',
                              		'$rootScope',
                              		'$http',
                              		'ngDialog',
                              		'EzConfirm',
                              		'commAlertService',
                              		function($scope, $rootScope, $http, ngDialog, EzConfirm,
                              				commAlertService) {
	var url = applicationContext + "/uid006/08";
	post($http, url, {}, function(values) {
		$scope.curUser = values.curUser;
		if(values.curUser.status == 0){
			$scope.zcShow = true;
		}else{
			$scope.zcShow = false;
		}
		if(values.curUser.status == 1){
			$scope.jzShow = true;
		}else{
			$scope.jzShow = false;
		}
	});
	/**
	 * 登录用户所属公司
	 */
	var urls = applicationContext + "/uid005/05";
	post($http, urls, {}, function(response) {
		$scope.company = response.comp;
	});
	
	var saveUrl = applicationContext + "/uid006/12";
	$scope.save = function(user){
		post($http, saveUrl, {"user":user}, function(response) {
			commAlertService.alertService().add('success',
					'温馨提示:保存成功！！！（3秒后自动消失）', 3000);
		}, function(message) {
			ngDialog.close('ngdialog1')
			commAlertService.alertService().add('success',
					'温馨提示：' + message + '（3秒后自动消失）', 3000);
		});
	};
	
}]);

app.controller('pwdController', [
                             		'$scope',
                             		'$rootScope',
                             		'$http',
                             		'ngDialog',
                             		'EzConfirm',
                             		'commAlertService',
                             		function($scope, $rootScope, $http, ngDialog, EzConfirm,
                             				commAlertService) {
	var pwdurl = applicationContext + "/uid006/13";
	$scope.save = function(){
		post($http, pwdurl, {"oldPwd":$scope.oldPwd,"newPwd":$scope.newPwd,"conNewPwd":$scope.conNewPwd}, function(values) {
			commAlertService.alertService().add('success',
					'温馨提示：修改成功！（3秒后自动消失）', 3000);
		}, function(message) {
			commAlertService.alertService().add('success',
					'温馨提示：' + message + '（3秒后自动消失）', 3000);
		})
	};
	// 取消弹框
	$scope.cancel = function() {
		ngDialog.close('ngdialog1');
	}
	
}]);