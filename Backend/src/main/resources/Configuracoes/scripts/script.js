const user = JSON.parse(sessionStorage.getItem('user'));

const id_input = document.querySelector('#inputId'),
      email_input = document.querySelector('#inputEmail'),
      name_input = document.querySelector('#inputNome'),
      birth_input = document.querySelector('#inputNascimento'),
      gender_input = document.querySelector('#inputGenero'),
      nick_input = document.querySelector('#inputNick'),
      btn_save = document.querySelector('#btnSave'),
      btn_delete = document.querySelector('#btnDeletar');

[email_input, name_input, birth_input, gender_input, nick_input].forEach(elem => {
    elem.oninput = e => {
        if(e.data === ';'){
            elem.value = elem.value.substring(0, elem.value.length - 1);
        }
    };
});

onload = () => {
    if(!user) {
        location.href = '../../index.html';
        return;
    }

    sessionStorage.removeItem('tmp');
    getUserInfo();
}

const logout = () => {
    sessionStorage.removeItem('user');
    location.href = '../../index.html';
}

gender_input.oninput = () => {
    if(gender_input.value.length > 1){
        gender_input.value = gender_input.value.substring(0, 1);
    }

    gender_input.value = gender_input.value.toUpperCase();
};

btn_delete.onclick = () => {
    let xhr = new XMLHttpRequest();

    xhr.open('GET', `http://localhost:5678/usuario/Excluir/${user.token}/${user.id}`, true);

    xhr.onload = () => {
        if(JSON.parse(xhr.responseText).Usuario[0].deletado){
            alert('Sua conta foi deletada!\nObrigado por procrastinar com Planejy!');
            location.href = '../../index.html';
        }else{
            alert('Ocorreu um erro ao apagar sua conta!\n:D');
        }
    }

    xhr.onerror = () => {
        alert('Ocorreu um erro ao apagar sua conta!\n:D');
    }

    xhr.send();
}

btn_save.onclick = () => {
    updateUserInfo();
};

const getUserInfo = () => {
    let xhr = new XMLHttpRequest();
    xhr.open('GET', `http://localhost:5678/usuario/get/${user.token}`, true);

    xhr.onload = () => {
        loadUserInfo(JSON.parse(xhr.responseText).Usuario[0]);
    }

    xhr.onerror = () => {
        alert('Ocorreu um erro ao carregar informações ;-;');
    }

    xhr.send();
}

const loadUserInfo = info => {
    id_input.value = info.id;
    email_input.value = info.email;
    name_input.value = info.nome;
    birth_input.value = info.nascimento;
    nick_input.value = info.nick;
    gender_input.value = info.genero.toUpperCase();
}

const infoToString = () => {
    let info = '';

    const email = email_input.value.trim(); 
    const name = name_input.value.trim(); 
    const birth = birth_input.value.trim(); 
    const gender = gender_input.value.trim(); 
    const nick = nick_input.value.trim();

    if(email.length > 0 && name.length > 0 && birth.length > 0 && gender.length > 0 && nick.length > 0){
        info = `${email};${name};${birth};${nick};${gender}`;
    }

    return info;
}

const updateUserInfo = () => {
    const info = infoToString();

    if(info.length === 0){
        alert('Preencha cada um dos campos corretamente!');
        return;
    }

    let xhr = new XMLHttpRequest();
    xhr.open('POST', `http://localhost:5678/usuario/Atualizar/${user.token}/${user.id}`, true);

    xhr.onload = () => {
        alert('Dados salvos com sucesso!');
    }

    xhr.onerror = () => {
        alert('Ocorreu um erro ao salvar informações ;-;');
    }

    xhr.send(info);
}