const user = JSON.parse(sessionStorage.getItem('user'));
console.log(user);
if(!user) console.log('yo');
let calendar;

const inputArray = [document.getElementById('inputNome'),
                    document.getElementById('inputNome2'),
                    document.getElementById('inputDescricao'),
                    document.getElementById('inputDescricao2')];

inputArray.forEach(element => {
    element.oninput = e => {
        if(e.data === ';'){
            element.value = element.value.substring(0, element.value.length - 1);
        }
    };
});

const formatDate = date => {
    let d = new Date(date),
        month = '' + (d.getMonth() + 1),
        day = '' + d.getDate(),
        year = d.getFullYear();

    if (month.length < 2)
        month = '0' + month;
    if (day.length < 2)
        day = '0' + day;

    return [year, month, day].join('-');
}


const noteToString = note => {
    return `${user.id};${note.titulo};${note.dia};${note.descricao};${note.horario};${note.categoria};${note.cor}`;
}

const postNotes = (tmpNote, method) => {
    let note = {};

    if (isNaN(tmpNote)) {
        note = {
            id_usuario: user.token,
            titulo: tmpNote.title,
            dia: tmpNote.start,
            descricao: tmpNote.description,
            horario: tmpNote.horario,
            categoria: tmpNote.categoria,
            cor: tmpNote.color,
            id: tmpNote.id
        };
    } else {
        note = {
            id: tmpNote
        };
    }

    let str = '';
    let xhr = new XMLHttpRequest();

    if(method === 'delete'){
        xhr.open('GET', `http://localhost:5678/nota/delete/${user.token}/${note.id}`, true);
    }else if(method === 'insert'){
        str = noteToString(note);
        xhr.open('POST', `http://localhost:5678/nota/post/${user.token}`, true);
    }else if(method === 'update'){
        str = noteToString(note);
        xhr.open('POST', `http://localhost:5678/nota/update/${user.token}/${note.id}`, true);
    }

    xhr.onload = () => {
        getNotes();
    }

    xhr.onerror = () => {
        alert('erro ao salvar notas ;-;');
    }

    xhr.send(str);
}

const getNotes = () => {
    let xhr = new XMLHttpRequest();
    xhr.open('GET', `http://localhost:5678/nota/get/${user.token}`, true);

    xhr.onload = () => {
        let tmp = JSON.parse(xhr.responseText);
        db = { data: [] };

        for (let i = 0; i < tmp.Notas.length; i++) {
            db.data.push(tmp.Notas[i]);
        }

        data.events = db.data;
        calendar = initCalendar();
        calendar.eventDragging = true;
        calendar.render();
        listarEventos();
    }

    xhr.onerror = () => {
        alert('erro ao recuperar notas ;-;');
    }

    xhr.send();
}

const initCalendar = () => {
    let calendarEl = document.getElementById('calendar');

    let calendar = new FullCalendar.Calendar(calendarEl, data);

    return calendar;
}

let db = { data: [] };

const data = {
    headerToolbar: {
        left: 'prev,next today',
        center: 'title',
        right: 'dayGridMonth,listMonth'
    },
    initialDate: formatDate(new Date().getTime()),
    navLinks: false, // can click day/week names to navigate views
    businessHours: true, // display business hours
    editable: true,
    selectable: true,
    handleWindowResize: true,
    events: db.data,
    windowResizeDelay: 0,
    navLinks: true,
    eventClick: function(info) {
        updateNotes(info.event);
    },
    navLinkWeekClick: function(weekStart, jsEvent) {
        console.log('week start', weekStart.toISOString());
        console.log('coords', jsEvent.pageX, jsEvent.pageY);
    },
    eventDrop: function(info) {
        for (let i = 0; i < db.data.length; i++) {
            if (db.data[i].id == info.event.id) {
                db.data[i].start = formatDate(info.event.start);
                postNotes(db.data[i], 'update');
            }
        }
    }
}

onload = () => {
    if (!user) {
        location.href = '../index.html';
        return;
    }

    getNotes();
}

const logout = () => {
    sessionStorage.removeItem('user');
    location.href = '../index.html';
}

// Menu de adição de notas
const init = () => {
    // Adiciona funções para tratar os eventos 
    $("#btnInsert").click(function() {
        // Verfica se o formulário está preenchido corretamente
        if (!$("#form-postit")[0].checkValidity()) {
            return;
        }

        // Obtem os valores dos campos do formulário
        let campoNome = $("#inputNome").val();
        let campoDescricao = $("#inputDescricao").val();
        let campoDia = $("#inputDia").val();
        let campoHorario = $("#inputHorario").val();
        let campoCategoria = $("#inputCategoria").val();

        let categ = document.getElementById('inputCategoria').querySelectorAll('option');
        let categName;

        for (let i = 0; i < categ.length; i++) {
            if (categ[i].value === campoCategoria) {
                categName = categ[i].textContent;
                i = categ.length; //break
            }
        }

        if (campoNome == '' || campoDescricao == '' || campoDia == '' || campoHorario == '' || campoCategoria == '' || categName == '') {
            alert('Campos obrigatórios não podem estar vazios!');
            return;
        }

        let postit = {
            nome: campoNome,
            descricao: campoDescricao,
            horario: campoHorario,
            start: campoDia,
            color: campoCategoria,
            categoria: categName
        };

        insertPostit(postit);

        // Limpa o formulario
        $("#form-postit")[0].reset();

        document.querySelector('.addNotes').style.display = 'none';
    });

    // Intercepta o click do botao Limpar Form
    $("#btnClear").click(function() {
        $("#form-postit")[0].reset();
    });
}

