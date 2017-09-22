/* dynamicDemoController = function dynamicDemoController(NgTableParams, $scope, $http, ngDialog,
		EzConfirm, commAlertService) { 
	  $scope.cols = [{
      field: "node",
      title: "node",
      sortable: "node",
      show: false,
      groupable: "node"
    } ,{
        field: "node",
        title: "node",
        sortable: "node",
        show: true,
        groupable: "node"
      } ];
	  //	  获取真数据
	var url = applicationContext+"/views/node_list.json";
	$http.get(url).success(function(data){
		$scope.tableParams = new NgTableParams({
		       
		      group: {
		        node: "desc"
		      }
		    }, {
		      dataset: data.nodes,
		      groupOptions: {
		        isExpanded: false
		      }
		 
		    })
	});
}
	
*/
dynamicDemoController = function aplNodeListController(NgTableParams, $scope, $http, ngDialog,
		EzConfirm, commAlertService) { 
 	var url = applicationContext+"/views/node_list.json?"+Math.random();
	$http.get(url).success(function(data){
		$scope.serviceList = data.nodes;
	})
	$scope.open = true;
	$scope.getIp = function(aa,node){
		var url1=applicationContext+"/views/"+node.node+"_services_list.json?"+Math.random();
			$http.get(url1).success(function(data){
				node.ipList = data.services;
				
				aa.open = !aa.open;
			});
	
}}

	 

 
