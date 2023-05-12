const user = JSON.parse(sessionStorage.getItem('user'));

onload = () => {
    if (!user) {
        location.href = '../../index.html';
        return;
    }

    executaPesquisa();
}

const logout = () => {
    sessionStorage.removeItem('user');
    location.href = '../../index.html';
}

function executaPesquisa() {
    let xhr = new XMLHttpRequest();

    xhr.onload = () => {
        loadArticles(JSON.parse(xhr.responseText));
    }

    xhr.onerror = () => {
        alert('Ocorreu um erro ao carregar artigos ;-;');
    }

    xhr.open('GET', `http://localhost:5678/articles/all/${user.token}`, true);
    xhr.send();
}

const loadArticles = response => {
    let tab = document.querySelector('.inner_content');

    let articles = '';
    for (let i = 0; i < response.Articles.length; i++) {
        let stars = ``;

        let j = 0;
        while (j < Math.round(response.Articles[i].nota)) {
            stars += '<span class="fa fa-lg fa-star yellow"></span>';
            j++;
        }

        while (j < 5) {
            stars += '<span class="fa fa-lg fa-star black"></span>';
            j++;
        }

        let article = `
            <div class="col-sm-6 col-lg-12 col-xl-6" id="card_Control">
                <div class="card mb-6">
                    <div class="row">
                        <div class="col-auto">
                            <img src="/Pages/Artigos/images/article-${response.Articles[i].chave}.png" alt="${response.Articles[i].imagem_alt}" class="img-fluid rounded-start" id="img_Control" />
                        </div>
                        <div class="col-lg">
                            <div class="card-body">
                                <h5 class="card-title">${response.Articles[i].titulo}</h5>
                                <p class="card-text">
                                    ${response.Articles[i].resumo}
                                </p>
                                <form action="../Artigos_inside/Artigos_inside.html">
                                    <input type="hidden" name="article_id" value="${response.Articles[i].chave}">
                                    <input class="btn btn-primary ${response.Articles[i].jaEntrou === "true" ? 'ja-lido' : ''}" type="submit" value="Leia Mais">
                                </form>
                                <div class="stars">
                                    ${stars}
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        `;

        articles += article;
    }

    tab.innerHTML = articles;
}