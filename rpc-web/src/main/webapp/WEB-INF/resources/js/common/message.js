 /**
 * 显示提示消息
 * @param $scope
 * @param message
 */ 
showmsg = function ($scope, ngDialog, message) {
		$scope.message=message;
		var messageDialog=ngDialog.open({
			template: 'messageDialog.html',
			className: 'ngdialog-theme-message ngdialog-theme-custom',
			controller: 'alertDialogController',
			scope:$scope
	    });
	}
//app.controller('alertDialogController', ['$scope', 'ngDialog', function ($scope, ngDialog) {
//	// 取消弹框
//	$scope.cancel = function() {
//		ngDialog.close('ngdialog1');
//	}
//}]);

alertDialogController = function($scope, ngDialog) {
	// 取消弹框
	$scope.cancel = function() {
		ngDialog.close('ngdialog1');
	}
}
 
