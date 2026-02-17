document.getElementById('update-date').textContent = new Date().toLocaleString('es-AR');

// --- Carga de datos del proyecto (issues, PRs, etc.) ---
Promise.all([
  fetch('dependency-tree.txt').then(res => res.ok ? res.text() : Promise.resolve('Error al cargar dependencias.')),
  fetch('milestones.json').then(res => res.ok ? res.json() : Promise.resolve([])),
  fetch('issues.json').then(res => res.ok ? res.json() : Promise.resolve([])),
  fetch('prs.json').then(res => res.ok ? res.json() : Promise.resolve([]))
]).then(([dependencyData, milestonesData, issuesData, prsData]) => {
  // --- Dependency Tree ---
  document.getElementById('dependency-tree-content').textContent = dependencyData;

  // --- Statistics ---
  const totalIssues = issuesData.length;
  const openIssues = issuesData.filter(i => i.state === 'open').length;
  const closedIssues = totalIssues - openIssues;
  const progress = totalIssues > 0 ? Math.round((closedIssues / totalIssues) * 100) : 0;
  const statsHTML = `
    <div class="stat-card"><div class="stat-number">${totalIssues}</div><div>Total Issues</div></div>
    <div class="stat-card"><div class="stat-number">${openIssues}</div><div>Issues Abiertos</div></div>
    <div class="stat-card"><div class="stat-number">${closedIssues}</div><div>Issues Cerrados</div></div>
    <div class="stat-card"><div class="stat-number">${progress}%</div><div>Progreso</div><div class="progress-bar" style="margin-top: 10px;"><div class="progress-fill" style="width: ${progress}%"></div></div></div>
  `;
  document.getElementById('stats').innerHTML = statsHTML;

  // --- Milestones ---
  let milestonesHTML = '<h2>🎯 Milestones</h2>';
  if (milestonesData.length > 0) {
    milestonesData.forEach(milestone => {
      const milestoneIssues = issuesData.filter(i => i.milestone && i.milestone.id === milestone.id);
      const openCount = milestoneIssues.filter(i => i.state === 'open').length;
      const totalCount = milestoneIssues.length;
      milestonesHTML += `
        <div class="section">
          <h3><a href="${milestone.html_url}" target="_blank">${milestone.title}</a></h3>
          <p>${milestone.description || 'Sin descripción'}</p>
          <p><strong>Fecha límite:</strong> ${milestone.due_on ? new Date(milestone.due_on).toLocaleDateString('es-AR') : 'No definida'}</p>
          <p><strong>Progreso:</strong> ${totalCount - openCount} de ${totalCount} completados</p>
          <div class="progress-bar"><div class="progress-fill" style="width: ${totalCount > 0 ? ((totalCount - openCount) / totalCount * 100) : 0}%"></div></div>
          <h4>Issues:</h4>
          ${milestoneIssues.length > 0 ? milestoneIssues.map(issue => `
            <div class="item">
              <strong><a href="${issue.html_url}" target="_blank">#${issue.number} - ${issue.title}</a></strong>
              ${issue.labels.map(label => `<span class="label" style="background-color: #${label.color};">${label.name}</span>`).join('')}
              <span style="float: right; color: ${issue.state === 'open' ? '#dc3545' : '#28a745'}">${issue.state === 'open' ? '🔴 Abierto' : '✅ Cerrado'}</span>
            </div>
          `).join('') : '<p>No hay issues para este milestone.</p>'}
        </div>
      `;
    });
  } else {
    milestonesHTML += '<div class="warn">No se encontraron milestones.</div>';
  }
  document.getElementById('milestones').innerHTML = milestonesHTML;

  // --- Pull Requests ---
  let prsHTML = '<h2>🔄 Pull Requests</h2>';
  if (prsData.length > 0) {
    prsData.forEach(pr => {
      prsHTML += `
        <div class="item">
          <strong><a href="${pr.html_url}" target="_blank">#${pr.number} - ${pr.title}</a></strong>
          <p style="margin: 0; font-size: 0.9rem;">Creado por: ${pr.user.login}</p>
          <span style="float: right; color: ${pr.state === 'open' ? '#dc3545' : '#28a745'}">${pr.state === 'open' ? '🔴 Abierto' : '✅ Cerrado'}</span>
        </div>
      `;
    });
  } else {
    prsHTML += '<div class="warn">No se encontraron pull requests.</div>';
  }
  document.getElementById('pull-requests').innerHTML = prsHTML;

}).catch(error => {
  console.error("Error al cargar los datos del proyecto:", error);
  document.getElementById('stats').innerHTML = '<div class="warn">No se pudieron cargar las estadísticas y datos del proyecto.</div>';
});

