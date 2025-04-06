// script.js

const API_BASE_URL = 'http://localhost:8080';
const CLIENTES_URL = `${API_BASE_URL}/api/clientes`;
const MESAS_URL = `${API_BASE_URL}/api/mesas`;
const FILA_URL = `${API_BASE_URL}/fila`;

// Função para carregar o total de mesas disponíveis
async function loadTotalMesasDisponiveis() {
    try {
        const response = await fetch(`${MESAS_URL}/disponiveis/count`);
        if (!response.ok) {
            throw new Error('Erro ao carregar total de mesas disponíveis');
        }
        const data = await response.json();
        document.getElementById('totalMesas').textContent = data;
    } catch (error) {
        console.error('Erro ao carregar total de mesas disponíveis:', error);
    }
}

// Função para carregar a fila de espera
async function loadFila() {
    try {
        const filaResponse = await fetch(FILA_URL);
        if (!filaResponse.ok) {
            throw new Error('Erro ao carregar fila');
        }
        const fila = await filaResponse.json();
        console.log('Dados da fila:', fila); // Para depuração

        const mesasResponse = await fetch(MESAS_URL);
        if (!mesasResponse.ok) {
            throw new Error('Erro ao carregar mesas');
        }
        const mesas = await mesasResponse.json();
        console.log('Dados das mesas:', mesas); // Para depuração

        const mesasDisponiveis = mesas.filter(m => m.status === 'DISPONIVEL');
        displayFila(fila, mesasDisponiveis);
        loadTotalMesasDisponiveis();
    } catch (error) {
        console.error('Erro ao carregar fila ou mesas:', error);
        alert(error.message);
    }
}

// Função para calcular o tempo de espera em minutos
function calcularTempoEspera(horaEntrada) {
    const agora = new Date();
    const entrada = new Date(horaEntrada);
    const diffMs = agora - entrada;
    return Math.floor(diffMs / 1000 / 60); // Converte para minutos
}

// Função para formatar o tempo de espera
function formatarTempoEspera(minutos) {
    if (minutos < 60) {
        return `${minutos} min`;
    } else {
        const horas = Math.floor(minutos / 60);
        const min = minutos % 60;
        return `${horas}h ${min}min`;
    }
}

// Função para exibir a fila na tela
function displayFila(fila, mesasDisponiveis) {
    const filaList = document.getElementById('filaList');
    filaList.innerHTML = '';

    if (fila.length === 0) {
        filaList.innerHTML = `
            <div class="empty-state">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <line x1="12" y1="5" x2="12" y2="19"></line>
                    <line x1="5" y1="12" x2="19" y2="12"></line>
                </svg>
                <p>Não há clientes na fila de espera</p>
                <small>Adicione clientes usando o formulário</small>
            </div>
        `;
        return;
    }

    fila.forEach((cliente) => {
        const filaDiv = document.createElement('div');
        filaDiv.className = 'queue-item';

        const tempoEspera = calcularTempoEspera(cliente.horaEntrada);
        const tempoFormatado = formatarTempoEspera(tempoEspera);

        // Posição na fila
        const posicao = document.createElement('div');
        posicao.className = 'queue-position';
        posicao.textContent = cliente.posicao || '-';
        filaDiv.appendChild(posicao);

        // Nome do cliente
        const nome = document.createElement('div');
        nome.className = 'queue-name';
        nome.textContent = cliente.nome;
        filaDiv.appendChild(nome);

        // Detalhes do cliente
        const detalhes = document.createElement('div');
        detalhes.className = 'queue-details';

        // Pessoas
        const pessoasDetail = document.createElement('div');
        pessoasDetail.className = 'queue-detail';
        pessoasDetail.innerHTML = `
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                <circle cx="9" cy="7" r="4"></circle>
                <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
                <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
            </svg>
            ${cliente.pessoas} pessoa(s)
        `;
        detalhes.appendChild(pessoasDetail);

        // Telefone
        const telefoneDetail = document.createElement('div');
        telefoneDetail.className = 'queue-detail';
        telefoneDetail.innerHTML = `
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"></path>
            </svg>
            ${cliente.telefone}
        `;
        detalhes.appendChild(telefoneDetail);

        // Tempo de espera
        const tempoDetail = document.createElement('div');
        tempoDetail.className = 'queue-detail';
        tempoDetail.innerHTML = `
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="12" cy="12" r="10"></circle>
                <polyline points="12 6 12 12 16 14"></polyline>
            </svg>
            Espera: ${tempoFormatado}
        `;
        detalhes.appendChild(tempoDetail);

        filaDiv.appendChild(detalhes);

        // Observações (se houver)
        if (cliente.observacoes) {
            const observacoesElement = document.createElement('div');
            observacoesElement.className = 'queue-notes';
            observacoesElement.textContent = `Observações: ${cliente.observacoes}`;
            filaDiv.appendChild(observacoesElement);
        }

        // Verifica se há mesa disponível compatível
        const mesaCompativel = mesasDisponiveis
            .filter(m => {
                const capacidadeTotal = m.capacidadeAgrupada || m.capacidade
                return capacidadeTotal >= cliente.pessoas
            })
            .sort((a, b) => {
                const capacidadeA = a.capacidadeAgrupada || a.capacidade
                const capacidadeB = b.capacidadeAgrupada || b.capacidade
                return capacidadeB - capacidadeA
            })
            .find(() => true);

        if (mesaCompativel && cliente.status === 'AGUARDANDO') {
            const chamarBtn = document.createElement('button');
            chamarBtn.className = 'call-btn';
            chamarBtn.innerHTML = `
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"></path>
                </svg>
                Chamar Cliente
            `;
            chamarBtn.onclick = () => chamarCliente(cliente, mesaCompativel);
            filaDiv.appendChild(chamarBtn);
        }

        filaList.appendChild(filaDiv);
    });
}

