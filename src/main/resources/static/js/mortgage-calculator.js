let mortgage = 0;
let payments = 0;

function calculateMortgage(){
    mortgage = 0;
    let propertyPrice = Math.round(parseFloat(document.forms['mortgage']['price'].value) *100)/100; //rounds to 2 decimal place
    let downpayment = Math.round(parseFloat(document.forms['mortgage']['downpay'].value) *100)/100;
    
    if(downpayment > propertyPrice){
        //todo: change alert to textbox near field
        alert("Downpayment should not exceed property price");
    }
    else{
        mortgage = propertyPrice - downpayment;
        let showMortgage = document.getElementById("yourMortgage");
        showMortgage.innerHTML = '$' + mortgage;
    }
}//Calculates the user's mortgage

function calculatePayment(){
    let amortization = parseFloat(document.forms['payment']['amortization'].value);
    let frequency = document.forms['payment']['frequency'].value;
    let ratePercent = Math.round(parseFloat(document.forms['payment']['rate'].value) *100)/100;
    let rate = ratePercent/100;//convert percent to decimal
    
    let showMortgage = document.getElementById("yourMortgage");
    let showPayments = document.getElementById("yourPayments");

    if(showMortgage.innerHTML == ''){
        alert("Calculate your mortgage before you can find out your payments.");
    }//checks if there's a mortgage value
    else if(ratePercent > 50 || ratePercent < 0){
        alert("Interest rate must be between %0.0 to %50.0.")
    }//checks if theres a valid interest rate value
    else{
        let interest = amortization*rate*mortgage;
        let totalPayment = mortgage+interest;
        
        if(frequency=='month'){
            let payFrequency = amortization * 12; 
            let monthlyPayment = Math.round((totalPayment/payFrequency) *100)/100;
            showPayments.innerHTML = "$" + monthlyPayment + ' monthly'
        }
        else if(frequency =='biweek'){
            let payFrequency = amortization * 24;
            let biweeklyPayment = Math.round((totalPayment/payFrequency) *100)/100;
            showPayments.innerHTML = "$" + biweeklyPayment + ' bi-weekly'
        }
    }//calculate downpayments
}
