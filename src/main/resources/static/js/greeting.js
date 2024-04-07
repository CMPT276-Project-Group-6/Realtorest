document.addEventListener('DOMContentLoaded', function() {
    var today = new Date();
    var hour = today.getHours();

    var greeting;
    if (hour >= 5 && hour < 12) {
        greeting = 'Good morning, ';
    } else if (hour >= 12 && hour < 18) {
        greeting = 'Good afternoon,';
    } else {
        greeting = 'Good evening,';
    }

    document.getElementById("greeting-user").innerHTML = greeting;
});