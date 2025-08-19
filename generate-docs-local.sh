#!/bin/bash
set -e

# 1. Configuración
# Reemplaza 'tu_token_de_github' con tu Token de Acceso Personal.
# O, mejor aún, expórtalo como una variable de entorno: export GITHUB_TOKEN="tu_token"
if [ -z "$GITHUB_TOKEN" ]; then
    echo "Error: La variable de entorno GITHUB_TOKEN no está configurada."
    echo "Por favor, ejecute: export GITHUB_TOKEN='tu_token_de_github'"
    exit 1
fi

# Detecta automáticamente el repositorio desde el remote de git
REPO=$(git remote get-url origin | sed -e 's/.*github.com[\/:]//' -e 's/\.git$//')
echo "Repositorio detectado: $REPO"

# 2. Limpieza y Creación de Directorios
echo "Limpiando y creando el directorio 'docs'வதற்காக..."
rm -rf docs
mkdir -p docs

# 3. Generar Árbol de Dependencias (igual que en el pipeline)
echo "Generando árbol de dependencias con Maven..."
mvn dependency:tree -DoutputFile=docs/dependency-tree.txt

# 4. Obtener Datos de la API de GitHub (igual que en el pipeline)
echo "Obteniendo datos del proyecto desde la API de GitHub..."
curl -s -H "Authorization: token $GITHUB_TOKEN" "https://api.github.com/repos/$REPO/milestones" > docs/milestones.json
curl -s -H "Authorization: token $GITHUB_TOKEN" "https://api.github.com/repos/$REPO/issues?state=all&per_page=100" > docs/issues.json
curl -s -H "Authorization: token $GITHUB_TOKEN" "https://api.github.com/repos/$REPO/pulls?state=all&per_page=100" > docs/prs.json

# Copia los diagramas existentes al directorio docs para que puedan ser encontrados por fetch
echo "Copiando diagramas Mermaid a 'docs'வதற்காக..."
cp *.mmd docs/ 2>/dev/null || true

# 5. Generar el archivo HTML (usando el contenido corregido)
echo "Generando docs/index.html..."
cat > docs/index.html << 'EOF'
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>UM Haberes Core Service - Documentación del Proyecto</title>
  <script src="https://cdn.jsdelivr.net/npm/mermaid@10/dist/mermaid.min.js"></script>
  <style>
    body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; line-height: 1.6; color: #333; max-width: 1200px; margin: 0 auto; padding: 20px; background-color: #f5f5f5; }
    a { color: #667eea; text-decoration: none; }
    a:hover { text-decoration: underline; }
    .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 2rem; border-radius: 10px; margin-bottom: 2rem; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
    .section { background: white; padding: 1.5rem; margin-bottom: 1.5rem; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
    .item { background: #f8f9fa; padding: 1rem; margin: 0.5rem 0; border-left: 4px solid #667eea; border-radius: 4px; }
    .label { display: inline-block; padding: 0.25rem 0.5rem; border-radius: 3px; font-size: 0.85rem; margin-right: 0.5rem; color: black; }
    .stats { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem; margin-bottom: 2rem; }
    .stat-card { background: white; padding: 1.5rem; border-radius: 8px; text-align: center; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
    .stat-number { font-size: 2rem; font-weight: bold; color: #667eea; }
    .progress-bar { width: 100%; height: 20px; background: #e9ecef; border-radius: 10px; overflow: hidden; }
    .progress-fill { height: 100%; background: #28a745; transition: width 0.3s ease; }
    pre { background-color: #f8f9fa; border: 1px solid #dee2e6; border-radius: 4px; padding: 1rem; white-space: pre-wrap; word-wrap: break-word; max-height: 400px; overflow-y: auto; }
    .warn { color: #b85c00; background: #fffbe6; border-left: 4px solid #ffc107; padding: 0.5rem 1rem; border-radius: 4px; margin-bottom: 1rem; }
  </style>
</head>
<body>
  <div class="header">
    <h1>📋 UM Haberes Core Service - Documentación del Proyecto</h1>
    <p>Servicio central para la gestión de haberes y liquidaciones de la Universidad.</p>
    <p>Última actualización: <span id="update-date"></span></p>
  </div>
  <div id="stats" class="stats"></div>
  <div id="architecture" class="section"></div>
  <div id="sequence-diagram" class="section"></div>
  <div id="erd-diagram" class="section"></div>
    <div id="deployment-diagram" class="section"></div>
    <div id="sequence-diagrams" class="section"></div>
    <div id="dependencies" class="section">
    <h2>🌳 Grafo de Dependencias</h2>
    <p>El siguiente es el árbol de dependencias completo del proyecto, generado por Maven.</p>
    <pre id="dependency-tree-content">Cargando...</pre>
  </div>
  <div id="milestones" class="section"></div>
  <div id="pull-requests" class="section"></div>
  
  <script>mermaid.initialize({startOnLoad:true});</script>
  <script>
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
  </script>
  <script>
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
        container.innerHTML = `<div class="warn">⚠️ El archivo <b>${diag.file}</b> existe pero no contiene un diagrama Mermaid válido.</div>`;
      }
      })
      .catch(() => {
      const container = document.getElementById(diag.id);
      container.innerHTML = `<div class="warn">⚠️ El archivo <b>${diag.file}</b> no se encuentra o no pudo cargarse.</div>`;
      });
    });
  </script>
</body>
</html>
EOF

echo "¡Proceso completado!"
echo "Abre el archivo 'docs/index.html' en tu navegador para ver el resultado."
