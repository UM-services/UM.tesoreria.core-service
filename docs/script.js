document.getElementById('update-date').textContent = new Date().toLocaleString('es-AR');

// --- Diagramas de Secuencia dinámicos ---
fetch('.')
  .then(response => response.text())
  .then(html => {
    // Extrae nombres de archivos sequence-*.mmd del listado HTML
    const regex = /href=\"(sequence-[^\"]+\.mmd)\"/g;
    let match;
    const files = [];
    while ((match = regex.exec(html)) !== null) {
      files.push(match[1]);
    }
    if (files.length > 0) {
      let content = '<h2>🔄 Diagramas de Secuencia</h2>';
      files.forEach(file => {
        fetch(file)
          .then(r => r.text())
          .then(data => {
            // Validación básica de sintaxis Mermaid
            const valid = /^sequenceDiagram/.test(data.trim());
            content += `<div class='section'><h3>${file.replace('sequence-','').replace('.mmd','').replace(/-/g,' ').replace(/\b\w/g, l => l.toUpperCase())}</h3>`;
            if (valid) {
              content += `<div class='mermaid'>${data}</div>`;
            } else {
              content += `<div class='warn'>⚠️ El archivo <b>${file}</b> no contiene un diagrama Mermaid válido.</div>`;
            }
            content += '</div>';
            document.getElementById('sequence-diagrams').innerHTML = content;
            mermaid.init(undefined, document.getElementById('sequence-diagrams').querySelectorAll('.mermaid'));
          });
      });
    } else {
      document.getElementById('sequence-diagrams').innerHTML = '<div class="warn">No se encontraron diagramas de secuencia.</div>';
    }
  });

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
{ id: 'sequence-diagram', file: 'sequence-example.mmd', title: '🔄 Ejemplo de Diagrama de Secuencia' },
{ id: 'erd-diagram', file: 'entities.mmd', title: '🗃️ Diagrama de Entidad-Relación (Simplificado)' },
{ id: 'deployment-diagram', file: 'deployment.mmd', title: '🚀 Diagrama de Despliegue' }
];
diagrams.forEach(diag => {
fetch(diag.file)
  .then(response => {
  if (!response.ok) throw new Error('missing');
  return response.text();
  })
  .then(data => {
  // Validación básica de sintaxis Mermaid (solo si hay contenido y empieza con un tipo válido)
  const valid = /^(flowchart|sequenceDiagram|erDiagram|classDiagram|stateDiagram|gantt|pie|journey|requirementDiagram|gitGraph|mindmap|timeline|quadrantChart)/.test(data.trim());
  const container = document.getElementById(diag.id);
  if (valid) {
    container.innerHTML = `<h2>${diag.title}</h2><div class="mermaid">${data}</div>`;
    mermaid.init(undefined, container.querySelector('.mermaid'));
  } else {
    container.innerHTML = `<div class="warn">⚠️ El archivo <b>${file}</b> existe pero no contiene un diagrama Mermaid válido.</div>`;
  }
  })
  .catch(() => {
  const container = document.getElementById(diag.id);
  container.innerHTML = `<div class="warn">⚠️ El archivo <b>${diag.file}</b> no se encuentra o no pudo cargarse.</div>`;
  });
});
