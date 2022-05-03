//===
// VARIABLES
//===
const DATE_TARGET = new Date(2022, 04, 28, 15, 00, 00);    //'04/13/2020 0:01 AM'
// DOM for render
const SPAN_DAYS = document.querySelector('span#days');
const SPAN_HOURS = document.querySelector('span#hours');
const SPAN_MINUTES = document.querySelector('span#minutes');
const SPAN_SECONDS = document.querySelector('span#seconds');
// Milliseconds for the calculations
const MILLISECONDS_OF_A_SECOND = 1000;
const MILLISECONDS_OF_A_MINUTE = MILLISECONDS_OF_A_SECOND * 60;
const MILLISECONDS_OF_A_HOUR = MILLISECONDS_OF_A_MINUTE * 60;
const MILLISECONDS_OF_A_DAY = MILLISECONDS_OF_A_HOUR * 24

//===
// FUNCTIONS
//===

/**
 * Method that updates the countdown and the sample
 */
function updateCountdown() {
    // Calcs
    const NOW = new Date();
    // const DATE_TARGET = new Date('26/04/2022 3:00 PM');    //'04/13/2020 0:01 AM'
    let yearDif =  DATE_TARGET.getFullYear() - NOW.getFullYear();
    let monthDif =  DATE_TARGET.getMonth() - NOW.getMonth();
    let dayDif =  DATE_TARGET.getDate() - NOW.getDate();
    let HourDif =  DATE_TARGET.getHours() - NOW.getHours();
    let minDif =  DATE_TARGET.getMinutes() - NOW.getMinutes();
    let secDif =  DATE_TARGET.getSeconds() - NOW.getSeconds();
    let milisecDif =  DATE_TARGET.getMilliseconds() - NOW.getMilliseconds();

    


    
    const DURATION = DATE_TARGET.getDate() - NOW.getDate();
    const REMAINING_DAYS = Math.floor(DURATION / MILLISECONDS_OF_A_DAY);
    const REMAINING_HOURS = Math.floor((DURATION % MILLISECONDS_OF_A_DAY) / MILLISECONDS_OF_A_HOUR);
    const REMAINING_MINUTES = Math.floor((DURATION % MILLISECONDS_OF_A_HOUR) / MILLISECONDS_OF_A_MINUTE);
    const REMAINING_SECONDS = Math.floor((DURATION % MILLISECONDS_OF_A_MINUTE) / MILLISECONDS_OF_A_SECOND);
 
    // Render
    SPAN_DAYS.textContent = REMAINING_DAYS;
    SPAN_HOURS.textContent = REMAINING_HOURS;
    SPAN_MINUTES.textContent = REMAINING_MINUTES;
    SPAN_SECONDS.textContent = REMAINING_SECONDS;
}

//===
// INIT
//===
updateCountdown();
// Refresh every second
setInterval(updateCountdown, MILLISECONDS_OF_A_SECOND);
