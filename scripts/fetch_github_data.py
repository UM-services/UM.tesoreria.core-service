from github import Github
import json
import os

def fetch_github_data():
    # Inicializar cliente de GitHub
    g = Github(os.environ['GITHUB_TOKEN'])
    
    # Obtener el repositorio actual
    repo = g.get_repo(os.environ['GITHUB_REPOSITORY'])
    
    # Obtener issues
    issues_data = []
    for issue in repo.get_issues(state='all'):
        issues_data.append({
            'number': issue.number,
            'title': issue.title,
            'state': issue.state,
            'created_at': issue.created_at.isoformat(),
            'closed_at': issue.closed_at.isoformat() if issue.closed_at else None,
            'labels': [label.name for label in issue.labels],
            'milestone': issue.milestone.title if issue.milestone else None,
            'body': issue.body or ''
        })
    
    # Obtener milestones
    milestones_data = []
    for milestone in repo.get_milestones(state='all'):
        milestone_data = {
            'title': milestone.title,
            'description': milestone.description or '',
            'state': milestone.state,
            'created_at': milestone.created_at.isoformat() if milestone.created_at else None,
            'due_on': milestone.due_on.isoformat() if milestone.due_on else None
        }
        milestones_data.append(milestone_data)
    
    # Guardar datos
    os.makedirs('data', exist_ok=True)
    with open('data/issues.json', 'w') as f:
        json.dump(issues_data, f, indent=2)
    with open('data/milestones.json', 'w') as f:
        json.dump(milestones_data, f, indent=2)

if __name__ == '__main__':
    fetch_github_data() 