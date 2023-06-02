const user = JSON.parse(sessionStorage.getItem('user'));

const back_box = document.querySelector('.back-box'),
      new_passwd = document.querySelector('.new-passwd'),
      confirm_passwd = document.querySelector('.confirm-passwd'),
      reset_form = document.querySelector('.reset-form'),
      form_button = document.querySelector('.form-button'),
      information = document.querySelector('.information'),
      show_passwd = document.querySelector('#show-passwd'),
      hide_passwd = document.querySelector('#hide-passwd'),
      show_confirmpwd = document.querySelector('#show-confirmpwd'),
      hide_confirmpwd = document.querySelector('#hide-confirmpwd'),
      tmp = JSON.parse(sessionStorage.getItem('tmp'));

let valid_passwd = false;

onload = () => {
    if(!tmp) location.href = '../../index.html';
}

const generateToken = n => {
    const characters ='ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    let result = '';

    for(let i = 0; i < n; i++){
        result += characters.charAt(Math.floor(Math.random() * characters.length));
    }

    return result;
}

back_box.addEventListener('click', () => {
    history.back();
});

confirm_passwd.addEventListener('input', () => {
    if (confirm_passwd.value != new_passwd.value) {
        confirm_passwd.style.backgroundColor = '#f1343499';
        valid_passwd = false;
        information.innerText = 'As senhas não se coincidem.'
    } else {
        confirm_passwd.style.backgroundColor = '#fff';
        valid_passwd = true;
        information.innerText = ''
    }
});

const updateUser = (user, p) => {
    let xhr = new XMLHttpRequest();
    xhr.open('POST', `http://localhost:5678/usuario/login/${user.email}/${user.token}`, true);

    xhr.onload = () => {
        if(JSON.parse(xhr.responseText).Usuario[0].id === -1){
            alert('Ocorreu um erro ao retornar à pagina ;-;\nPor favor, efetue o login novamente.');
            location.href = '../../index.html';
        }else{
            alert('Senha alterada!');
            sessionStorage.setItem('user', JSON.stringify(user));
            location.href = '../../Configuracoes/index.html';
        }
    }

    xhr.onerror = () => {
        alert('Senha alterada!\nProceda com o login para confirmar');
    }

    xhr.send(p)
}

reset_form.addEventListener('submit', e => {
    e.preventDefault();

    if(confirm_passwd.value === new_passwd.value){
        const p = new_passwd.value.trim();
        let xhr = new XMLHttpRequest();
        xhr.open('POST', `http://localhost:5678/usuario/recuperarSenha/${tmp.codigo}`, true);

        xhr.onload = () => {
            if(JSON.parse(xhr.responseText).Usuario[0].id === -1){
                alert('Ocorreu um erro ao redefinir senha ;-;');
            }else{
                if(!user){
                    alert('Senha alterada!\nProceda com o login para confirmar.');
                    location.href = '../../index.html';
                }else{
                    user.token = generateToken(40);
                    updateUser(user, p);
                }
            }
        }

        xhr.onerror = () => {
            alert('Ocorreu um erro ao alterar a senha ;-;');
        }
        
        xhr.send(p);
    }
});

show_passwd.addEventListener('click', e => {
    e.preventDefault();
    new_passwd.setAttribute('type', 'text');
    show_passwd.style.display = 'none';
    hide_passwd.style.display = 'block';
});

show_passwd.addEventListener('vmousedown', e => {
    e.preventDefault();
    new_passwd.setAttribute('type', 'text');
    show_passwd.style.display = 'none';
    hide_passwd.style.display = 'block';
});

hide_passwd.addEventListener('click', e => {
    e.preventDefault();
    new_passwd.setAttribute('type', 'password');
    show_passwd.style.display = 'block';
    hide_passwd.style.display = 'none';
});

show_confirmpwd.addEventListener('click', e => {
    e.preventDefault();
    confirm_passwd.setAttribute('type', 'text');
    show_confirmpwd.style.display = 'none';
    hide_confirmpwd.style.display = 'block';
});

show_confirmpwd.addEventListener('vmousedown', e => {
    e.preventDefault();
    confirm_passwd.setAttribute('type', 'text');
    show_confirmpwd.style.display = 'none';
    hide_confirmpwd.style.display = 'block';
});

hide_confirmpwd.addEventListener('click', e => {
    e.preventDefault();
    confirm_passwd.setAttribute('type', 'password');
    show_confirmpwd.style.display = 'block';
    hide_confirmpwd.style.display = 'none';
});