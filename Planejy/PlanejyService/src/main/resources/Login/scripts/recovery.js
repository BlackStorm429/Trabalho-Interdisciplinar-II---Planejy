const back_box = document.querySelector('.back-box'),
      code_form = document.querySelector('.code-form'),
      code_input = document.querySelector('.code-input'),
      form_input = document.querySelector('.form-input'),
      form_email = document.querySelector('.email-form'),
      request_email = document.querySelector('.request-email'),
      request_code = document.querySelector('.request-code');


const generateToken = n => {
    const characters ='ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    let result = '';

    for(let i = 0; i < n; i++){
        result += characters.charAt(Math.floor(Math.random() * characters.length));
    }

    return result;
}

const code = generateToken(6);
console.log(code);

onload = () => {
    sessionStorage.removeItem('tmp');

    if(sessionStorage.getItem('user')){
        document.querySelectorAll('.txt-primary').forEach(e => e.innerHTML = 'Redefinir senha');
    }

    request_code.style.display = 'none';
    request_email.style.display = 'flex';
}

form_email.addEventListener('submit', e => {
    e.preventDefault();

    let xhr = new XMLHttpRequest();
    xhr.open('GET', `http://localhost:5678/usuario/recuperarSenha/${form_input.value.trim()}/${code}`, true);

    xhr.onload = () => {
        const response = JSON.parse(xhr.responseText).Usuario[0];
        
        if(response.id === -1){
            alert('Email não encontrado!');
        }else{
            request_email.style.display = 'none';
            request_code.style.display = 'flex';
        }
    }

    xhr.onerror = () => {
        alert('Email não encontrado');
    }

    xhr.send();
});

code_form.addEventListener('submit', e => {
    e.preventDefault();

    if (code_input.value === code) {
        sessionStorage.setItem('tmp', JSON.stringify({email: form_input.value, codigo: code_input.value}));
        location.href = 'reset-passwd.html';
    } else {
        alert("Código incorreto!");
    }
});

back_box.addEventListener('click', () => {
    history.back();
});