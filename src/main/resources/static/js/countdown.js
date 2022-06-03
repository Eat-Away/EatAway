
function update_timmer() {
    var mins = document.getElementById("minutos"); 
    var segs = document.getElementById("segundos");

        segs.innerText = segs.innerText - 1;

        if(segs.innerText <= 0){
            segs.innerText = 60;
            mins.innerText = mins.innerText -1 ;
        }
    
}

setInterval(update_timmer,1000);
