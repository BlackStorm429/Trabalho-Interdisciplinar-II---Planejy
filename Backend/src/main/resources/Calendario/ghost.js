const ghost = document.getElementById('ghost');

let mouseX = 0;
let mouseY = 0;

let ghostX = 0;
let ghostY = 0;

let x = 0;
let y = 0;

let visible = false;
let animation = null;

let timer = 0;

const hide = () => {
    ghost.style.opacity = 0;
}

const show = () => {
    ghost.style.opacity = 1;
}

const turnOff = () => {
    document.body.removeEventListener('mousemove', setPosition);
    cancelAnimationFrame(animation);
    hide();
    visible = false;
}

const turnOn = () => {
    document.body.addEventListener('mousemove', setPosition);
    visible = true;
    move();
}

const setPosition = e => {
    mouseX = e.clientX;
    mouseY = e.clientY;
    show();
    timer = 0;
}

const move = () => {
    x = mouseX - ghostX;
    y = mouseY - ghostY;

    ghostX += x / 100;
    ghostY += y / 100;

    ghost.style.setProperty('--ghostXPos', ghostX + 'px');
    ghost.style.setProperty('--ghostYPos', ghostY + 'px');

    timer++;

    if(timer > 300) hide();

    animation = requestAnimationFrame(move);
}
