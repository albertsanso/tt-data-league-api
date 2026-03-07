import yaml, json, pathlib

repo_root = pathlib.Path(__file__).resolve().parents[1]
input_path = repo_root / 'openapi.yaml'
output_path = repo_root / 'openapi.json'

with input_path.open('r', encoding='utf-8') as f:
    data = yaml.safe_load(f)

# write to temp then move to avoid locking issues
tmp = repo_root / 'openapi.json.tmp'
with tmp.open('w', encoding='utf-8') as f:
    json.dump(data, f, indent=2, ensure_ascii=False)
# atomic replace
try:
    tmp.replace(output_path)
    print('Wrote', output_path)
except Exception as e:
    print('Failed to replace:', e)