// Função para adicionar cliente à fila
async function addCliente(nome, telefone, pessoas, prioridade, observacoes) {
    try {
        // Validações básicas
        if (!nome || !telefone || !pessoas) {
            throw new Error('Nome, telefone e número de pessoas são obrigatórios');
        }

        // Limpa o telefone (remove caracteres não numéricos)
        const telefoneLimpo = telefone.replace(/\D/g, '');
        
        // Validação do número de pessoas
        const pessoasInt = parseInt(pessoas);
        if (isNaN(pessoasInt) || pessoasInt <= 0) {
            alert('Número de pessoas deve ser maior que zero');
            return;
        }
        
        // Validação da prioridade
        let prioridadeInt = 0;
        if (prioridade) {
            prioridadeInt = parseInt(prioridade);
            if (isNaN(prioridadeInt) || prioridadeInt < 0 || prioridadeInt > 10) {
                alert('Prioridade deve ser um número entre 0 e 10');
                return;
            }
        }
        
        // Envia os dados para o backend
        const response = await fetch(CLIENTES_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                nome: nome.trim(),
                telefone: telefoneLimpo,
                pessoas: pessoasInt,
                prioridade: prioridadeInt,
                posicao: 1,
                status: 'AGUARDANDO',
                observacoes: observacoes ? observacoes.trim() : null
            })
        });
        
        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Erro ao adicionar cliente');
        }
        
        // Limpa o formulário
        document.getElementById('filaForm').reset();
        
        // Recarrega a fila
        await loadFila();
        
    } catch (error) {
        console.error('Erro ao adicionar cliente:', error);
        alert(error.message || 'Erro ao adicionar cliente');
    }
}

// Função para chamar cliente e alocar mesa
async function chamarCliente(cliente, mesa) {
    try {
        // Atualiza o status do cliente para ATENDIDO
        const clienteResponse = await fetch(`${CLIENTES_URL}/${cliente.id}/status`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                status: 'ATENDIDO'
            })
        });

        if (!clienteResponse.ok) {
            const errorData = await clienteResponse.json();
            throw new Error(errorData.message || 'Erro ao atualizar status do cliente');
        }

        // Atualiza o status da mesa para OCUPADA
        const mesaResponse = await fetch(`${MESAS_URL}/${mesa.id}/status`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                status: 'OCUPADA'
            })
        });

        if (!mesaResponse.ok) {
            const errorData = await mesaResponse.json();
            throw new Error(errorData.message || 'Erro ao atualizar status da mesa');
        }

        // Recarrega a fila e as mesas
        await loadFila();
        
    } catch (error) {
        console.error('Erro ao chamar cliente:', error);
        alert(error.message || 'Erro ao chamar cliente');
    }
}

// Evento de submit do formulário
document.getElementById('filaForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const nome = document.getElementById('nome').value;
    const telefone = document.getElementById('telefone').value;
    const pessoas = parseInt(document.getElementById('pessoas').value);
    const prioridade = parseInt(document.getElementById('prioridade').value) || 0;
    const observacoes = document.getElementById('observacoes').value;

    await addCliente(nome, telefone, pessoas, prioridade, observacoes);

    document.getElementById('filaForm').reset();
});

// Carrega a fila ao iniciar
document.addEventListener('DOMContentLoaded', loadFila);

// Atualiza a fila a cada 10 segundos
setInterval(loadFila, 10000);