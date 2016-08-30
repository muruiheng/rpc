angular.module('globalAlert',['ng'])
.value("alerts",[]) //如果不写这个，那么下面的$rootScope.alerts = []就只能是显示一个了
.factory('commAlertService',['$rootScope','$timeout','alerts',function($rootScope,$timeout,alerts){
  return {
    "alertService":function(){
      var alertJson = {};
      $rootScope.alerts = alerts;
      alertJson.add = function(type,msg,time){
		    if(alerts.length<1){
		    	  $rootScope.alerts.push({'type': type, 'msg': msg,'close':function(){
		              alertJson.closeAlert(this);
		            }})
		    }else{
				$rootScope.alerts.splice(0,1);  
			    $rootScope.alerts.push({'type': type, 'msg': msg,'close':function(){
                    alertJson.closeAlert(this);
                }})
		    }
	        //如果设置定time的话就定时消失
	        if(time){
	          $timeout(function(){
	            $rootScope.alerts = [];
	          },time);
	        }
      };
      alertJson.closeAlert = function(alert){
        $rootScope.alerts.splice($rootScope.alerts.indexOf(alert),1);
      };
      return alertJson;
    }
  }
}])
  
