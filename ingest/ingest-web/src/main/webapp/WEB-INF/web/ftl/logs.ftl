<#import "/spring.ftl" as spring />
Ingest Log:
<script type="text/javascript">

window.setInterval("refreshLog()",3000);

function refreshLog(){

    $.get("/ingest-web/refresh", function(data){
    
    	document.getElementById("loadLog").innerHTML=
    		document.getElementById("loadLog").innerHTML+data;
    }
    );
	
	
	
}


</script>

<div id="loadLog" style="width:600px;height:400px; overflow-y:scroll; border:1px solid;"/> 