// --- Diagramas Mermaid ---
const diagrams = [
  { id: 'architecture', file: 'architecture.mmd', title: '🏛️ Arquitectura General del Servicio' },
  { id: 'hexagonal-architecture', file: 'hexagonal-architecture.mmd', title: '🔷 Arquitectura Hexagonal - Caso de Uso Curso Cargo Contratado' },
  { id: 'hexagonal-chequeraCuota', file: 'hexagonal-chequeraCuota.mmd', title: '🔷 Arquitectura Hexagonal - Chequera Cuota' },
  { id: 'hexagonal-mercadopago', file: 'hexagonal-mercadopago-context-history.mmd', title: '🔷 Arquitectura Hexagonal - MercadoPago Context History' },
  { id: 'dependencies-diagram', file: 'dependencies.mmd', title: '📦 Diagrama de Dependencias' },
  { id: 'erd-diagram', file: 'entities.mmd', title: '🗃️ Diagrama de Entidad-Relación (Simplificado)' },
  { id: 'deployment-diagram', file: 'deployment.mmd', title: '🚀 Diagrama de Despliegue' },
  { id: 'sequence-diagrams', file: 'sequence-alta-usuario.mmd', title: 'Alta de Usuario' },
  { id: 'sequence-diagrams', file: 'sequence-baja-usuario.mmd', title: 'Baja de Usuario' },
  { id: 'sequence-diagrams', file: 'sequence-chequera-serie-sede.mmd', title: 'Chequera Serie Sede' },
  { id: 'sequence-diagrams', file: 'sequence-comprobante.mmd', title: 'Comprobante' },
  { id: 'sequence-diagrams', file: 'sequence-consulta-articulos.mmd', title: 'Consulta de Artículos' },
  { id: 'sequence-diagrams', file: 'sequence-example.mmd', title: 'Ejemplo de Secuencia' },
  { id: 'sequence-diagrams', file: 'sequence-liquidacion-sueldos.mmd', title: 'Liquidación de Sueldos' },
  { id: 'sequence-diagrams', file: 'sequence-mercadopago-to-change.mmd', title: 'MercadoPago to Change' },
  { id: 'sequence-diagrams', file: 'sequence-movimientos-cuenta.mmd', title: 'Movimientos de Cuenta' },
  { id: 'sequence-diagrams', file: 'sequence-pago-chequera.mmd', title: 'Pago de Chequera' },
  { id: 'sequence-diagrams', file: 'sequence-reemplazo-chequera.mmd', title: 'Reemplazo de Chequera' }
];

const sequenceDiagramsContainer = document.getElementById('sequence-diagrams');
sequenceDiagramsContainer.innerHTML = '<h2>🔄 Diagramas de Secuencia</h2>';

diagrams.forEach(diag => {
  fetch(diag.file)
    .then(response => {
      if (!response.ok) {
        throw new Error(`Network response was not ok for ${diag.file}`);
      }
      return response.text();
    })
    .then(data => {
      let content = data.startsWith('---') ? data.substring(data.indexOf('---', 3) + 3).trim() : data;
      const valid = /^(flowchart|sequenceDiagram|erDiagram|classDiagram|stateDiagram|gantt|pie|journey|requirementDiagram|gitGraph|mindmap|timeline|quadrantChart)/.test(content);
      
      const container = document.getElementById(diag.id);
      const diagramElement = document.createElement('div');
      diagramElement.className = 'section';

      if (valid) {
        diagramElement.innerHTML = `<h3>${diag.title}</h3><div class="mermaid">${content}</div>`;
      } else {
        diagramElement.innerHTML = `<div class="warn">⚠️ El archivo <b>${diag.file}</b> no contiene un diagrama Mermaid válido.</div>`;
      }

      if (diag.id === 'sequence-diagrams') {
        sequenceDiagramsContainer.appendChild(diagramElement);
      } else {
        container.innerHTML = `<h2>${diag.title}</h2>`;
        container.appendChild(diagramElement);
      }
    })
    .catch(() => {
      const container = document.getElementById(diag.id);
      const errorElement = document.createElement('div');
      errorElement.className = 'warn';
      errorElement.innerHTML = `⚠️ El archivo <b>${diag.file}</b> no se encuentra o no pudo cargarse.`;
      
      if (diag.id === 'sequence-diagrams') {
        const diagramElement = document.createElement('div');
        diagramElement.className = 'section';
        diagramElement.appendChild(errorElement);
        sequenceDiagramsContainer.appendChild(diagramElement);
      } else {
        container.innerHTML = `<h2>${diag.title}</h2>`;
        container.appendChild(errorElement);
      }
    });
});

// Render all Mermaid diagrams at once after a short delay
setTimeout(() => {
    mermaid.init(undefined, document.querySelectorAll('.mermaid'));
}, 500);
