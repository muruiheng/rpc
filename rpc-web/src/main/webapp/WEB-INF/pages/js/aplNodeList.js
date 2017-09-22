aplNodeListController = function aplNodeListController(NgTableParams, $scope, $http, ngDialog,
		EzConfirm, commAlertService) { 
	 /* $scope.cols = [{
      field: "service",
      title: "service",
      sortable: "service",
      show: false,
      groupable: "service"
    },{
        field: "node",
        title: "node",
        sortable: "node",
        show: true,
        groupable: "node"
      }];
	  // 获取真数据
	  	var Ndata=[];
	  	var j=0;
		var url = applicationContext+"/views/services_list.json";
		$http.get(url).success(function(data){
			$scope.tableParams = new NgTableParams({
			      group: {
			        service: "desc"
			      }
			    }, {
			      dataset: data.services,
			      groupOptions: {
			        isExpanded: false
			      }
			 
			    })
		
			
 		for(var i=0;i<data.services.length;i++){
 				var url1=applicationContext+"/views/"+data.services[i].service+"_node_list.json";
 				$http.get(url1).success(function(data1){
 					$scope.tableParams = new NgTableParams({
 					      group: {
 					        node: "desc"
 					      }
 					    }, {
 					      dataset: data1.nodes,
 					      groupOptions: {
 					        isExpanded: false
 					      }
 					 
 					    })
 				})
		}
})*/
	var url = applicationContext+"/views/services_list.json?"+Math.random();
	$http.get(url).success(function(data){
		$scope.serviceList = data.services;
	})
	$scope.open = true;
	$scope.getIp = function(aa,node){
		var url1=applicationContext+"/views/"+node.service+"_node_list.json?"+Math.random();
			$http.get(url1).success(function(data){
				node.ipList = data.nodes;
				
				aa.open = !aa.open;
			});
	
}}
 	