const apiUrl = "http://localhost:8080/api/mesas" // Ajuste para /mesas/all se criou o novo endpoint

// Import Axios (assumes you're using a module bundler or have it globally available)
// If using a module bundler (like Webpack, Parcel, etc.):
// import axios from 'axios';

// If you're not using a module bundler, ensure Axios is included in your HTML via a <script> tag.
// In that case, Axios is globally available and you don't need to declare it here.
// For this example, let's assume Axios is included via a <script> tag in the HTML.

// Função para carregar todas as mesas
async function loadMesas() {
  try {
    const response = await axios.get(apiUrl)
    const mesas = response.data
    console.log("Mesas retornadas:", mesas) // Adiciona log para depuração
    displayMesas(mesas)
  } catch (error) {
    console.error("Erro ao carregar mesas:", error)
  }
}

// Função para exibir as mesas na tela
function displayMesas(mesas) {
  const mesasList = document.getElementById("mesasList")
  mesasList.innerHTML = ""

  if (mesas.length === 0) {
    mesasList.innerHTML = `
            <div class="empty-state">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
                    <line x1="3" y1="9" x2="21" y2="9"></line>
                    <line x1="9" y1="21" x2="9" y2="9"></line>
                </svg>
                <p>Nenhuma mesa encontrada.</p>
                <small>Adicione mesas usando o formulário</small>
            </div>
        `
    return
  }

  mesas.forEach((mesa) => {
    const mesaDiv = document.createElement("div")
    mesaDiv.className = "mesa-item"

    const mesaInfo = document.createElement("div")
    mesaInfo.className = "mesa-info"

    // Número da mesa
    const mesaNumero = document.createElement("div")
    mesaNumero.className = "mesa-numero"
    mesaNumero.textContent = mesa.numero

    // Detalhes da mesa
    const mesaDetalhes = document.createElement("div")
    mesaDetalhes.className = "mesa-detalhes"

    // Nome da mesa
    const mesaNome = document.createElement("div")
    mesaNome.className = "mesa-nome"
    mesaNome.textContent = `Mesa ${mesa.numero}`

    // Capacidade
    const mesaCapacidade = document.createElement("div")
    mesaCapacidade.className = "mesa-capacidade"
    const capacidadeTotal = mesa.capacidadeAgrupada || mesa.capacidade
    mesaCapacidade.innerHTML = `
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                <circle cx="9" cy="7" r="4"></circle>
                <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
                <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
            </svg>
            Capacidade: ${capacidadeTotal} pessoa(s)
            ${mesa.capacidadeAgrupada ? `(Agrupada)` : ''}
        `

    // Status
    const mesaStatus = document.createElement("div")
    mesaStatus.className = `mesa-status status-${mesa.status.toLowerCase()}`
    mesaStatus.textContent = mesa.status === "DISPONIVEL" ? "Disponível" : "Ocupada"

    mesaDetalhes.appendChild(mesaNome)
    mesaDetalhes.appendChild(mesaCapacidade)
    mesaDetalhes.appendChild(mesaStatus)

    mesaInfo.appendChild(mesaNumero)
    mesaInfo.appendChild(mesaDetalhes)

    // Select para mudar status
    const statusSelect = document.createElement("select")
    statusSelect.innerHTML = `
            <option value="DISPONIVEL" ${mesa.status === "DISPONIVEL" ? "selected" : ""}>Disponível</option>
            <option value="OCUPADA" ${mesa.status === "OCUPADA" ? "selected" : ""}>Ocupada</option>
        `
    statusSelect.onchange = () => updateMesaStatus(mesa.id, statusSelect.value)

    // Botões de agrupamento
    const botoesAgrupamento = document.createElement("div")
    botoesAgrupamento.className = "mesa-botoes"

    if (mesa.status === "DISPONIVEL" && !mesa.mesaAgrupada) {
        const agruparBtn = document.createElement("button")
        agruparBtn.className = "agrupar-btn"
        agruparBtn.textContent = "Agrupar"
        agruparBtn.onclick = () => mostrarModalAgrupamento(mesa)
        botoesAgrupamento.appendChild(agruparBtn)
    }

    if (mesa.capacidadeAgrupada) {
        const desagruparBtn = document.createElement("button")
        desagruparBtn.className = "desagrupar-btn"
        desagruparBtn.textContent = "Desagrupar"
        desagruparBtn.onclick = () => desagruparMesas(mesa.id)
        botoesAgrupamento.appendChild(desagruparBtn)
    }

    mesaDiv.appendChild(mesaInfo)
    mesaDiv.appendChild(statusSelect)
    mesaDiv.appendChild(botoesAgrupamento)
    mesasList.appendChild(mesaDiv)
  })
}

