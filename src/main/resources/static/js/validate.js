
var password = document.getElementByName("password");
var confirm_password = document.getElementByName("password_again");

var email = document.getElementByName("email");
var mailformat = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/;

function validatePassword(){
  if(password.value != confirm_again.value) {
    confirm_password.setCustomValidity("Passwords Don't Match");
  } else {
    confirm_password.setCustomValidity('');
  }
}

password.onchange = validatePassword;
confirm_password.onkeyup = validatePassword;


function validateEmail() {
    var x = document.forms["myForm"]["email"].value;
    var atpos = x.indexOf("@");
    var dotpos = x.lastIndexOf(".");
    if (atpos<1 || dotpos<atpos+2 || dotpos+2>=x.length) {
        window.alert("Not a valid e-mail address");
        return false;
    }
}
