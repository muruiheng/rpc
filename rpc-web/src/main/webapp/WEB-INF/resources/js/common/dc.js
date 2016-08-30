$(function (){
	var ve=parseInt($(".value").html())/17;
	for(var i=0;i<ve;i++){
		var div1="<div class='line'></div>";
		$(".line1").prepend(div1);
	}

})