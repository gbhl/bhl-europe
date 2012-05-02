<#import "/spring.ftl" as spring />
Ingest Log:
<script type="text/javascript">

window.setInterval("refreshLog()",3000);

function refreshLog(){

    $.get("/ingest/refresh", function(data){
    
    	document.getElementById("loadLog").innerHTML=
    		document.getElementById("loadLog").innerHTML+data;
    }
    );
}


</script>

<div id="loadLog" style="overflow-y:scroll; border:1px solid;"/> 


