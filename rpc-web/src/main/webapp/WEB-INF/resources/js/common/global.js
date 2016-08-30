var applicationContext = "/mgctrl";
libpath = applicationContext + "/views/js/";

/**
 * jslib库
 * 
 * @type
 */
var jslib = {
	loadAngular : function() {
		loadJsAndCss(libpath + "jquery-1.12.2.min.js", "js");
		loadJsAndCss(libpath + "angular/angular.min.js", "js");
		loadJsAndCss(libpath + "angular/angular-route.min.js", "js");
		loadJsAndCss(libpath + "angular/ngDialog.min.js", "js");
 	//	loadJsAndCss(libpath + "angular/tm.pagination.js", "js");
		loadJsAndCss(libpath + "angular/tm.pagination.min.js", "js");
		loadJsAndCss(libpath + "angular/ui-bootstrap-tpls.min.js", "js");
		loadJsAndCss(libpath + "angular/ez-confirm.min.js", "js");
	    loadJsAndCss(libpath + "angular/ez-confirm-tpl.js", "js");
	    loadJsAndCss(libpath + "angular/ez-focus.min.js", "js"); 
 		loadJsAndCss(libpath + "angular/message.js", "js");
 		loadJsAndCss(libpath + "angular/angular-ui-tree.min.js", "js");
 		loadJsAndCss(libpath + "bootstrap/bootstrap.min.js", "js");
 		loadJsAndCss(libpath + "datepicker/WdatePicker.js", "js");
 		loadJsAndCss(libpath + "highcharts/highcharts.js", "js"); 	
 		loadJsAndCss(libpath + "highcharts/exporting.js", "js"); 	
 		loadJsAndCss(libpath + "highcharts/highcharts-more.js", "js"); 		
 		loadJsAndCss(libpath + "highcharts/solid-gauge.js", "js"); 	
 		loadJsAndCss(libpath + "highcharts/highcharts-3d.js", "js"); 
 		loadJsAndCss(libpath + "angular/angular-cookies.min.js", "js");
 		loadJsAndCss(libpath + "ng-tags-input/ng-tags-input.min.js", "js");
 		loadJsAndCss(libpath + "angular/angular-touch.min.js", "js");
 		loadJsAndCss(libpath + "angucomplete/angucomplete.js", "js");


	},
	loadBootstrap : function() {
		loadJsAndCss(libpath + "bootstrap/css/bootstrap.min.css", "css");
		loadJsAndCss(libpath + "bootstrap/css/bootstrap-theme.min.css", "css");
		loadJsAndCss(libpath + "../css/ngDialog.min.css", "css");
		loadJsAndCss(libpath + "../css/ngDialog-theme-default.min.css", "css");
		loadJsAndCss(libpath + "../css/ngDialog-theme-plain.min.css", "css");
		loadJsAndCss(libpath + "../css/ngDialog-theme-dialog.min.css", "css");
		loadJsAndCss(libpath + "../css/ngDialog-theme-message.min.css", "css");
		loadJsAndCss(libpath + "../css/ngDialog-theme-window.min.css", "css");
		loadJsAndCss(libpath + "../css/tm.pagination.min.css", "css");
		loadJsAndCss(libpath + "../css/ez-confirm.min.css", "css");
		loadJsAndCss(libpath + "../css/angular-ui-tree.min.css", "css");
		loadJsAndCss(libpath + "../css/home.css", "css");
		loadJsAndCss(libpath + "../css/button.css", "css");
		loadJsAndCss(libpath + "../css/input.css", "css");
		loadJsAndCss(libpath + "../css/left-menu.css", "css");
		loadJsAndCss(libpath + "../css/mgManager.css", "css");
		loadJsAndCss(libpath + "../css/table.css", "css");
		loadJsAndCss(libpath + "../css/new.css", "css");
		loadJsAndCss(libpath + "../css/pic.css", "css");
		loadJsAndCss(libpath + "ng-tags-input/css/ng-tags-input.bootstrap.min.css", "css");
		loadJsAndCss(libpath + "ng-tags-input/css/ng-tags-input.min.css", "css");
		loadJsAndCss(libpath + "angucomplete/css/angucomplete.css", "css");
	}
	
};

/**
 * 页面初始化自动加载的方法
 */
(function() {
	loadBasicJs(loadDependJs);
})();

/**
 * 加载基础类库
 * 
 * @param callback
 *            回调函数，加载其它依赖类库
 */
