function verificar() {
    const conjuncao = document.getElementById("conjuncao").innerText.trim();
    const resposta = document.getElementById("resposta").value.trim();
    const somCorreto = new Audio("som-correto.mp3");
    const somErro = new Audio("som-errado.mp3");

    if (!resposta) {
        mostrarResultado("Por favor, digite uma resposta.", "orange");
        return;
    }

    fetch(`http://localhost:8000/verificar?conjuncao=${encodeURIComponent(conjuncao)}&resposta=${encodeURIComponent(resposta)}`)
        .then(res => {
            if (!res.ok) throw new Error("Erro na requisição");
            return res.text();
        })
        .then(data => {
            const acertou = data.startsWith("Correto!");
            mostrarResultado(data, acertou ? "green" : "red");

            if (acertou) {
                somCorreto.play();
            } else {
                somErro.play();
            }

            // Espera 2 segundos antes de sortear a próxima conjunção
            setTimeout(() => {
                sortearConjuncao();
            }, 3000);
        })
        .catch(error => {
            mostrarResultado("Erro ao verificar: " + error.message, "red");
        });
}

function sortearConjuncao() {
    fetch("http://localhost:8000/sortear")
        .then(res => {
            if (!res.ok) throw new Error("Erro ao sortear conjunção");
            return res.text();
        })
        .then(conjuncao => {
            document.getElementById("conjuncao").textContent = conjuncao;
            document.getElementById("resposta").value = "";
            document.getElementById("resultado").textContent = "";
        })
        .catch(error => {
            mostrarResultado("Erro ao carregar conjunção: " + error.message, "red");
        });
}

function mostrarResultado(texto, cor) {
    const resultado = document.getElementById("resultado");
    resultado.textContent = texto;
    resultado.style.color = cor;
}

// Sorteia a primeira conjunção ao carregar a página
window.onload = sortearConjuncao;


//20250028720444