import yaml

data = yaml.safe_load(open('openapi.yaml'))
print('OpenAPI Spec Summary:')
print(f'  Paths: {len(data["paths"])}')
print(f'  Schemas: {len(data["components"]["schemas"])}')
print(f'  Tags: {len(data["tags"])}')
print()
print('API Paths:')
for p in sorted(data['paths'].keys()):
    print(f'    - {p}')

