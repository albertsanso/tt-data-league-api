#!/usr/bin/env python3
"""
OpenAPI 3.0.3 specification generator for tt-data-league-api
Parses Java controllers and DTOs to generate a comprehensive OpenAPI spec.
"""

import os
import re
import json
from pathlib import Path
from typing import Dict, List, Any, Optional, Set
import yaml

class OpenAPIGenerator:
    def __init__(self):
        self.paths = {}
        self.schemas = {}
        self.tags_seen = set()
        self.dto_types = {}
        self.controller_annotations = {}

    def generate(self, project_root: str) -> Dict[str, Any]:
        """Generate complete OpenAPI spec"""
        # Scan DTOs first to build schema info
        self.scan_dtos(os.path.join(project_root, 'tt-data-league-api-rest'))

        # Scan custom OpenAPI controller annotations for base paths and tags
        self.scan_custom_controller_annotations(os.path.join(project_root, 'tt-data-league-api-rest'))

        # Scan controllers
        self.scan_controllers(os.path.join(project_root, 'tt-data-league-api-rest'))
        self.scan_controllers(os.path.join(project_root, 'tt-data-league-api-runtime'))

        # Build OpenAPI spec
        spec = {
            'openapi': '3.0.3',
            'info': {
                'title': 'tt-data-league-api',
                'version': 'v1',
                'description': 'OpenAPI 3 specification inferred from the `tt-data-league-api-rest` module.\nThis file was generated from controller and DTO records and includes\nexample payloads and required properties where applicable.'
            },
            'servers': [
                {
                    'url': 'http://localhost:8080/api/v1',
                    'description': 'Local development server (inferred)'
                }
            ],
            'tags': sorted([
                {'name': tag, 'description': self._get_tag_description(tag)}
                for tag in self.tags_seen
            ], key=lambda x: x['name']),
            'paths': self.paths,
            'components': {
                'schemas': self.schemas,
                'securitySchemes': {
                    'bearerAuth': {
                        'type': 'http',
                        'scheme': 'bearer',
                        'bearerFormat': 'JWT'
                    }
                }
            },
            'security': [
                {'bearerAuth': []}
            ]
        }

        return spec

    def scan_custom_controller_annotations(self, rest_module: str):
        """Scan custom @*OpenAPIv1Controller annotations for base path/tag metadata"""
        java_dir = os.path.join(rest_module, 'src/main/java/org/cttelsamicsterrassa/data/api')
        if not os.path.exists(java_dir):
            return

        for root, _, files in os.walk(java_dir):
            for file in files:
                if not file.endswith('OpenAPIv1Controller.java'):
                    continue

                filepath = os.path.join(root, file)
                with open(filepath, 'r', encoding='utf-8') as f:
                    content = f.read()

                annotation_name_match = re.search(r'public\s+@interface\s+(\w+)', content)
                if not annotation_name_match:
                    continue

                annotation_name = annotation_name_match.group(1)
                mapping_match = re.search(r'@RequestMapping\(\s*API_BASE_PATH_V1\s*\+\s*"([^"]+)"\s*\)', content)
                tag_match = re.search(r'@Tag\(\s*name\s*=\s*"([^"]+)"(?:\s*,\s*description\s*=\s*"([^"]*)")?', content)

                base_path = mapping_match.group(1) if mapping_match else ''
                tag_name = tag_match.group(1) if tag_match else self._annotation_to_tag(annotation_name)
                tag_description = tag_match.group(2) if tag_match and tag_match.group(2) else self._get_tag_description(tag_name)

                if tag_name:
                    self.tags_seen.add(tag_name)

                self.controller_annotations[annotation_name] = {
                    'base_path': base_path,
                    'tag_name': tag_name,
                    'tag_description': tag_description
                }

    def _get_tag_description(self, tag: str) -> str:
        """Get description for a tag"""
        descriptions = {
            'Club API': 'Endpoints for managing clubs in the table tennis league',
            'Club Member API': 'Endpoints for managing club members in the table tennis league',
            'Season Player API': 'Endpoints for managing season players in the table tennis league',
            'Match API': 'Endpoints for managing matches in the table tennis league',
            'Season Player Result API': 'Endpoints for managing season player results in the table tennis league',
            'Auth API': 'Endpoints for authentication'
        }
        return descriptions.get(tag, '')

    def scan_dtos(self, rest_module: str):
        """Scan all DTO files to understand their structure"""
        dto_dirs = [
            os.path.join(rest_module, 'src/main/java/org/cttelsamicsterrassa/data/api/rest')
        ]

        for dto_dir in dto_dirs:
            if os.path.exists(dto_dir):
                self._scan_dtos_recursive(dto_dir)

    def _scan_dtos_recursive(self, directory: str):
        """Recursively scan for DTO files"""
        for root, dirs, files in os.walk(directory):
            for file in files:
                if file.endswith('Dto.java') or file.endswith('Request.java') or file.endswith('Response.java'):
                    filepath = os.path.join(root, file)
                    self._parse_dto_file(filepath)

    def _parse_dto_file(self, filepath: str):
        """Parse a single DTO file and extract schema information"""
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()

        class_name = os.path.splitext(os.path.basename(filepath))[0]

        # Extract record fields
        record_pattern = r'public record\s+(\w+)\((.*?)\)'
        match = re.search(record_pattern, content, re.DOTALL)

        if match:
            class_name = match.group(1)
            fields_str = match.group(2)
            fields = self._parse_record_fields(fields_str)
            schema = self._build_schema_from_fields(class_name, fields, content)
            self.schemas[class_name] = schema
            self.dto_types[class_name] = fields
        else:
            # Check for regular class
            class_pattern = r'public class\s+(\w+)'
            match = re.search(class_pattern, content)
            if match:
                class_name = match.group(1)
                fields = self._extract_class_fields(content)
                schema = self._build_schema_from_fields(class_name, fields, content)
                self.schemas[class_name] = schema
                self.dto_types[class_name] = fields

    def _parse_record_fields(self, fields_str: str) -> Dict[str, str]:
        """Parse record field declarations"""
        fields = {}
        # Split by comma, but be careful about nested generics
        field_pattern = r'(\w+)\s+(\w+)'
        for match in re.finditer(field_pattern, fields_str):
            field_type = match.group(1)
            field_name = match.group(2)
            fields[field_name] = field_type
        return fields

    def _extract_class_fields(self, content: str) -> Dict[str, str]:
        """Extract fields from a regular Java class"""
        fields = {}
        field_pattern = r'(?:private|public)\s+(\w+(?:<[^>]+>)?)\s+(\w+)'
        for match in re.finditer(field_pattern, content):
            field_type = match.group(1)
            field_name = match.group(2)
            if not field_name.startswith('$'):  # Skip synthetic fields
                fields[field_name] = field_type
        return fields

    def _build_schema_from_fields(self, class_name: str, fields: Dict[str, str], content: str) -> Dict[str, Any]:
        """Build OpenAPI schema from DTO fields"""
        schema = {
            'type': 'object',
            'properties': {}
        }

        required_fields = []

        for field_name, field_type in fields.items():
            field_schema = self._get_field_schema(field_type, content)
            schema['properties'][field_name] = field_schema

            # Try to detect if field is required
            if field_type not in ['List', 'Optional'] and 'java.util.Optional' not in content:
                required_fields.append(field_name)

        if required_fields:
            schema['required'] = required_fields

        return schema

    def _get_field_schema(self, field_type: str, context: str = '') -> Dict[str, Any]:
        """Get OpenAPI schema for a field type"""
        # Handle UUID
        if field_type == 'UUID':
            return {'type': 'string', 'format': 'uuid'}

        # Handle primitives
        if field_type == 'String':
            return {'type': 'string'}
        elif field_type in ['int', 'Integer']:
            return {'type': 'integer'}
        elif field_type in ['long', 'Long']:
            return {'type': 'integer', 'format': 'int64'}
        elif field_type in ['boolean', 'Boolean']:
            return {'type': 'boolean'}
        elif field_type in ['double', 'Double', 'float', 'Float']:
            return {'type': 'number'}

        # Handle List
        if field_type.startswith('List'):
            inner_type = re.search(r'List<(\w+)>', field_type)
            if inner_type:
                inner = inner_type.group(1)
                if inner == 'String':
                    return {'type': 'array', 'items': {'type': 'string'}}
                else:
                    return {'type': 'array', 'items': {'$ref': f'#/components/schemas/{inner}'}}
            return {'type': 'array', 'items': {}}

        # Handle DTO reference
        if field_type.endswith('Dto'):
            return {'$ref': f'#/components/schemas/{field_type}'}

        # Default
        return {'type': 'string'}

    def scan_controllers(self, module: str):
        """Scan controller files"""
        java_dir = os.path.join(module, 'src/main/java/org/cttelsamicsterrassa/data/api')

        if os.path.exists(java_dir):
            for root, dirs, files in os.walk(java_dir):
                for file in files:
                    if file.endswith('Controller.java'):
                        filepath = os.path.join(root, file)
                        self._parse_controller(filepath)

    def _parse_controller(self, filepath: str):
        """Parse a controller file"""
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()

        # Skip custom annotation definition files (handled separately)
        if 'public @interface' in content:
            return

        # Extract controller annotation/path
        controller_path = ''
        controller_tag = None

        # Check for custom OpenAPIv1Controller annotation usage
        annotation_pattern = r'@(\w+OpenAPIv1Controller)'
        annotation_match = re.search(annotation_pattern, content)
        if annotation_match:
            annotation_name = annotation_match.group(1)
            annotation_meta = self.controller_annotations.get(annotation_name, {})
            controller_path = annotation_meta.get('base_path', '')
            controller_tag = annotation_meta.get('tag_name', self._annotation_to_tag(annotation_name))
            if controller_tag:
                self.tags_seen.add(controller_tag)

        # Check for @RequestMapping
        if not controller_path:
            mapping_pattern = r'@RequestMapping\(\s*API_BASE_PATH_V1\s*\+\s*"([^"]+)"\s*\)'
            mapping_match = re.search(mapping_pattern, content)
            if mapping_match:
                controller_path = mapping_match.group(1)

        if not controller_tag:
            class_name_match = re.search(r'public\s+class\s+(\w+Controller)', content)
            if class_name_match:
                base_name = class_name_match.group(1).replace('Controller', '')
                controller_tag = f'{base_name} API' if base_name else 'API'
            else:
                controller_tag = 'API'
            self.tags_seen.add(controller_tag)

        # Extract methods
        method_pattern = r'@(GetMapping|PostMapping|PutMapping|DeleteMapping)(?:\(([^)]*)\))?'
        method_matches = list(re.finditer(method_pattern, content, re.DOTALL))

        for idx, match in enumerate(method_matches):
            method_type = match.group(1)
            annotation_args = (match.group(2) or '').strip()
            method_path = ''
            if annotation_args:
                path_match = re.search(r'["\']([^"\']*)["\']', annotation_args)
                if path_match:
                    method_path = path_match.group(1)

            # Get the full method context
            method_start = match.start()
            method_end = method_matches[idx + 1].start() if idx + 1 < len(method_matches) else len(content)
            method_chunk = content[method_start:method_end]

            # Find the method signature
            method_sig_pattern = r'public\s+([\w<>\[\],.?\s]+?)\s+(\w+)\s*\('
            method_sig_match = re.search(method_sig_pattern, method_chunk)
            if method_sig_match:
                # Extract operation details
                op_summary = self._extract_operation_summary(method_chunk)
                op_description = self._extract_operation_description(method_chunk)

                # Build full path
                full_path = controller_path + method_path
                if not full_path.startswith('/'):
                    full_path = '/' + full_path

                # Map HTTP method
                http_method = method_type.replace('Mapping', '').lower()

                # Extract parameters and response
                params = self._extract_parameters(method_chunk)
                response_type = method_sig_match.group(1).strip().replace('\n', ' ')

                # Create operation
                operation = {
                    'summary': op_summary,
                    'tags': [controller_tag or 'API'],
                    'parameters': params if params else [],
                    'responses': self._get_responses(response_type)
                }

                if op_description:
                    operation['description'] = op_description

                if full_path not in self.paths:
                    self.paths[full_path] = {}

                self.paths[full_path][http_method] = operation

    def _annotation_to_tag(self, annotation: str) -> str:
        """Convert controller annotation to tag name"""
        # ClubOpenAPIv1Controller -> Club API
        match = re.search(r'(\w+)OpenAPIv1Controller', annotation)
        if match:
            base_name = match.group(1)
            # Split camelCase
            parts = re.findall(r'[A-Z]?[a-z]+|[A-Z]+(?=[A-Z][a-z]|\d|\W|$)', base_name)
            return ' '.join(parts) + ' API'
        return 'API'

    def _extract_operation_summary(self, method_content: str) -> str:
        """Extract @Operation summary"""
        summary_pattern = r'@Operation\(summary\s*=\s*"([^"]+)"'
        match = re.search(summary_pattern, method_content)
        if match:
            return match.group(1)
        return 'Operation'

    def _extract_operation_description(self, method_content: str) -> Optional[str]:
        """Extract @Operation description"""
        desc_pattern = r'description\s*=\s*"([^"]+)"'
        match = re.search(desc_pattern, method_content)
        if match:
            return match.group(1)
        return None

    def _extract_parameters(self, method_content: str) -> List[Dict[str, Any]]:
        """Extract method parameters"""
        params = []

        # @RequestParam
        req_param_pattern = r'@RequestParam\s*(?:\(\s*(?:value\s*=\s*)?["\']([^"\']+)["\']?\))?[^)]*\)\s*([\w<>\[\],.?\s]+?)\s+(\w+)'
        for match in re.finditer(req_param_pattern, method_content):
            param_name = match.group(1) or match.group(3)
            params.append({
                'name': param_name,
                'in': 'query',
                'required': True,
                'schema': {'type': 'string'}
            })

        # @PathVariable
        path_var_pattern = r'@PathVariable\s*(?:\(\s*(?:value\s*=\s*)?["\']([^"\']+)["\']?\))?[^)]*\)\s*([\w<>\[\],.?\s]+?)\s+(\w+)'
        for match in re.finditer(path_var_pattern, method_content):
            param_name = match.group(1) or match.group(3)
            params.append({
                'name': param_name,
                'in': 'path',
                'required': True,
                'schema': {'type': 'string', 'format': 'uuid' if param_name.lower().endswith('id') else 'string'}
            })

        return params

    def _get_responses(self, return_type: str) -> Dict[str, Any]:
        """Build response schema"""
        responses = {
            '200': {
                'description': 'Operation successful',
                'content': {
                    'application/json': {}
                }
            },
            '500': {
                'description': 'Internal server error',
                'content': {
                    'application/json': {
                        'schema': {'$ref': '#/components/schemas/ErrorResponse'}
                    }
                }
            }
        }

        # Extract actual return type
        if 'ResponseEntity' in return_type:
            inner_match = re.search(r'ResponseEntity<(\w+(?:<[^>]+>)?)>', return_type)
            if inner_match:
                inner_type = inner_match.group(1)
                if inner_type != 'Void':
                    if inner_type.startswith('List'):
                        list_type_match = re.search(r'List<(\w+)>', inner_type)
                        if list_type_match:
                            responses['200']['content']['application/json']['schema'] = {
                                'type': 'array',
                                'items': {'$ref': f'#/components/schemas/{list_type_match.group(1)}'}
                            }
                    else:
                        responses['200']['content']['application/json']['schema'] = {
                            '$ref': f'#/components/schemas/{inner_type}'
                        }
                else:
                    del responses['200']['content']
                    responses['204'] = {'description': 'Deleted (no content)'}
        else:
            direct_type = return_type.replace(' ', '')
            if direct_type and direct_type not in ['void', 'Void']:
                # direct DTO/class return types (e.g., MatchDto)
                responses['200']['content']['application/json']['schema'] = {
                    '$ref': f'#/components/schemas/{direct_type}'
                }

        return responses


def main():
    project_root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))

    generator = OpenAPIGenerator()
    spec = generator.generate(project_root)

    # Ensure schemas like ErrorResponse exist
    if 'ErrorResponse' not in spec['components']['schemas']:
        spec['components']['schemas']['ErrorResponse'] = {
            'type': 'object',
            'required': ['code', 'message'],
            'properties': {
                'code': {
                    'type': 'string',
                    'example': 'INTERNAL_ERROR'
                },
                'message': {
                    'type': 'string',
                    'example': 'An unexpected server error occurred'
                },
                'details': {
                    'type': 'object',
                    'additionalProperties': True
                }
            }
        }

    # Save to openapi.yaml
    output_path = os.path.join(project_root, 'openapi.yaml')
    with open(output_path, 'w', encoding='utf-8') as f:
        yaml.dump(spec, f, default_flow_style=False, sort_keys=False, allow_unicode=True)

    print(f'Generated OpenAPI spec: {output_path}')
    print(f'  - {len(spec["paths"])} paths')
    print(f'  - {len(spec["components"]["schemas"])} schemas')
    print(f'  - {len(spec["tags"])} tags')


if __name__ == '__main__':
    main()

