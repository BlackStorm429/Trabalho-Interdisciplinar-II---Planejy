const foo = strs => {
    if(strs.length === 0) return;
    
    let str = '';
    
    for(let i = 0; i < strs.length - 1; i++){
        if(!(strs[i].includes('!') || strs[i] === 'Outros')){
            str += strs[i].toLowerCase().replace(/ |-/g, '_') + ';';
        }
    }

    if(!(strs[strs.length - 1].includes('!') || strs[strs.length - 1] === 'Outros')){
        str += strs[strs.length - 1].toLowerCase().replace(/ |-/g, '_');
    }

    return str;
}