// Função para adicionar uma nova mesa
async function addMesa(numero, capacidade) {
  try {
    const response = await axios.post(apiUrl, {
      numero: numero,
      capacidade: capacidade,
      status: "DISPONIVEL", // Status inicial
    })
    loadMesas() // Recarrega a lista após adicionar
  } catch (error) {
    console.error("Erro ao adicionar mesa:", error)
  }
}

// Função para atualizar o status da mesa
async function updateMesaStatus(id, status) {
  try {
    if (status === "DISPONIVEL") {
      await axios.post(`${apiUrl}/${id}/liberar`)
    } else if (status === "OCUPADA") {
      await axios.post(`${apiUrl}/${id}/alocar`)
    }
    loadMesas() // Recarrega a lista após atualizar
  } catch (error) {
    console.error("Erro ao atualizar status da mesa:", error)
    const errorMessage = error.response?.data?.message || error.message || "Erro desconhecido ao atualizar status da mesa"
    alert(`Erro ao atualizar status da mesa: ${errorMessage}`)
    loadMesas() // Recarrega para garantir que o estado visual está sincronizado
  }
}

// Função para mostrar modal de agrupamento
function mostrarModalAgrupamento(mesaPrincipal) {
  const modal = document.createElement("div")
  modal.className = "modal"
  modal.innerHTML = `
        <div class="modal-content">
            <h2>Agrupar Mesa ${mesaPrincipal.numero}</h2>
            <div id="mesasDisponiveisAgrupamento"></div>
            <button onclick="fecharModal()">Cancelar</button>
        </div>
    `
  document.body.appendChild(modal)

  // Carrega mesas disponíveis para agrupamento
  carregarMesasDisponiveisAgrupamento(mesaPrincipal.id)
}

// Função para carregar mesas disponíveis para agrupamento
async function carregarMesasDisponiveisAgrupamento(mesaPrincipalId) {
  try {
    const response = await axios.get(`${apiUrl}/disponiveis-agrupamento`)
    const mesas = response.data.filter(m => m.id !== mesaPrincipalId)
    const container = document.getElementById("mesasDisponiveisAgrupamento")
    container.innerHTML = ""

    mesas.forEach(mesa => {
      const mesaDiv = document.createElement("div")
      mesaDiv.className = "mesa-agrupamento-item"
      mesaDiv.innerHTML = `
                <span>Mesa ${mesa.numero} (${mesa.capacidade} pessoas)</span>
                <button onclick="agruparMesas(${mesaPrincipalId}, ${mesa.id})">Agrupar</button>
            `
      container.appendChild(mesaDiv)
    })
  } catch (error) {
    console.error("Erro ao carregar mesas para agrupamento:", error)
  }
}

// Função para agrupar mesas
async function agruparMesas(mesaPrincipalId, mesaSecundariaId) {
  try {
    await axios.post(`${apiUrl}/agrupar?mesaPrincipalId=${mesaPrincipalId}&mesaSecundariaId=${mesaSecundariaId}`)
    fecharModal()
    loadMesas()
  } catch (error) {
    console.error("Erro ao agrupar mesas:", error)
    alert("Erro ao agrupar mesas: " + error.response?.data || error.message)
  }
}

// Função para desagrupar mesas
async function desagruparMesas(mesaPrincipalId) {
  try {
    await axios.post(`${apiUrl}/${mesaPrincipalId}/desagrupar`)
    loadMesas()
  } catch (error) {
    console.error("Erro ao desagrupar mesas:", error)
    const errorMessage = error.response?.data?.message || error.message || "Erro desconhecido ao desagrupar mesas"
    alert(`Erro ao desagrupar mesas: ${errorMessage}`)
  }
}

// Função para fechar modal
function fecharModal() {
  const modal = document.querySelector(".modal")
  if (modal) {
    modal.remove()
  }
}

// Evento de submit do formulário
document.getElementById("mesaForm").addEventListener("submit", async (e) => {
  e.preventDefault()

  const numero = document.getElementById("numero").value
  const capacidade = document.getElementById("capacidade").value

  await addMesa(Number.parseInt(numero), Number.parseInt(capacidade))

  // Limpa o formulário
  document.getElementById("mesaForm").reset()
})

// Carrega as mesas ao iniciar
document.addEventListener("DOMContentLoaded", loadMesas)

