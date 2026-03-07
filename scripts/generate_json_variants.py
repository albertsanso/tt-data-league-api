import yaml, json, pathlib

repo_root = pathlib.Path(__file__).resolve().parents[1]
input_path = repo_root / 'openapi.yaml'
if not input_path.exists():
    print('openapi.yaml not found')
    raise SystemExit(1)

data = yaml.safe_load(input_path.read_text(encoding='utf-8'))

for name in ['openapi.inferred.json', 'openapi.full.json']:
    out = repo_root / name
    out.write_text(json.dumps(data, indent=2, ensure_ascii=False), encoding='utf-8')
    print('Wrote', out)

