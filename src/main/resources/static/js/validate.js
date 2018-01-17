function checkPassword(str)
  {
    var re = /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{6,}$/;
    return re.test(str);
  }

  function checkForm(form)
  {
    if(form.login.value == "") {
      alert("Error: Username cannot be blank!");
      form.login.focus();
      return false;
    }
    re = /^\w+$/;
    if(!re.test(form.login.value)) {
      alert("Error: Username must contain only letters, numbers and underscores!");
      form.login.focus();
      return false;
    }
    if(form.password.value != "" && form.password.value == form.password_again.value) {
      if(!checkPassword(form.password.value)) {
        alert("The password you have entered is not valid!Must contain :1 uppercase, 1 lowercase, 1 number!");
        form.password.focus();
        return false;
      }
    } else {
      alert("Error: Passwords don't match!!");
      form.password.focus();
      return false;
    }
    return true;
  }