function loadBasicJs(callback) {
	// 引入jquery
	jslib.loadAngular();
	// 引入其它依赖库
	callback();
};
/**
 * 加载其它依赖类库
 */
function loadDependJs(callback) {
	// 引入bootstrap
	jslib.loadBootstrap();
};

/*******************************************************************************
 * 动态加载基础类库
 * 
 * @param filename
 *            文件名
 * @param filetype
 *            文件类型
 ******************************************************************************/
function loadJsAndCss(filename, filetype) {
	if (filetype == "js") { // 如果文件类型为js文件
		document.write("<script src=" + filename + "><\/script>");
	} else if (filetype == "css") { // 如果文件类型为css文件
		document.write("<link rel='stylesheet' type='text/css' href="
				+ filename + " \/>");
	}
};

/**
 * 处理post请求
 * 
 * @param $http
 *            angularjs 的http
 * @param url
 *            后台url
 * @param data
 *            post后台的的json数据
 */
post = function($http, url, data, success, failed) {
	$http.post(url, data).success(function(response) {
		if (response.relogin) {
			alert("请重新登录");
		} else if (response.success) {
			success(response.values, response.message);
		} else {
			failed(response.message);
		}

	});
}

/**
 * 处理get请求
 * 
 * @param $http
 * @param url
 * @param data
 * @param success
 * @param failed
 */
get = function($http, url, data, success, failed) {
	$http.get(url, data).success(function(response) {
		if (response.relogin) {
			alert("请重新登录");
		} else if (response.success) {
			success(response.values, response.message);
		} else {
			failed(response.message);
		}

	});
}

var base64encodechars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
var base64decodechars = new Array(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1,
		63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1,
		0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
		20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31,
		32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49,
		50, 51, -1, -1, -1, -1, -1);

/**
 * 加密 
 * @param str 需要加密的字符串
 * @returns {String}
 */
base64encode = function (str) {
	var out, i, len;
	var c1, c2, c3;
	len = str.length;
	i = 0;
	out = "";
	while (i < len) {
		c1 = str.charCodeAt(i++) & 0xff;
		if (i == len) {
			out += base64encodechars.charAt(c1 >> 2);
			out += base64encodechars.charAt((c1 & 0x3) << 4);
			out += "==";
			break;
		}
		c2 = str.charCodeAt(i++);
		if (i == len) {
			out += base64encodechars.charAt(c1 >> 2);
			out += base64encodechars.charAt(((c1 & 0x3) << 4)
					| ((c2 & 0xf0) >> 4));
			out += base64encodechars.charAt((c2 & 0xf) << 2);
			out += "=";
			break;
		}
		c3 = str.charCodeAt(i++);
		out += base64encodechars.charAt(c1 >> 2);
		out += base64encodechars.charAt(((c1 & 0x3) << 4) | ((c2 & 0xf0) >> 4));
		out += base64encodechars.charAt(((c2 & 0xf) << 2) | ((c3 & 0xc0) >> 6));
		out += base64encodechars.charAt(c3 & 0x3f);
	}
	return out;
}

/**
 * 解密
 * @param str 需要解密的字符串
 * @returns {String}
 */
base64decode = function (str) {
	var c1, c2, c3, c4;
	var i, len, out;
	len = str.length;
	i = 0;
	out = "";
	while (i < len) {

		do {
			c1 = base64decodechars[str.charCodeAt(i++) & 0xff];
		} while (i < len && c1 == -1);
		if (c1 == -1)
			break;

		do {
			c2 = base64decodechars[str.charCodeAt(i++) & 0xff];
		} while (i < len && c2 == -1);
		if (c2 == -1)
			break;

		out += String.fromCharCode((c1 << 2) | ((c2 & 0x30) >> 4));

		do {
			c3 = str.charCodeAt(i++) & 0xff;
			if (c3 == 61)
				return out;
			c3 = base64decodechars[c3];
		} while (i < len && c3 == -1);
		if (c3 == -1)
			break;

		out += String.fromCharCode(((c2 & 0xf) << 4) | ((c3 & 0x3c) >> 2));

		do {
			c4 = str.charCodeAt(i++) & 0xff;
			if (c4 == 61)
				return out;
			c4 = base64decodechars[c4];
		} while (i < len && c4 == -1);
		if (c4 == -1)
			break;
		out += String.fromCharCode(((c3 & 0x03) << 6) | c4);
	}
	return out;
}

