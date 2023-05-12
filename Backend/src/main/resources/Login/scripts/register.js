const back_box = document.querySelector('.back-box'),
      user_firstname = document.querySelector('#firstname'),
      user_email = document.querySelector('#email'),
      user_confirmemail = document.querySelector('#confirmemail'),
      user_nick = document.querySelector('#nickname'),
      user_birthday = document.querySelector('#birthDate'),
      user_passwd = document.querySelector('#passwd'),
      user_confirmpasswd = document.querySelector('#confirmpasswd'),
      form_element = document.querySelector('#register'),
      show_passwd = document.querySelector('#show-passwd'),
      hide_passwd = document.querySelector('#hide-passwd');

let btn_submit = document.querySelector('.btn-submit');
let valid_passwd = true;
let valid_email = true;

onload = () => {
    sessionStorage.removeItem('user');
}

document.querySelector('.btn-login').onclick = () => {
    location.href = '../../index.html';
}

back_box.addEventListener('click', () => {
    location.href = '../../index.html';
});

user_confirmemail.addEventListener('blur', () => {
    if (user_confirmemail.value != user_email.value) {
        user_confirmemail.style.backgroundColor = '#f1343499';
        valid_email = false;
        btn_submit.setAttribute('disabled', 'true');
    } else {
        user_confirmemail.style.backgroundColor = '#fff';
        valid_email = true;
        if (valid_email && valid_passwd) {
            btn_submit.removeAttribute('disabled');
        }
    }
});

user_confirmpasswd.addEventListener('blur', () => {
    if (user_confirmpasswd.value != user_passwd.value) {
        user_confirmpasswd.style.backgroundColor = '#f1343499';
        valid_passwd = false;
        btn_submit.setAttribute('disabled', 'true');
    } else {
        user_confirmpasswd.style.backgroundColor = '#fff';
        valid_passwd = true;
        if (valid_email && valid_passwd) {
            btn_submit.removeAttribute('disabled');
        }
    }
});

show_passwd.addEventListener('click', e => {
    user_confirmpasswd.setAttribute('type', 'text');
    show_passwd.style.display = 'none';
    hide_passwd.style.display = 'block';
    e.preventDefault();
});

hide_passwd.addEventListener('click', e => {
    user_confirmpasswd.setAttribute('type', 'password');
    show_passwd.style.display = 'block';
    hide_passwd.style.display = 'none';
    e.preventDefault();
});

form_element.addEventListener('submit', e => {
    e.preventDefault();
});

btn_submit.onclick = () => {
    if(user_passwd.value === 'senha123'){
        alert('ERRO: esta senha já está sendo usada por `Pedrinho123`');
    }else{
        let user = {
            name: user_firstname.value.trim(),
            email: user_email.value.trim(),
            nick: user_nick.value.trim(),
            password: user_passwd.value.trim(),
            gender: 'n'
        }
        
        postUser(user);
    }
}

const postUser = user => {
    let xhr = new XMLHttpRequest();
    xhr.open('POST', `http://localhost:5678/usuario/registrar/${user.name}/${user.nick}/${user.email}`, true);

    xhr.onload = () => {
        let response = JSON.parse(xhr.responseText).Usuario[0];
        if(response.sucesso){
            alert('Nova conta criada!\nProceda com o login');
            location.href = '../../index.html';
        }else if(!response.nick && !response.email){
            alert(`ERRO: nick '${user.nick}' e email '${user.email}' já estão sendo usados!`);
        }else if(!response.nick){
            alert(`ERRO: nick '${user.nick}' já está sendo usado!`);
        }else if(!response.email){
            alert(`ERRO: email '${user.email}' já está sendo usado!`);
        }
    }

    xhr.onerror = () => {
        alert('erro ao criar conta ;-;');
    }

    xhr.send(user.password);
}