const addNotes = () => {
    let noteMenu = document.querySelector('.addNotes');
    noteMenu.style.display = 'block';

    $('#btnCancel').click(function() {
        noteMenu.style.display = 'none';
        // Limpa o formulario
        $("#form-postit")[0].reset();
    });

    init();
}

const updateNotes = note => {
    let noteMenu = document.querySelector('.updateNotes');
    noteMenu.style.display = 'block';

    let inputName = document.getElementById('inputNome2');
    let inputDay = document.getElementById('inputDia2');
    let inputDescription = document.getElementById('inputDescricao2');
    let inputHour = document.getElementById('inputHorario2');
    let inputCategory = document.getElementById('inputCategoria2');


    // == fill current note values == //

    inputName.value = note.title;
    inputDay.value = note.startStr;
    inputDescription.value = note.extendedProps.description;
    inputHour.value = note.extendedProps.horario;
    inputCategory.value = note.backgroundColor;


    // == defining behavior of buttons == //

    document.getElementById('btnCancel2').onclick = () => {
        noteMenu.style.display = 'none';
    }

    document.getElementById('btnInsert2').onclick = () => {
        if (inputName.value == '' || inputCategory.value == '' || inputDay.value == '' || inputDescription.value == '' || inputHour.value == '') {
            alert('Campos obrigatórios não podem estar vazios!');
        } else {
            let categ = document.getElementById('inputCategoria').querySelectorAll('option');
            let categName;

            for (let i = 0; i < categ.length; i++) {
                if (categ[i].value === inputCategory.value) {
                    categName = categ[i].textContent;
                    i = categ.length; //break
                }
            }

            let index = 0;
            for (; index < db.data.length; index++) {
                if (db.data[index].id == note.id) {
                    break;
                }
            }

            db.data[index].title = inputName.value;
            db.data[index].description = inputDescription.value;
            db.data[index].horario = inputHour.value;
            db.data[index].start = inputDay.value;
            db.data[index].color = inputCategory.value;
            db.data[index].categoria = categName;

            noteMenu.style.display = 'none';

            postNotes(db.data[index], 'update');
            noteMenu.style.display = 'none';
            db = { data: [] };
            getNotes();
        }
    }

    document.getElementById('btnClear2').onclick = () => {
        inputName.value = null;
        inputDay.value = null;
        inputDescription.value = null;
        inputHour.value = null;
        inputCategory.value = null;
    }

    document.getElementById('btnDelet2').onclick = () => {
        let index = 0;
        for (; index < db.data.length; index++) {
            if (db.data[index].id == note.id) {
                break;
            }
        }

        postNotes(db.data[index], 'delete');
        noteMenu.style.display = 'none';
    }
}

const insertPostit = postit => {
    let novoPostit = {
        "title": postit.nome,
        "description": postit.descricao,
        "start": postit.start,
        "horario": postit.horario,
        "color": postit.color,
        "categoria": postit.categoria,
        "textColor": 'black'
    };

    postNotes(novoPostit, 'insert');
    postCateg([novoPostit.categoria]);
}

const openEventList = () => {
    // Abrindo a lista
    let noteList = document.querySelector('.tabelaEventos');
    noteList.style.display = 'block';
    listarEventos();


    // caso clique no botao de fechar...
    $('#btnClose').click(function() {
        noteList.style.display = 'none';
    });
}

const listarEventos = () => {
    // limpa a lista de contatos apresentados
    $("#table-events").empty();

    // Popula a tabela com os registros do banco de dados
    for (let index = 0; index < db.data.length; index++) {
        const evento = db.data[index];

        // Inclui o evento na tabela   
        $("#table-events").append(`<tr><td scope="row">${evento.title}</td>
                                        <td>${evento.description}</td>
                                        <td>${evento.start}</td>
                                        <td>${evento.horario}</td>
                                        <td><span onclick="trashcanFunc(${db.data[index].id});" id="btn-delete-event" class="material-icons">delete</span>
                                    </tr>`);
    }
}

const trashcanFunc = id => {
    postNotes(id, 'delete');
    $("#table-events").empty();
}

const taskMaster = ['f', '4', '4', '3', '6', 'f'];
const boo = ['b', 'o', 'o'];
let buffer = [];

window.addEventListener('keyup', e => {
    const key = e.key.toLowerCase();
    buffer.push(key);

    if (buffer.length === taskMaster.length) {
        if (buffer.every((element, index) => element === taskMaster[index])){
            location.href = 'https://taskmaster.carolnigri.repl.co/';
        }
    }else if(buffer.length === boo.length){
        if(buffer.every((element, index) => element === boo[index])){
            if(visible) turnOff();
            else turnOn();

            buffer = [];
        }
    } else if (buffer[buffer.length - 1] != taskMaster[buffer.length - 1] && buffer[buffer.length - 1] != boo[buffer.length - 1]) {
        buffer = [];
    }
});
