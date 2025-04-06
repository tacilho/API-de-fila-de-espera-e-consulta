let intervaloAtualizacao = null;

function formatarTelefone(telefone) {
    // Remove todos os caracteres não numéricos
    const numeros = telefone.replace(/\D/g, '');
    
    // Formata o número no padrão (00) 00000-0000
    if (numeros.length === 11) {
        return `(${numeros.substring(0, 2)}) ${numeros.substring(2, 7)}-${numeros.substring(7)}`;
    }
    return telefone;
}

async function verificarStatus() {
    const telefone = document.getElementById('telefone').value;
    const statusDiv = document.getElementById('status');
    
    if (!telefone) {
        statusDiv.innerHTML = `
            <div class="alert alert-warning">
                <i class="fas fa-exclamation-triangle"></i>
                <div>
                    <h3>Campo obrigatório</h3>
                    <p>Por favor, digite seu número de telefone</p>
                </div>
            </div>
        `;
        return;
    }

    try {
        const response = await fetch(`http://localhost:8080/api/clientes/status/${encodeURIComponent(telefone)}`);
        const data = await response.json();
        
        if (!response.ok) {
            throw new Error(data.error || 'Erro ao verificar status');
        }
        
        atualizarStatus(data);
    } catch (error) {
        console.error('Erro ao verificar status:', error);
        statusDiv.innerHTML = `
            <div class="alert alert-danger">
                <i class="fas fa-exclamation-circle"></i>
                <div>
                    <h3>Erro ao verificar status</h3>
                    <p>${error.message}</p>
                </div>
            </div>
        `;
    }
}

function atualizarStatus(data) {
    const statusDiv = document.getElementById('status');
    let mensagem = '';
    
    if (data.error) {
        mensagem = `
            <div class="alert alert-danger">
                <i class="fas fa-exclamation-circle"></i>
                <div>
                    <h3>Erro</h3>
                    <p>${data.error}</p>
                </div>
            </div>
        `;
    } else {
        switch(data.status) {
            case 'CHAMADO':
                mensagem = `
                    <div class="alert alert-success">
                        <i class="fas fa-check-circle"></i>
                        <div>
                            <h3>Sua mesa está pronta!</h3>
                            <p>Por favor, dirija-se ao balcão</p>
                        </div>
                    </div>
                `;
                break;
            case 'AGUARDANDO':
                mensagem = `
                    <div class="alert alert-info">
                        <i class="fas fa-hourglass-half"></i>
                        <div>
                            <h3>Sua posição na fila: ${data.posicao}</h3>
                            <p>Aguarde sua vez</p>
                        </div>
                    </div>
                `;
                break;
            default:
                mensagem = `
                    <div class="alert alert-secondary">
                        <i class="fas fa-info-circle"></i>
                        <div>
                            <h3>Status desconhecido</h3>
                            <p>Entre em contato com o atendimento</p>
                        </div>
                    </div>
                `;
        }
    }
    
    statusDiv.innerHTML = mensagem;
}

// Adiciona máscara ao campo de telefone
document.getElementById('telefone').addEventListener('input', function(e) {
    let value = e.target.value.replace(/\D/g, '');
    if (value.length > 11) value = value.substring(0, 11);
    
    if (value.length > 0) {
        value = value.replace(/^(\d{2})(\d)/g, '($1) $2');
        if (value.length > 9) {
            value = value.replace(/(\d{5})(\d)/, '$1-$2');
        }
    }
    
    e.target.value = value;
}); 